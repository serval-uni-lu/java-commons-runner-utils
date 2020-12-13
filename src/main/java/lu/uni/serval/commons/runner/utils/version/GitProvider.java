package lu.uni.serval.commons.runner.utils.version;

import lu.uni.serval.commons.runner.utils.configuration.GitConfiguration;
import lu.uni.serval.commons.runner.utils.configuration.RepositoryConfiguration;
import lu.uni.serval.commons.runner.utils.os.OsUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import tech.ikora.gitloader.exception.InvalidGitRepositoryException;
import tech.ikora.gitloader.git.CommitCollector;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;
import lu.uni.serval.commons.runner.utils.configuration.MavenConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class GitProvider implements VersionProvider {
    private static final Logger logger = LogManager.getLogger(GitProvider.class);

    private final GitConfiguration configuration;
    private final File tmpFolder;
    private final Set<LocalRepository> repositories;

    public GitProvider(GitConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.tmpFolder = OsUtils.getTmpFolder();
        this.repositories = new HashSet<>();
    }

    @Override
    public void clean() throws IOException {
        for(LocalRepository localRepository: repositories){
            if(localRepository == null) continue;
            if(localRepository.getGit() == null) continue;
            if(localRepository.getGit().getRepository() == null) continue;
            localRepository.getGit().getRepository().close();
        }

        FileUtils.forceDelete(this.tmpFolder);
    }

    @Override
    public Iterator<Version> iterator() {
        return new Iterator<Version>() {
            private final Iterator<RepositoryConfiguration> configIterator = configuration.getRepositories().iterator();

            private Iterator<GitCommit> commitIterator = null;
            private LocalRepository repository = null;
            private MavenConfiguration mavenConfiguration = null;

            @Override
            public boolean hasNext() {
                while(commitIterator == null || !commitIterator.hasNext()){
                    if(configIterator.hasNext()){
                        initialize(configIterator.next());
                    }
                    else break;
                }

                return commitIterator != null && commitIterator.hasNext();
            }

            @Override
            public Version next() {
                if(!hasNext()){
                    return null;
                }

                final GitCommit commit = commitIterator.next();
                Version version;

                try {
                    GitUtils.checkout(repository.getGit(), commit.getId());

                    final LocalDateTime dateTime = commit.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                    version = new Version(
                            repository.getRemoteUrl(),
                            repository.getLocation(),
                            dateTime,
                            commit.getId(),
                            commit.getDifference().getFormatted(),
                            mavenConfiguration
                    );
                } catch (GitAPIException | IOException e) {
                    logger.error(String.format("Git API error failed to load commit %s from %s: %s",
                            commit.getId(),
                            repository.getRemoteUrl(),
                            e.getMessage()
                    ));
                    return next();
                }

                return version;
            }

            private void initialize(RepositoryConfiguration repository){
                if(repository.isIgnore()){
                    reset();
                    return;
                }

                try {
                    mavenConfiguration = repository.getProcessConfiguration();

                    final File repositoryFolder = new File(tmpFolder, GitUtils.extractProjectName(repository.getLocation()));

                    logger.info(String.format("Loading repository from %s...", repository.getLocation()));

                    this.repository = GitUtils.loadCurrentRepository(
                            repository.getLocation(),
                            configuration.getToken(),
                            repositoryFolder,
                            repository.getBranch()
                    );

                    repositories.add(this.repository);

                    logger.info("Repository loaded!");

                    commitIterator = new CommitCollector()
                            .forGit(this.repository.getGit())
                            .onBranch(repository.getBranch())
                            .from(repository.getStartDate())
                            .to(repository.getEndDate())
                            .ignoring(repository.getIgnoreCommits())
                            .every(repository.getFrequency())
                            .limit(repository.getMaximumCommitsNumber())
                            .collect().iterator();

                    logger.info("Commits resolved!");
                } catch (InvalidGitRepositoryException | IOException e) {
                    logger.error(String.format("Failed to initialize repository '%s': [%s] %s",
                            repository.getLocation(),
                            e.getClass().getSimpleName(),
                            e.getMessage()
                    ));

                    reset();
                }
            }

            private void reset(){
                this.repository = null;
                this.commitIterator = null;
                this.mavenConfiguration = null;
            }
        };
    }
}

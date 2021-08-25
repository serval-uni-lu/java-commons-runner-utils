package lu.uni.serval.commons.runner.utils.version;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import lu.uni.serval.commons.git.exception.InvalidGitRepositoryException;
import lu.uni.serval.commons.git.utils.CommitCollector;
import lu.uni.serval.commons.git.utils.GitCommit;
import lu.uni.serval.commons.git.utils.GitUtils;
import lu.uni.serval.commons.git.utils.LocalRepository;
import lu.uni.serval.commons.runner.utils.configuration.GitConfiguration;
import lu.uni.serval.commons.runner.utils.configuration.RepositoryConfiguration;
import lu.uni.serval.commons.runner.utils.os.OsUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
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
            if(localRepository != null && localRepository.getGit() != null){
                GitUtils.close(localRepository.getGit(), true);
            }
        }
    }

    @Override
    public Iterator<Version> iterator() {
        return new GitIterator();
    }

    class GitIterator implements Iterator<Version> {
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

                final LocalDateTime dateTime = commit.getDate().atZone(ZoneId.systemDefault()).toLocalDateTime();

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

                logger.printf(Level.INFO,
                        "Loading repository from %s...",
                        repository.getLocation()
                );

                this.repository = GitUtils.loadCurrentRepository(
                        repository.getLocation(),
                        configuration.getToken(),
                        repositoryFolder,
                        repository.getBranch()
                );

                repositories.add(this.repository);

                logger.info("Repository loaded!");

                if(isCherryPick(repository)){
                    commitIterator = new CommitCollector()
                            .forGit(this.repository.getGit())
                            .cherryPick(repository.getCherryPick()).iterator();
                }
                else {
                    commitIterator = new CommitCollector()
                            .forGit(this.repository.getGit())
                            .onBranch(repository.getBranch())
                            .from(repository.getStartDate())
                            .to(repository.getEndDate())
                            .ignoring(repository.getIgnoreCommits())
                            .every(repository.getFrequency())
                            .limit(repository.getMaximumCommitsNumber())
                            .collect().iterator();
                }

                logger.info("Commits resolved!");
            } catch (InvalidGitRepositoryException | IOException | GitAPIException e) {
                logger.error(String.format("Failed to initialize repository '%s': [%s] %s",
                        repository.getLocation(),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                ));

                reset();
            }
        }

        private boolean isCherryPick(RepositoryConfiguration repository){
            return repository.getCherryPick() != null && repository.getCherryPick().length != 0;
        }

        private void reset(){
            this.repository = null;
            this.commitIterator = null;
            this.mavenConfiguration = null;
        }
    }
}

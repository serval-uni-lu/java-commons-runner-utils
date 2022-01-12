package lu.uni.serval.commons.runner.utils.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lu.uni.serval.commons.runner.utils.build.maven.MavenConfiguration;
import lu.uni.serval.commons.runner.utils.configuration.Configuration;
import lu.uni.serval.commons.runner.utils.configuration.GitConfiguration;

public class SimpleConfiguration extends Configuration {
    @JsonProperty(value = "git", required = true)
    GitConfiguration<MavenConfiguration> git;

    public GitConfiguration<MavenConfiguration> getGit() {
        return git;
    }

    public void setGit(GitConfiguration<MavenConfiguration> git) {
        this.git = git;
    }
}

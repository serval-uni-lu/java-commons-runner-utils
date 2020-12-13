package lu.uni.serval.commons.runner.utils.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;

public class GitConfiguration {
    @JsonProperty(value = "repositories", required = true)
    private Set<RepositoryConfiguration> repositories = Collections.emptySet();

    @JsonProperty(value = "token", required = true)
    private String token = "";

    public Set<RepositoryConfiguration> getRepositories() {
        return repositories;
    }

    public void setRepositories(Set<RepositoryConfiguration> repositories) {
        this.repositories = repositories;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}

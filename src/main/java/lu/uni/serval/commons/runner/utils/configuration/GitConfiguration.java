package lu.uni.serval.commons.runner.utils.configuration;

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


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;

public class GitConfiguration<T extends BuildConfiguration> extends Configuration {
    @JsonProperty(value = "repositories", required = true)
    private Set<RepositoryConfiguration<T>> repositories = Collections.emptySet();

    @JsonProperty(value = "token", required = true)
    private String token = "";

    public Set<RepositoryConfiguration<T>> getRepositories() {
        return repositories;
    }

    public void setRepositories(Set<RepositoryConfiguration<T>> repositories) {
        this.repositories = repositories;
        this.repositories.forEach(r -> r.parent = this);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

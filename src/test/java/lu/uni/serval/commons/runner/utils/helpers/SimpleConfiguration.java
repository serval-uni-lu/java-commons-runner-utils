package lu.uni.serval.commons.runner.utils.helpers;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 - 2022 University of Luxembourg
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
        this.git.setParent(this);
    }
}

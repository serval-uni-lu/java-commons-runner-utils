package lu.uni.serval.commons.runner.utils.configuration;

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

public class BuildConfiguration extends Configuration{
    @JsonProperty(value = "run before")
    ScriptConfiguration beforeBuild;
    @JsonProperty(value = "run after")
    ScriptConfiguration afterBuild;

    public ScriptConfiguration getBeforeBuild() {
        return beforeBuild;
    }

    public void setBeforeBuild(ScriptConfiguration beforeBuild) {
        this.beforeBuild = beforeBuild;
        this.beforeBuild.parent = this;
    }

    public ScriptConfiguration getAfterBuild() {
        return afterBuild;
    }

    public void setAfterBuild(ScriptConfiguration afterBuild) {
        this.afterBuild = afterBuild;
        this.afterBuild.parent = this;
    }
}

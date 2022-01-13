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

public class BuildConfiguration  extends Configuration{
    @JsonProperty(value = "run before")
    String beforeBuild = "";
    @JsonProperty(value = "run after")
    String afterBuild = "";

    public String getBeforeBuild() {
        return beforeBuild;
    }

    public void setBeforeBuild(String beforeBuild) {
        this.beforeBuild = beforeBuild;
    }

    public String getAfterBuild() {
        return afterBuild;
    }

    public void setAfterBuild(String afterBuild) {
        this.afterBuild = afterBuild;
    }
}

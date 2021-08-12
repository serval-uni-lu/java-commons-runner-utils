package lu.uni.serval.commons.runner.utils.configuration;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg, Renaud RWEMALIKA
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import java.util.List;

public class MavenConfiguration extends Configuration {
    @JsonProperty(value = "java home")
    String javaHome = "";
    @JsonProperty(value = "environment variables")
    Entries environment = new Entries();
    @JsonProperty(value = "java arguments")
    Entries arguments = new Entries();
    @JsonProperty(value = "java tool options")
    List<Entry> javaToolOptions = Collections.emptyList();
    @JsonProperty(value = "maven options")
    List<Entry> mavenOptions = Collections.emptyList();
    @JsonProperty(value = "profiles")
    List<String> profiles = Collections.emptyList();
    @JsonProperty(value = "run before")
    String beforeBuild = "";
    @JsonProperty(value = "maven goals")
    List<String> goals = Collections.emptyList();
    @JsonProperty(value = "run after")
    String afterBuild = "";

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public Entries getEnvironment() {
        return environment;
    }

    public void setEnvironment(Entries environment) {
        this.environment = environment;
    }

    public Entries getArguments() {
        return arguments;
    }

    public void setArguments(Entries arguments) {
        this.arguments = arguments;
    }

    public List<Entry> getJavaToolOptions() {
        return javaToolOptions;
    }

    public void setJavaToolOptions(List<Entry> javaToolOptions) {
        this.javaToolOptions = javaToolOptions;
    }

    public List<Entry> getMavenOptions() {
        return mavenOptions;
    }

    public void setMavenOptions(List<Entry> mavenOptions) {
        this.mavenOptions = mavenOptions;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> freeFormParameters) {
        this.profiles = freeFormParameters;
    }

    public String getBeforeBuild() {
        return beforeBuild;
    }

    public void setBeforeBuild(String beforeBuild) {
        this.beforeBuild = beforeBuild;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public String getAfterBuild() {
        return afterBuild;
    }

    public void setAfterBuild(String afterBuild) {
        this.afterBuild = afterBuild;
    }
}

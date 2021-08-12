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
import lu.uni.serval.commons.git.utils.Frequency;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

public class RepositoryConfiguration extends Configuration{
    @JsonProperty(value = "ignore", defaultValue = "false")
    boolean ignore = false;
    @JsonProperty(value = "location", required = true)
    String location = "";
    @JsonProperty(value = "start date")
    private Instant startDate;
    @JsonProperty(value = "end date")
    private Instant endDate;
    @JsonProperty(value = "ignore commits")
    private Set<String> ignoreCommits = Collections.emptySet();
    @JsonProperty(value = "maximum number of commits", defaultValue = "0")
    private int maximumCommitsNumber = 0;
    @JsonProperty(value = "frequency", defaultValue = "UNIQUE")
    private Frequency frequency = Frequency.UNIQUE;
    @JsonProperty(value = "branch")
    private String branch = null;
    @JsonProperty(value = "process configuration")
    private MavenConfiguration mavenConfiguration = new MavenConfiguration();
    @JsonProperty(value = "cherry pick")
    private String[] cherryPick;

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Set<String> getIgnoreCommits() {
        return ignoreCommits;
    }

    public void setIgnoreCommits(Set<String> ignoreCommits) {
        this.ignoreCommits = ignoreCommits;
    }

    public int getMaximumCommitsNumber() {
        return maximumCommitsNumber;
    }

    public void setMaximumCommitsNumber(int maximumCommitsNumber) {
        this.maximumCommitsNumber = maximumCommitsNumber;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = Frequency.valueOf(frequency.toUpperCase());
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public MavenConfiguration getProcessConfiguration() {
        return mavenConfiguration;
    }

    public void setProcessConfiguration(MavenConfiguration process) {
        this.mavenConfiguration = process;
    }

    public String[] getCherryPick() {
        return cherryPick;
    }

    public void setCherryPick(String[] cherryPick) {
        this.cherryPick = cherryPick;
    }
}

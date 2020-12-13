package lu.uni.serval.commons.runner.utils.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.ikora.gitloader.git.Frequency;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class RepositoryConfiguration extends Configuration{
    @JsonProperty(value = "ignore", defaultValue = "false")
    boolean ignore = false;
    @JsonProperty(value = "location", required = true)
    String location = "";
    @JsonProperty(value = "start date")
    private Date startDate;
    @JsonProperty(value = "end date")
    private Date endDate;
    @JsonProperty(value = "ignore commits")
    private Set<String> ignoreCommits = Collections.emptySet();
    @JsonProperty(value = "maximum number of commits", defaultValue = "0")
    private int maximumCommitsNumber = 0;
    @JsonProperty(value = "frequency", defaultValue = "UNIQUE")
    private Frequency frequency = Frequency.UNIQUE;
    @JsonProperty(value = "branch", defaultValue = "master")
    private String branch = "master";
    @JsonProperty(value = "process configuration")
    private MavenConfiguration mavenConfiguration = new MavenConfiguration();

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
}

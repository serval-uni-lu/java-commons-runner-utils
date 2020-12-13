package lu.uni.serval.commons.runner.utils.configuration;

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

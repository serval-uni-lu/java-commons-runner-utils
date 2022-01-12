package lu.uni.serval.commons.runner.utils.configuration;

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

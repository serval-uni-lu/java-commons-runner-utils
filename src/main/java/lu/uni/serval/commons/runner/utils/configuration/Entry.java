package lu.uni.serval.commons.runner.utils.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lu.uni.serval.commons.runner.utils.os.OsUtils;

public class Entry {
    @JsonProperty(value = "name", required = true)
    private String name = "";
    @JsonProperty(value = "value")
    private String value = "";

    public Entry(){}

    public Entry(String name){
        this.name = name;
        this.value = "";
    }

    public Entry(String name, String value){
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String format(String separator){
        return format("", separator, "");
    }

    public String format(String prefix, String separator){
        return format(prefix, separator, "");
    }

    public String format(String prefix, String separator, String suffix){
        if(this.value != null && !this.value.isEmpty()){
            return String.format("%s%s%s%s%s", prefix, escapeSpace(name), separator, escapeSpace(value), suffix);
        }
        else{
            return String.format("%s%s%s", prefix, escapeSpace(name), suffix);
        }
    }

    private static String escapeSpace(String raw){
        if(OsUtils.isWindows()){
            return raw.replace(" ", "^ ");
        }

        return raw.replace(" ", "\\ ");
    }
}

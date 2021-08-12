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

package lu.uni.serval.commons.runner.utils.process;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg
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


import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class JavaLauncher extends ProcessLauncher {
    private File javaHome;
    private final Entries javaParameters = new Entries();
    private final Entries shortNameParameters = new Entries();
    private final Entries longNameParameters = new Entries();
    private final List<String> freeParameters = new ArrayList<>();

    protected JavaLauncher(String name) {
        super(name);
    }

    protected void setJavaHome(File javaHome){
        this.javaHome = javaHome;
    }

    protected void addJavaParameter(Entry entry){
        javaParameters.add(entry);
    }

    protected void addJavaParameters(Entries entries){
        javaParameters.addAll(entries);
    }

    protected void addShortNameParameter(Entry entry){
        shortNameParameters.add(entry);
    }

    protected void addLongNameParameter(Entry entry){
        longNameParameters.add(entry);
    }

    protected void addFreeParameter(String parameter){
        freeParameters.add(parameter);
    }

    protected List<String> getParameters(){
        final List<String> parameters = new LinkedList<>();

        parameters.addAll(freeParameters);
        parameters.addAll(javaParameters.format("-D", "="));
        parameters.addAll(shortNameParameters.format("-", " "));
        parameters.addAll(longNameParameters.format("--", " "));

        return parameters;
    }

    @Override
    protected Map<String, String> getEnvironment(){
        Map<String, String> localEnv = super.getEnvironment();

        if(javaHome != null && javaHome.exists()){
            localEnv.put("JAVA_HOME", javaHome.getAbsolutePath());
        }

        return localEnv;
    }
}

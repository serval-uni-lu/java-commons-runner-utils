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
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;

public abstract class JavaLauncher extends ProcessLauncher {
    private File javaHome;
    private boolean isPropagateJavaAgents = false;
    private final Entries javaParameters = new Entries();
    private final Entries shortNameParameters = new Entries();
    private final Entries longNameParameters = new Entries();
    private final List<String> freeParameters = new ArrayList<>();

    protected JavaLauncher(String name) {
        super(name);
    }

    public boolean isPropagateJavaAgents() {
        return isPropagateJavaAgents;
    }

    public void setPropagateJavaAgents(boolean propagateJavaAgents) {
        isPropagateJavaAgents = propagateJavaAgents;
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

    protected Set<String> getJavaagents(){
        return ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(a -> a.startsWith("-javaagent:"))
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<String, String> getEnvironment(){
        final Map<String, String> localEnv = super.getEnvironment();

        if(javaHome != null && javaHome.exists()){
            localEnv.put("JAVA_HOME", javaHome.getAbsolutePath());
            addPathEntry(new File(javaHome, "bin"));
        }

        return localEnv;
    }
}

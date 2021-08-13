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
import java.util.Map;

public abstract class JavaLauncher extends ProcessLauncher {
    private File javaHome;
    private final Entries javaParameters = new Entries();

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
        javaParameters.putAll(entries);
    }

    protected Entries getJavaParameters(){
        return javaParameters;
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

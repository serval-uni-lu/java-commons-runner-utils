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


import lu.uni.serval.commons.runner.utils.configuration.Entry;

import java.util.*;

public class ClassLauncher extends JavaLauncher {
    private final Class<?> classLaunched;

    public ClassLauncher(final Class<?> classLaunched) {
        super(classLaunched.getName());
        this.classLaunched = classLaunched;
    }

    public ClassLauncher withJavaParameter(String name, String value){
        addJavaParameter(new Entry(name, value));
        return this;
    }

    public ClassLauncher withFreeParameter(String freeParameter){
        addFreeParameter(freeParameter);
        return this;
    }

    public ClassLauncher withShortNameParameter(String name, String value){
        addShortNameParameter(new Entry(name, value));
        return this;
    }

    public ClassLauncher withLongNameParameter(String name, String value){
        addLongNameParameter(new Entry(name, value));
        return this;
    }

    @Override
    protected List<String> getCommand() {
        final List<String> command = new LinkedList<>();

        command.add("java");
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(this.classLaunched.getName());

        command.addAll(getParameters());

        return command;
    }
}

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
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;

public class JarLauncher extends JavaLauncher {
    private final File jar;

    public JarLauncher(final File jar) {
        super(jar.getName());
        this.jar = jar;

        super.setDirectory(jar.getParentFile());
    }

    public JarLauncher withJavaParameter(String name, String value){
        addJavaParameter(new Entry(name, value));
        return this;
    }

    public JarLauncher withFreeParameter(String freeParameter){
        addFreeParameter(freeParameter);
        return this;
    }

    @Override
    protected List<String> getCommand() {
        final List<String> command = new ArrayList<>();

        command.add("java");
        command.add("-jar");

        command.add(new Entry(getJarName()).format(""));

        command.addAll(getParameters());

        return command;
    }

    private String getJarName(){
        return FilenameUtils.getName(jar.getAbsolutePath());
    }
}

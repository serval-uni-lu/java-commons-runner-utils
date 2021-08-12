package lu.uni.serval.commons.runner.utils.process;

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


import lu.uni.serval.commons.runner.utils.os.OsUtils;

import java.io.File;
import java.util.*;

public class ScriptLauncher extends ProcessLauncher{
    private final List<String> command;

    public ScriptLauncher(String name, String command, File directory){
        super(name);
        this.command = Arrays.asList(command.split("\\s+"));
        this.directory = directory;
    }

    @Override
    protected List<String> getCommand() {
        final List<String> processCommand = new ArrayList<>();

        if(OsUtils.isWindows()){
            processCommand.add("cmd.exe");
            processCommand.add("/c");
        }

        processCommand.addAll(command);

        return processCommand;
    }
}

package lu.uni.serval.commons.runner.utils.build.maven;

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
import lu.uni.serval.commons.runner.utils.os.OsUtils;
import lu.uni.serval.commons.runner.utils.process.FileLogger;
import lu.uni.serval.commons.runner.utils.process.JavaLauncher;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MavenLauncher extends JavaLauncher {

    private final Entries javaToolOptions = new Entries();
    private final Entries mavenOptions = new Entries();
    private final List<String> profiles = new ArrayList<>();

    private List<String> modules = new ArrayList<>();
    private List<String> goals = Collections.emptyList();

    public MavenLauncher() {
        super("Maven Build");
    }

    public MavenLauncher usingJavaVersion(File javaHome){
        super.setJavaHome(javaHome);
        return this;
    }

    public MavenLauncher withMavenOptions(String key, String value){
        mavenOptions.put(key, value);
        return this;
    }

    public MavenLauncher withMavenOptions(List<Entry> options){
        mavenOptions.putAll(options);
        return this;
    }

    public MavenLauncher withEnvironmentVariables(Entry... entries){
        for(Entry entry: entries){
            super.addEnvironmentVariable(entry);
        }

        return this;
    }

    public MavenLauncher withEnvironmentVariables(Entries environmentVariables){
        super.addEnvironmentVariables(environmentVariables);
        return this;
    }

    public MavenLauncher withJavaToolOptions(List<Entry> javaToolOptions){
        this.javaToolOptions.putAll(javaToolOptions);
        return this;
    }

    public MavenLauncher withJavaParameter(String name, String value){
        addJavaParameter(new Entry(name, value));
        return this;
    }

    public MavenLauncher withJavaParameters(Entries extraParameters){
        addJavaParameters(extraParameters);
        return this;
    }

    public MavenLauncher withLongNameParameter(String name, String value){
        addLongNameParameter(new Entry(name, value));
        return this;
    }

    public MavenLauncher withShortNameParameter(String name, String value){
        addShortNameParameter(new Entry(name, value));
        return this;
    }

    public MavenLauncher usingProfile(List<String> profiles){
        this.profiles.addAll(cleanList(profiles));
        return this;
    }

    public MavenLauncher inDirectory(File directory){
        super.setDirectory(directory);
        return this;
    }

    public MavenLauncher forModules(String... modules){
        return forModules(Arrays.asList(modules));
    }

    public MavenLauncher forModules(List<String> modules){
        this.modules = cleanList(modules);
        return this;
    }

    public MavenLauncher forGoals(String... goals){
        return forGoals(Arrays.asList(goals));
    }

    public MavenLauncher forGoals(List<String> goals) {
        this.goals = cleanList(goals);
        return this;
    }

    public MavenLauncher writeOutputTo(File logFile) {
        if(logFile != null){
            addListener(new FileLogger(logFile, true));
        }

        return this;
    }

    @Override
    protected Map<String, String> getEnvironment(){
        Map<String, String> localEnv = super.getEnvironment();

        final String formattedMavenOptions = mavenOptions.stream()
                .map(e -> e.format("-", ":"))
                .collect(Collectors.joining(" "));

        if(!formattedMavenOptions.isEmpty()){
            localEnv.put("MAVEN_OPTS", formattedMavenOptions);
        }

        final String formattedJavaToolOptions = javaToolOptions.stream()
                .map(e -> e.format("-D", ":"))
                .collect(Collectors.joining(" "));

        if(!formattedJavaToolOptions.isEmpty()){
            localEnv.put("JAVA_TOOL_OPTIONS", String.join(" ", formattedJavaToolOptions));
        }

        return localEnv;
    }

    @Override
    protected List<String> getCommand(){
        final List<String> command = new ArrayList<>();

        command.add(OsUtils.isWindows() ? "mvn.cmd" : "mvn");
        command.add("--batch-mode");

        if(!modules.isEmpty()) {
            command.add("-pl");
            command.add(String.join(",", modules));
        }

        command.addAll(goals);

        command.addAll(getParameters());

        if(!profiles.isEmpty()){
            command.add(String.format("-P%s", String.join(",", profiles)));
        }

        return command;
    }

    private List<String> cleanList(List<String> raw){
        return raw.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}

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
import lu.uni.serval.commons.runner.utils.listener.Listener;
import lu.uni.serval.commons.runner.utils.listener.ProcessLogger;
import lu.uni.serval.commons.runner.utils.listener.StringLogger;
import lu.uni.serval.commons.runner.utils.os.OsUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class ProcessLauncher {
    private static final Logger logger = LogManager.getLogger(ProcessLauncher.class);

    protected File directory = null;
    protected final ProcessLogger processLogger;
    private final Entries environmentVariables = new Entries();
    private Process process;
    private final Set<Listener> listeners = new HashSet<>();
    private final List<File> paths = new ArrayList<>();

    protected ProcessLauncher(String name) {
        processLogger = new ProcessLogger(name);
        addListener(processLogger);
    }

    protected void setDirectory(File directory){
        this.directory = directory;
    }

    protected void addEnvironmentVariables(Entries entries){
        environmentVariables.putAll(entries);
    }

    protected void addPathEntry(File pathEntry){
        paths.add(pathEntry);
    }

    public void addEnvironmentVariable(Entry entry){
        environmentVariables.add(entry);
    }

    public String executeSync(int timeout, TimeUnit timeUnit) throws IOException, InterruptedException {
        final StringLogger stringLogger = new StringLogger();
        addListener(stringLogger);

        execute();
        processLogger.join(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
        kill();

        return stringLogger.getOut();
    }

    public void executeAsync() throws IOException {
        execute();
    }

    private void execute() throws IOException {
        final ProcessBuilder builder = new ProcessBuilder();

        builder.redirectErrorStream(true);

        setEnvironment(builder);
        setDirectory(builder);
        setCommand(builder);

        logger.printf(Level.INFO, "Execute command: %s", String.join(" ", builder.command()));

        process = builder.start();
        startListeners(process);
    }

    private void setEnvironment(ProcessBuilder builder){
        final Map<String, String> localEnv = builder.environment();
        localEnv.putAll(getEnvironment());

        if(!paths.isEmpty()){
            final String path = paths.stream()
                    .map(File::getAbsolutePath)
                    .reduce(localEnv.get(OsUtils.PATH), OsUtils::addValueToPath);

            localEnv.put(OsUtils.PATH, path);
        }
    }

    private void setDirectory(ProcessBuilder builder){
        getDirectory().ifPresent(builder::directory);
    }

    private void setCommand(ProcessBuilder builder){
        builder.command(getCommand());
    }

    private void startListeners(Process process){
        final InputStreamSplitter splitter = new InputStreamSplitter(process.getInputStream());

        this.listeners.forEach(splitter::register);

        splitter.start();
    }

    public boolean isRunning(){
        if(process == null){
            return false;
        }

        return this.process.isAlive();
    }

    public int getExitCode(){
        if(process == null){
            return 0;
        }

        return this.process.exitValue();
    }

    public void kill(){
        if(isRunning()){
            Process killedProcess;

            do {
                killedProcess = this.process.destroyForcibly();
            } while (killedProcess.isAlive());
        }
    }

    public void addListener(Listener listener){
        if(listener != null){
            this.listeners.add(listener);
        }
    }

    protected Map<String, String> getEnvironment(){
        Map<String, String> localEnv = new HashMap<>();

        for(Entry entry: environmentVariables){
            localEnv.put(entry.getName(), entry.getValue());
        }

        return localEnv;
    }

    protected Optional<File> getDirectory(){
        if(directory != null && directory.exists()){
            return Optional.of(directory);
        }

        return Optional.empty();
    }

    protected abstract List<String> getCommand();
}

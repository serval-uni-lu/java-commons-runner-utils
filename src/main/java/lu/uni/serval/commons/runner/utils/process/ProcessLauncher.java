package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class ProcessLauncher {
    private static final Logger logger = LogManager.getLogger(ProcessLauncher.class);

    protected File directory = null;
    protected final ProcessLogger processLogger;
    private final Entries environmentVariables = new Entries();
    private Process process;
    private Set<Listener> listeners = new HashSet<>();

    public ProcessLauncher(String name) {
        processLogger = new ProcessLogger(name);
        addListener(processLogger);
    }

    protected void setDirectory(File directory){
        this.directory = directory;
    }

    protected void addEnvironmentVariables(Entries entries){
        environmentVariables.putAll(entries);
    }

    public void addEnvironmentVariable(Entry entry){
        environmentVariables.add(entry);
    }

    public void execute(boolean isSynchronous) throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder();

        builder.redirectErrorStream(true);

        setEnvironment(builder);
        setDirectory(builder);
        setCommand(builder);

        logger.printf(Level.DEBUG, "Execute command: %s", String.join(" ", builder.command()));

        process = builder.start();
        startListeners(process);

        if(isSynchronous){
            processLogger.join();
        }
    }

    private void setEnvironment(ProcessBuilder builder){
        Map<String, String> localEnv = builder.environment();
        localEnv.putAll(getEnvironment());
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

package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class ProcessLauncher implements Synchronizable {
    private static final Logger logger = LogManager.getLogger(ProcessLauncher.class);

    protected File directory = null;
    protected final ProcessLogger processLogger;
    private final Map<Synchronization.Step, Thread> synchronizationThreads = new HashMap<>();
    private final Entries environmentVariables = new Entries();
    private Process process;
    private Set<Listener> listeners = new HashSet<>();

    public ProcessLauncher(String name){
        processLogger = new ProcessLogger(name);
        addListener(processLogger);
        registerSynchronizationThread(Synchronization.Step.FINISHED, processLogger);
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

        logger.debug(String.format("Execute command: %s", String.join(" ", builder.command())));

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

    public void addListener(Listener listener){
        if(listener != null){
            this.listeners.add(listener);
        }
    }

    protected void registerSynchronizationThread(Synchronization.Step step, Thread thread){
        synchronizationThreads.put(step, thread);
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

    @Override
    public Optional<Thread> getThread(Synchronization.Step step) {
        return Optional.ofNullable(synchronizationThreads.get(step));
    }

    protected abstract List<String> getCommand();
}

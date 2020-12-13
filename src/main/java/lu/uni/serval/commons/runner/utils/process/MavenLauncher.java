package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;
import lu.uni.serval.commons.runner.utils.os.OsUtils;

import java.io.*;
import java.util.*;

public class MavenLauncher extends JavaLauncher implements Synchronizable {

    private final Entries javaToolOptions = new Entries();
    private final Entries mavenOptions = new Entries();
    private final List<String> profiles = new ArrayList<>();
    private List<String> goals = Collections.emptyList();
    private File logFile = null;

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

    public MavenLauncher usingProfile(List<String> profiles){
        this.profiles.addAll(profiles);
        return this;
    }

    public MavenLauncher inDirectory(File directory){
        super.setDirectory(directory);
        return this;
    }

    public MavenLauncher forGoals(List<String> goals) {
        this.goals = goals;
        return this;
    }

    public MavenLauncher writeOutputTo(File logFile) {
        this.logFile = logFile;
        return this;
    }

    @Override
    protected Map<String, String> getEnvironment(){
        Map<String, String> localEnv = super.getEnvironment();

        final List<String> formattedMavenOptions = new ArrayList<>();
        for(Entry entry: mavenOptions){
            formattedMavenOptions.add(entry.format("-", ":"));
        }
        localEnv.put("MAVEN_OPTS", String.join(" ", formattedMavenOptions));

        final List<String> formattedJavaToolOptions = new ArrayList<>();
        for(Entry entry: javaToolOptions){
            formattedJavaToolOptions.add(entry.format("-D", ":"));
        }
        localEnv.put("JAVA_TOOL_OPTIONS", String.join(" ", formattedJavaToolOptions));

        return localEnv;
    }

    @Override
    protected List<String> getCommand(){
        final List<String> command = new ArrayList<>();

        command.add(OsUtils.isWindows() ? "mvn.cmd" : "mvn");
        command.add("--batch-mode");
        command.addAll(goals);

        for(Entry entry: super.getJavaParameters()){
            command.add(entry.format("-D", "="));
        }

        if(!profiles.isEmpty()){
            command.add("-P");
            command.add(String.join(",", profiles));
        }

        return command;
    }

    @Override
    protected Set<Listener> getListeners() {
        Set<Listener> listeners = new HashSet<>(1);
        listeners.add(new FileLogger(logFile, true));

        return listeners;
    }
}

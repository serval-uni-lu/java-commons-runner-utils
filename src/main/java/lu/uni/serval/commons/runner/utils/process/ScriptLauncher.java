package lu.uni.serval.commons.runner.utils.process;

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

    @Override
    protected Set<Listener> getListeners() {
        return Collections.emptySet();
    }
}

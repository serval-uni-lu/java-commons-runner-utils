package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;

public class JarLauncher extends JavaLauncher {
    private final File jar;
    private final List<String> freeParameters = new ArrayList<>();

    public JarLauncher(final File jar) {
        super(jar.getName());
        this.jar = jar;

        super.setDirectory(jar.getParentFile());
    }

    public JarLauncher withJavaParameter(String name, String value){
        addJavaParameter(new Entry(name, value));
        return this;
    }

    public JarLauncher withJavaParameters(Entries extraParameters){
        addJavaParameters(extraParameters);
        return this;
    }

    public JarLauncher withFreeParameter(String freeParameter){
        freeParameters.add(freeParameter);
        return this;
    }

    @Override
    protected List<String> getCommand() {
        final List<String> command = new ArrayList<>();

        command.add("java");
        command.add("-jar");

        command.add(new Entry(getJarName()).format(""));

        for(Entry entry: super.getJavaParameters()){
            command.add(entry.format("-D", "="));
        }

        command.addAll(freeParameters);

        return command;
    }

    private String getJarName(){
        return FilenameUtils.getName(jar.getAbsolutePath());
    }
}

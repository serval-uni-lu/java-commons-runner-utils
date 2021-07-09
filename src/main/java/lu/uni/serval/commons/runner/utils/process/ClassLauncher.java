package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;

import java.util.*;

public class ClassLauncher extends JavaLauncher {
    private final Class<?> classLaunched;
    private final List<String> freeParameters = new ArrayList<>();

    public ClassLauncher(final Class<?> classLaunched) {
        super(classLaunched.getName());
        this.classLaunched = classLaunched;
    }

    public ClassLauncher withJavaParameter(String name, String value){
        addJavaParameter(new Entry(name, value));
        return this;
    }

    public ClassLauncher withJavaParameters(Entries extraParameters){
        addJavaParameters(extraParameters);
        return this;
    }

    public ClassLauncher withFreeParameter(String freeParameter){
        freeParameters.add(freeParameter);
        return this;
    }

    @Override
    protected List<String> getCommand() {
        final List<String> command = new LinkedList<>();

        command.add("java");
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(this.classLaunched.getName());

        command.addAll(freeParameters);

        return command;
    }
}

package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManagedClassLauncher extends JavaLauncher {
    private final Class<? extends ManagedProcess> classLaunched;
    private final List<String> freeParameters = new ArrayList<>();
    private final String brokerHost;
    private final int brokerPort;
    private final String name;

    public ManagedClassLauncher(final Class<? extends ManagedProcess> classLaunched, final String brokerHost, final int brokerPort) {
        super(classLaunched.getName());

        this.classLaunched = classLaunched;
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
        this.name = String.format("%s-%d", classLaunched.getSimpleName(),  System.currentTimeMillis());
    }

    public ManagedClassLauncher(final Class<? extends ManagedProcess> classLaunched, final int brokerPort){
        this(classLaunched, "localhost", brokerPort);
    }

    public ManagedClassLauncher withJavaParameter(String name, String value){
        addJavaParameter(new Entry(name, value));
        return this;
    }

    public ManagedClassLauncher withJavaParameters(Entries extraParameters){
        addJavaParameters(extraParameters);
        return this;
    }

    public ManagedClassLauncher withFreeParameter(String freeParameter){
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

        command.add("-brokerHost");
        command.add(this.brokerHost);

        command.add("-brokerPort");
        command.add(String.valueOf(this.brokerPort));

        command.add("-name");
        command.add(name);

        command.addAll(freeParameters);

        return command;
    }

    public String getName() {
        return this.name;
    }
}

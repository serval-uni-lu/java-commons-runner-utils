package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.configuration.Entries;
import lu.uni.serval.commons.runner.utils.configuration.Entry;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManagedClassLauncher extends JavaLauncher {
    private final Class<? extends ManagedProcess> classLaunched;
    private final List<String> freeParameters = new ArrayList<>();
    private final String name;
    private final String brokerUrl;

    public ManagedClassLauncher(final Class<? extends ManagedProcess> classLaunched) throws NotInitializedException {
        super(classLaunched.getName());

        this.classLaunched = classLaunched;
        this.name = String.format("%s.%d", classLaunched.getSimpleName().toLowerCase(),  System.currentTimeMillis());
        this.brokerUrl = BrokerInfo.url();
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

        command.add("-name");
        command.add(this.name);

        command.add("-brokerUrl");
        command.add(this.brokerUrl);

        command.addAll(freeParameters);

        return command;
    }

    public String getName() {
        return this.name;
    }
}

package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.helpers.InfiniteLaunchableClass;
import lu.uni.serval.commons.runner.utils.helpers.SimpleLaunchableClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.Broker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClassLauncherTest {
    @Test
    void launchProcessFromClass() throws IOException, InterruptedException {
        final StringLogger stringLogger = new StringLogger();
        final ClassLauncher classLauncher = new ClassLauncher(SimpleLaunchableClass.class);

        classLauncher.addListener(stringLogger);
        classLauncher.execute(true);

        assertEquals("Hello from process with arguments: []", stringLogger.getOut().trim());
        assertEquals("", stringLogger.getErr().trim());
    }
}
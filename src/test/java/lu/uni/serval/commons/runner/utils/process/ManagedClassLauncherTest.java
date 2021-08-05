package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.helpers.InfiniteLaunchableClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.Broker;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagedClassLauncherTest {
    private static Broker broker;

    @BeforeAll
    static void startBroker() throws IOException, InterruptedException {
        final String bindAddress = "tcp://localhost:61616";
        final String name = "testBroker";

        broker = new Broker(name, bindAddress);
        broker.executeAndWaitForReady();
    }

    @AfterAll
    static void killBroker() {
        broker.close();
    }

    @Test
    void killProcessRunningForEver() throws IOException, InterruptedException, JMSException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class, "localhost", 61616);
        classLauncher.execute(false);
        Thread.sleep(1000);
        assertTrue(classLauncher.isRunning());

        BrokerUtils.sendMessageToQueue("localhost", 61616, classLauncher.getQueueName(), "STOP");

        assertFalse(classLauncher.isRunning());
    }
}
package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.helpers.InfiniteLaunchableClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.Broker;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.StopFrame;
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
        broker = new Broker("testBroker", Constants.LOCALHOST, Constants.DEFAULT_BROKER_PORT);
        broker.executeAndWaitForReady();
    }

    @AfterAll
    static void stopBroker() {
        broker.close();
    }

    @Test
    void stopProcessUsingQueue() throws IOException, InterruptedException, JMSException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class, 61616);
        classLauncher.execute(false);
        Thread.sleep(200);
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToQueue( 61616, classLauncher.getQueueName(), new StopFrame());
        Thread.sleep(500);

        assertFalse(classLauncher.isRunning());
    }

    @Test
    void stopProcessUsingAdminTopic() throws IOException, InterruptedException, JMSException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class, 61616);
        classLauncher.execute(false);
        Thread.sleep(200);
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToTopic( 61616, Constants.ADMIN_TOPIC, new StopFrame());
        Thread.sleep(500);

        assertFalse(classLauncher.isRunning());
    }
}
package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.helpers.InfiniteLaunchableClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerManager;
import lu.uni.serval.commons.runner.utils.messaging.frame.ClosingFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.frame.ReadyFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.StopFrame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagedClassLauncherTest {
    private static BrokerManager brokerManager;

    @BeforeAll
    static void startBroker() throws IOException, InterruptedException {
        brokerManager = new BrokerManager("testBroker", Constants.LOCALHOST, Constants.DEFAULT_BROKER_PORT);
        brokerManager.executeAndWaitForReady();
    }

    @AfterAll
    static void stopBroker() {
        brokerManager.close();
    }

    @Test
    void stopProcessUsingQueue() throws IOException, InterruptedException, JMSException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class, brokerManager.getPort());
        classLauncher.execute(false);

        final Frame frame = MessageUtils.waitForMessage(brokerManager.getHost(), brokerManager.getPort(), classLauncher.getName(), ReadyFrame.CODE);
        assertEquals(ReadyFrame.CODE, frame.getCode());
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToTopic(brokerManager.getPort(), Constants.ADMIN_TOPIC, new StopFrame());
        final Frame closingFrame = MessageUtils.waitForMessage(brokerManager.getHost(), brokerManager.getPort(), classLauncher.getName(), ClosingFrame.CODE);
        assertEquals(ClosingFrame.CODE, closingFrame.getCode());
    }

    @Test
    void stopProcessUsingAdminTopic() throws IOException, InterruptedException, JMSException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class, brokerManager.getPort());
        classLauncher.execute(false);

        final Frame readyFrame = MessageUtils.waitForMessage(brokerManager.getHost(), brokerManager.getPort(), classLauncher.getName(), ReadyFrame.CODE);
        assertEquals(ReadyFrame.CODE, readyFrame.getCode());
        assertTrue(classLauncher.isRunning());
        Thread.sleep(500);
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToTopic(brokerManager.getPort(), Constants.ADMIN_TOPIC, new StopFrame());
        final Frame closingFrame = MessageUtils.waitForMessage(brokerManager.getHost(), brokerManager.getPort(), classLauncher.getName(), ClosingFrame.CODE);
        assertEquals(ClosingFrame.CODE, closingFrame.getCode());
    }

    @Test
    void forciblyKillProcess() throws IOException, InterruptedException, JMSException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class, brokerManager.getPort());
        classLauncher.execute(false);

        final Frame readyFrame = MessageUtils.waitForMessage(brokerManager.getHost(), brokerManager.getPort(), classLauncher.getName(), ReadyFrame.CODE);
        assertEquals(ReadyFrame.CODE, readyFrame.getCode());
        assertTrue(classLauncher.isRunning());
        Thread.sleep(500);
        assertTrue(classLauncher.isRunning());

        classLauncher.kill();
        assertFalse(classLauncher.isRunning());
    }
}
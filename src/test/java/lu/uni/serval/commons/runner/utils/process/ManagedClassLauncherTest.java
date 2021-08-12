package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.exception.AlreadyInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.helpers.InfiniteLaunchableClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;
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
    static void startBroker() throws IOException, InterruptedException, AlreadyInitializedException, NotInitializedException {
        BrokerInfo.initialize(Constants.DEFAULT_BROKER_PROTOCOL, Constants.DEFAULT_BROKER_HOST, Constants.DEFAULT_BROKER_PORT);

        brokerManager = new BrokerManager("testBroker");
        brokerManager.executeAndWaitForReady();
    }

    @AfterAll
    static void stopBroker() {
        brokerManager.close();
    }

    @Test
    void stopProcessUsingQueue() throws IOException, InterruptedException, JMSException, NotInitializedException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class);
        classLauncher.execute(false);

        final Frame frame = MessageUtils.waitForMessage(classLauncher.getName(), ReadyFrame.CODE);
        assertEquals(ReadyFrame.CODE, frame.getCode());
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToQueue(classLauncher.getName(), new StopFrame());
        final Frame closingFrame = MessageUtils.waitForMessage(classLauncher.getName(), ClosingFrame.CODE);
        assertEquals(ClosingFrame.CODE, closingFrame.getCode());
    }

    @Test
    void stopProcessUsingAdminTopic() throws IOException, InterruptedException, JMSException, NotInitializedException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class);
        classLauncher.execute(false);

        final Frame readyFrame = MessageUtils.waitForMessage(classLauncher.getName(), ReadyFrame.CODE);
        assertEquals(ReadyFrame.CODE, readyFrame.getCode());
        assertTrue(classLauncher.isRunning());
        Thread.sleep(500);
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToTopic(Constants.TOPIC_ADMIN, new StopFrame());
        final Frame closingFrame = MessageUtils.waitForMessage(classLauncher.getName(), ClosingFrame.CODE);
        assertEquals(ClosingFrame.CODE, closingFrame.getCode());
    }

    @Test
    void forciblyKillProcess() throws IOException, InterruptedException, JMSException, NotInitializedException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(InfiniteLaunchableClass.class);
        classLauncher.execute(false);

        final Frame readyFrame = MessageUtils.waitForMessage(classLauncher.getName(), ReadyFrame.CODE);
        assertEquals(ReadyFrame.CODE, readyFrame.getCode());
        assertTrue(classLauncher.isRunning());
        Thread.sleep(500);
        assertTrue(classLauncher.isRunning());

        classLauncher.kill();
        assertFalse(classLauncher.isRunning());
    }
}
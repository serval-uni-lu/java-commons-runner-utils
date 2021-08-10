package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class BrokerManagerTest {
    @Test
    void testStartAndStop() throws IOException, InterruptedException {
        final BrokerManager brokerManager = new BrokerManager("testBroker", Constants.LOCALHOST, Constants.DEFAULT_BROKER_PORT);
        final Observer observer = new Observer();
        observer.addRunner(brokerManager::onBrokerStopped);

        brokerManager.executeAndWaitForReady();
        assertTrue(brokerManager.isRunning());
        Thread.sleep(500);
        assertTrue(brokerManager.isRunning());
        brokerManager.close();

        observer.waitOnMessages();

        assertFalse(brokerManager.isRunning());
        assertThrows(ConnectException.class, () -> new Socket(Constants.LOCALHOST, Constants.DEFAULT_BROKER_PORT));
    }
}
package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.Broker;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {
    @Test
    void testStartAndStop() throws IOException, InterruptedException {
        final Broker broker = new Broker("testBroker", Constants.LOCALHOST, Constants.DEFAULT_BROKER_PORT);
        final Observer observer = new Observer();
        observer.addRunner(broker::onBrokerStopped);

        broker.executeAndWaitForReady();
        assertTrue(broker.isRunning());
        Thread.sleep(500);
        assertTrue(broker.isRunning());
        broker.close();

        observer.waitOnMessages();

        assertFalse(broker.isRunning());
        assertThrows(ConnectException.class, () -> new Socket(Constants.LOCALHOST, Constants.DEFAULT_BROKER_PORT));
    }
}
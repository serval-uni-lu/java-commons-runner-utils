package lu.uni.serval.commons.runner.utils.messaging.activemq;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {
    @Test
    void testStartAndStop() throws IOException, InterruptedException {
        final String bindAddress = "tcp://localhost:61616";
        final String name = "testBroker";

        final Broker broker = new Broker(name, bindAddress);

        broker.executeAndWaitForReady();
        assertTrue(broker.isRunning());
        Thread.sleep(500);
        assertTrue(broker.isRunning());
        broker.close();
        assertFalse(broker.isRunning());
    }
}
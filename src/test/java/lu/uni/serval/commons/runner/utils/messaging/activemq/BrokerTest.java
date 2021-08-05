package lu.uni.serval.commons.runner.utils.messaging.activemq;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {
    @Test
    void testStartAndStop() throws IOException, InterruptedException {
        final String bindAddress = "tcp://localhost:61616";
        final String name = "testBroker";

        final Broker broker = new Broker(name, bindAddress);
        final Observer observer = new Observer();

        broker.executeAndWaitForReady();
        assertTrue(broker.isRunning());
        Thread.sleep(500);
        assertTrue(broker.isRunning());
        broker.close();

        broker.onBrokerStopped(() -> {
            synchronized (observer){
                observer.touch();
                observer.notifyAll();
            }
        });

        synchronized (observer){
            while (!observer.isTouched()){
                observer.wait();
            }
        }

        assertFalse(broker.isRunning());
        assertThrows(ConnectException.class, () -> new Socket("localhost", 61616));
    }
}
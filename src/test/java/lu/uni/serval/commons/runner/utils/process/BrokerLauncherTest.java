package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.helpers.Observer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BrokerLauncherTest {
    @Test
    void test() throws IOException, InterruptedException {
        final String bindAddress = "tcp://localhost:61616";
        final String name = "testBroker";

        final Observer observer = new Observer();
        final BrokerLauncher brokerLauncher = new BrokerLauncher(name, bindAddress);

        brokerLauncher.onBrokerReady(() -> onBrokerReady(observer));
        brokerLauncher.launch();
        while (!observer.isTouched()){}
        assertTrue(brokerLauncher.isRunning());

        brokerLauncher.close();
        assertFalse(brokerLauncher.isRunning());
    }

    private void onBrokerReady(Observer observer){
        observer.touch();
    }
}
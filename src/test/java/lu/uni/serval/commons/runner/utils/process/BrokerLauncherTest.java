package lu.uni.serval.commons.runner.utils.process;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BrokerLauncherTest {
    @Test
    void testStartAndStop() throws IOException, InterruptedException {
        final String bindAddress = "tcp://localhost:61616";
        final String name = "testBroker";

        final BrokerLauncher brokerLauncher = new BrokerLauncher(name, bindAddress);

        brokerLauncher.launchAndWaitForReady();
        assertTrue(brokerLauncher.isRunning());

        brokerLauncher.close();
        assertFalse(brokerLauncher.isRunning());
    }
}
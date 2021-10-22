package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.exception.AlreadyInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotStartedException;
import lu.uni.serval.commons.runner.utils.helpers.RequestClass;
import lu.uni.serval.commons.runner.utils.helpers.ResponseClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerLauncher;
import lu.uni.serval.commons.runner.utils.process.ManagedClassLauncher;
import lu.uni.serval.commons.runner.utils.process.StringLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageUtilsTest {
    private static BrokerLauncher brokerLauncher;

    @BeforeAll
    static void startBroker() throws IOException, InterruptedException, AlreadyInitializedException, NotInitializedException, NotStartedException {
        BrokerInfo.initialize(Constants.DEFAULT_BROKER_PROTOCOL, Constants.DEFAULT_BROKER_HOST, Constants.DEFAULT_BROKER_PORT);

        brokerLauncher = new BrokerLauncher("testBroker");
        brokerLauncher.executeAndWaitForReady();
    }

    @AfterAll
    static void stopBroker() {
        brokerLauncher.close();
    }

    @Test
    void testWaitForResponse() throws NotInitializedException, IOException, InterruptedException {
        final ManagedClassLauncher responseProcess = new ManagedClassLauncher(ResponseClass.class);
        responseProcess.executeAsync();

        final ManagedClassLauncher requestProcess = new ManagedClassLauncher(RequestClass.class);
        final StringLogger requestProcessOutput = new StringLogger();
        requestProcess.withLongNameParameter("text", "MyTest");
        requestProcess.withLongNameParameter("workerQueueName", responseProcess.getName());
        requestProcess.addListener(requestProcessOutput);
        requestProcess.executeSync(15, TimeUnit.SECONDS);

        assertEquals("tseTyM", requestProcessOutput.getOut().trim());
    }
}

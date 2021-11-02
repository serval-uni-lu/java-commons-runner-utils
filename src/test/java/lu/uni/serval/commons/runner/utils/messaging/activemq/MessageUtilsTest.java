package lu.uni.serval.commons.runner.utils.messaging.activemq;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
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

        assertTrue(requestProcessOutput.getOut().contains("tseTyM"));
    }

    @Test
    void testWaitForResponseWithException() throws NotInitializedException, IOException, InterruptedException {
        final ManagedClassLauncher responseProcess = new ManagedClassLauncher(ResponseClass.class);
        responseProcess.withLongNameParameter("isError", "true");
        responseProcess.executeAsync();

        final ManagedClassLauncher requestProcess = new ManagedClassLauncher(RequestClass.class);
        final StringLogger requestProcessOutput = new StringLogger();
        requestProcess.withLongNameParameter("text", "MyTest");
        requestProcess.withLongNameParameter("workerQueueName", responseProcess.getName());
        requestProcess.addListener(requestProcessOutput);
        requestProcess.executeSync(15, TimeUnit.SECONDS);

        assertTrue(requestProcessOutput.getOut().contains("[java.lang.Exception] Forced exception"));
    }
}

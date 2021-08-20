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
import lu.uni.serval.commons.runner.utils.helpers.TestManagedClass;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerManager;
import lu.uni.serval.commons.runner.utils.process.ManagedClassLauncher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class BrokerManagerTest {
    @BeforeAll
    static void initializeBrokerInfo() throws AlreadyInitializedException {
        BrokerInfo.initialize(Constants.DEFAULT_BROKER_PROTOCOL, Constants.DEFAULT_BROKER_HOST, Constants.DEFAULT_BROKER_PORT);
    }

    @BeforeEach

    @Test
    void testStartAndStop() throws IOException, InterruptedException, NotInitializedException, NotStartedException {
        final BrokerManager brokerManager = new BrokerManager("testBroker");
        brokerManager.executeAndWaitForReady();
        assertTrue(brokerManager.isRunning());
        brokerManager.close();

        assertTrue(Awaiter.waitUntil(10000, () -> !brokerManager.isRunning()));
        assertThrows(ConnectException.class, () -> new Socket(Constants.DEFAULT_BROKER_HOST, Constants.DEFAULT_BROKER_PORT));
    }

    @Test
    void testStopManagedProcesses() throws NotInitializedException, IOException, InterruptedException, NotStartedException {
        final ManagedClassLauncher launcher = new ManagedClassLauncher(TestManagedClass.class);

        try(final BrokerManager brokerManager = new BrokerManager("testBroker")){
            brokerManager.executeAndWaitForReady();
            launcher.execute(false);
            // give time for the process to properly start
            Thread.sleep(3000);
            assertTrue(launcher.isRunning());
        }

        assertTrue(Awaiter.waitUntil(10000, () -> !launcher.isRunning()));
    }
}

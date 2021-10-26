package lu.uni.serval.commons.runner.utils.process;

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
import lu.uni.serval.commons.runner.utils.helpers.TestTransportListener;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerLauncher;
import lu.uni.serval.commons.runner.utils.messaging.frame.ClosingFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.frame.ReadyFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.StopFrame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ManagedClassLauncherTest {
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
    void stopProcessUsingQueue() throws IOException, InterruptedException, JMSException, NotInitializedException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(TestManagedClass.class);
        classLauncher.executeAsync();

        final Optional<Frame> frame = MessageUtils.waitForMessage(classLauncher.getName(), ReadyFrame.CODE);
        assertTrue(frame.isPresent());
        assertEquals(ReadyFrame.CODE, frame.get().getCode());
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToQueue(new TestTransportListener(), classLauncher.getName(), new StopFrame());
        final Optional<Frame> closingFrame = MessageUtils.waitForMessage(classLauncher.getName(), ClosingFrame.CODE);
        assertTrue(closingFrame.isPresent());
        assertEquals(ClosingFrame.CODE, closingFrame.get().getCode());
    }

    @Test
    void stopProcessUsingAdminTopic() throws IOException, InterruptedException, JMSException, NotInitializedException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(TestManagedClass.class);
        classLauncher.executeAsync();

        final Optional<Frame> readyFrame = MessageUtils.waitForMessage(classLauncher.getName(), ReadyFrame.CODE);
        assertTrue(readyFrame.isPresent());
        assertEquals(ReadyFrame.CODE, readyFrame.get().getCode());
        assertTrue(classLauncher.isRunning());

        MessageUtils.sendMessageToTopic(new TestTransportListener(), Constants.TOPIC_ADMIN, new StopFrame());
        final Optional<Frame> closingFrame = MessageUtils.waitForMessage(classLauncher.getName(), ClosingFrame.CODE);
        assertTrue(closingFrame.isPresent());
        assertEquals(ClosingFrame.CODE, closingFrame.get().getCode());
    }

    @Test
    void forciblyKillProcess() throws IOException, InterruptedException, JMSException, NotInitializedException {
        final ManagedClassLauncher classLauncher = new ManagedClassLauncher(TestManagedClass.class);
        classLauncher.executeAsync();

        final Optional<Frame> readyFrame = MessageUtils.waitForMessage(classLauncher.getName(), ReadyFrame.CODE);
        assertTrue(readyFrame.isPresent());
        assertEquals(ReadyFrame.CODE, readyFrame.get().getCode());

        assertTrue(classLauncher.isRunning());
        classLauncher.kill();
        assertFalse(classLauncher.isRunning());
    }
}

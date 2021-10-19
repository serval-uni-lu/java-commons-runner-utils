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


import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.frame.RequestFrame;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.transport.TransportListener;

import javax.jms.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MessageUtils {
    private MessageUtils() {}

    public static void sendMessageToTopic(TransportListener listener, String topicName, Frame frame) throws JMSException, NotInitializedException {
        TopicConnection connection = null;

        try{
            connection = BrokerUtils.getTopicConnection(listener);
            sendMessageToTopic(connection, topicName, frame);
        }
        finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    public static void sendMessageToTopic(TopicConnection connection, String topicName, Frame frame) throws JMSException {
        final Destination destination = new ActiveMQTopic(topicName);
        Session session = null;
        MessageProducer producer = null;

        try{
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            producer.send(session.createObjectMessage(frame));
        }
        finally {
            if(producer != null){
                producer.close();
            }

            if(session != null){
                session.close();
            }
        }
    }

    public static void sendMessageToQueue(TransportListener listener, String queueName, Frame frame) throws JMSException, NotInitializedException {
        QueueConnection connection = null;

        try{
            connection = BrokerUtils.getQueueConnection(listener);
            sendMessageToQueue(connection, queueName, frame);
        }
        finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    public static void sendMessageToQueue(QueueConnection connection, String queueName, Frame frame) throws JMSException {
        final Destination destination = new ActiveMQQueue(queueName);
        Session session = null;
        MessageProducer producer = null;

        try{
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            producer.send(toMessage(frame, session));
        }
        finally {
            if(producer != null){
                producer.close();
            }

            if(session != null){
                session.close();
            }
        }
    }

    public static Frame sendRequestSync(QueueConnection connection, String queueName, RequestFrame requestFrame) throws JMSException, InterruptedException {
        Frame responseFrame;

        final Destination destination = new ActiveMQQueue(queueName);

        Session session = null;
        MessageProducer producer = null;

        try{
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            final ResponseWaiter responseWaiter = new ResponseWaiter();
            final Destination responseDestination = session.createTemporaryQueue();
            final MessageConsumer responseConsumer = session.createConsumer(responseDestination);

            responseConsumer.setMessageListener(responseWaiter);

            final Message message = toMessage(requestFrame, session);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSReplyTo(responseDestination);
            producer.send(message);

            responseWaiter.await();

            responseFrame = responseWaiter.getResponseFrame();
        }
        finally {
            if(producer != null){
                producer.close();
            }

            if(session != null){
                session.close();
            }
        }

        return responseFrame;
    }

    public static Optional<Frame> waitForMessage(String topicName, int... code) throws JMSException, NotInitializedException, InterruptedException {
        final MessageWaiter messageWaiter = new MessageWaiter(code);

        final TopicConnection topicConnection = BrokerUtils.getTopicConnection(messageWaiter);
        final Session session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createTopic(topicName);
        final MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(messageWaiter);

        messageWaiter.await();

        session.close();
        topicConnection.close();

        return Optional.ofNullable(messageWaiter.getFrame());
    }

    public static Frame fromMessage(final Message message) throws JMSException {
        if (message instanceof ObjectMessage) {
            final Object object = ((ObjectMessage)message).getObject();

            if(Frame.class.isAssignableFrom(object.getClass())){
                final Frame frame = (Frame)object;

                if(RequestFrame.class.isAssignableFrom(object.getClass())){
                    ((RequestFrame)frame).setMessage(message);
                }

                return frame;
            }
            else {
                throw new JMSException(String.format(
                        "Excepted object implementing Frame, but got %s instead.",
                        object.getClass().getSimpleName())
                );
            }
        }

        throw new JMSException(String.format(
                "Excepted message of type ObjectMessage, but got %s instead.",
                message.getClass().getSimpleName())
        );
    }

    public static Message toMessage(final Frame frame, final Session session) throws JMSException {
        return session.createObjectMessage(frame);
    }

    private static class MessageWaiter implements TransportListener, MessageListener{
        private final int[] codes;
        private final CountDownLatch latch = new CountDownLatch(1);

        private Frame frame;

        public MessageWaiter(int... codes){
            this.codes = codes;
        }

        public Frame getFrame() {
            return frame;
        }

        public void await() throws InterruptedException {
            latch.await();
        }

        @Override
        public void onMessage(Message message) {
            try{
                final Frame candidate = fromMessage(message);

                if(Arrays.stream(codes).anyMatch(c -> c == candidate.getCode())){
                    frame = candidate;
                    latch.countDown();
                }
            }
            catch (Exception ignore) {
                //ignore
            }
        }

        @Override
        public void onCommand(Object command) {
            //ignore
        }

        @Override
        public void onException(IOException error) {
            latch.countDown();
        }

        @Override
        public void transportInterupted() {
            latch.countDown();
        }

        @Override
        public void transportResumed() {
            //ignore
        }
    }

    private static class ResponseWaiter implements MessageListener {
        private final CountDownLatch latch = new CountDownLatch(1);
        private Frame responseFrame = null;

        @Override
        public void onMessage(Message message) {
            latch.countDown();

            try {
                responseFrame = fromMessage(message);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        public Frame getResponseFrame() {
            return responseFrame;
        }

        public void await() throws InterruptedException {
            latch.await();
        }
    }
}

package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

public class MessageUtils {
    private MessageUtils() {}

    public static void sendMessageToTopic(String topicName, Frame frame) throws JMSException, NotInitializedException {
        TopicConnection connection = null;

        try{
            connection = BrokerUtils.getTopicConnection();
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

    public static void sendMessageToQueue(String queueName, Frame frame) throws JMSException, NotInitializedException {
        QueueConnection connection = null;

        try{
            connection = BrokerUtils.getQueueConnection();
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

    public static Frame waitForMessage(String topicName, int code) throws JMSException, NotInitializedException {
        final TopicConnection topicConnection = BrokerUtils.getTopicConnection();

        final Session session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createTopic(topicName);
        final MessageConsumer consumer = session.createConsumer(destination);

        Frame frame = null;

        while (frame == null){
            try{
                final Message message = consumer.receive();

                if (message instanceof ObjectMessage) {
                    final Frame candidate = (Frame) ((ObjectMessage)message).getObject();

                    if(candidate.getCode() == code){
                        frame = candidate;
                    }
                }
            }
            catch (Exception ignore) {
                //ignore
            }
        }

        session.close();
        topicConnection.close();

        return frame;
    }
}

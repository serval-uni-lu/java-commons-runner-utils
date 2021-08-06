package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

public class MessageUtils {
    private MessageUtils() {}

    public static void sendMessageToTopic(int port, String topicName, Frame frame) throws JMSException {
        sendMessageToTopic(Constants.LOCALHOST, port, topicName, frame);
    }

    public static void sendMessageToTopic(String host, int port, String topicName, Frame frame) throws JMSException {
        TopicConnection connection = null;

        try{
            connection = BrokerUtils.getTopicConnection(host, port);
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

    public static void sendMessageToQueue(int port, String queueName, Frame frame) throws JMSException {
        sendMessageToQueue(Constants.LOCALHOST, port, queueName, frame);
    }

    public static void sendMessageToQueue(String host, int port, String queueName, Frame frame) throws JMSException {
        QueueConnection connection = null;

        try{
            connection = BrokerUtils.getQueueConnection(host, port);
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
}

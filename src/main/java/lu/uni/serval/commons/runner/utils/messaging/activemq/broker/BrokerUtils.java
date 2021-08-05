package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

public class BrokerUtils {
    public static Connection connect(String host, int port) throws JMSException {
        final String brokerUrl = String.format("tcp://%s:%d", host, port);

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        final Connection connection = connectionFactory.createConnection();
        connection.start();

        return connection;
    }

    public static void sendMessageToQueue(String host, int port, String queueName, String message) throws JMSException {
        Connection connection = null;

        try{
            connection = connect(host, port);
            sendMessageToQueue(connection, queueName, message);
        }
        finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    public static void sendMessageToQueue(Connection connection, String queueName, String message) throws JMSException {
        Session session = null;
        MessageProducer producer = null;

        try{
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(new ActiveMQQueue(queueName));
            producer.send(session.createTextMessage(message));
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

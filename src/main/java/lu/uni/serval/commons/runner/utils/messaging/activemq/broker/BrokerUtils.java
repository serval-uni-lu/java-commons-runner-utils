package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class BrokerUtils {
    public static Connection getConnection(String host, int port) throws JMSException {
        final String brokerUrl = String.format("tcp://%s:%d", host, port);

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTrustAllPackages(true);

        final Connection connection = connectionFactory.createConnection();
        connection.start();

        return connection;
    }

    public static QueueConnection getQueueConnection(String host, int port) throws JMSException {
        final String brokerUrl = String.format("tcp://%s:%d", host, port);

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTrustAllPackages(true);

        final QueueConnection connection = connectionFactory.createQueueConnection();
        connection.start();

        return connection;
    }

    public static TopicConnection getTopicConnection(String host, int port) throws JMSException {
        final String brokerUrl = String.format("tcp://%s:%d", host, port);

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTrustAllPackages(true);

        final TopicConnection connection = connectionFactory.createTopicConnection();
        connection.start();

        return connection;
    }
}

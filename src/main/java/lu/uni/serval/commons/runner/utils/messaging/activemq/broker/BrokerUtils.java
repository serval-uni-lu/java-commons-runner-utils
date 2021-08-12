package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BrokerUtils {
    private BrokerUtils() {}

    public static QueueConnection getQueueConnection() throws JMSException, NotInitializedException {
        return getQueueConnection(Collections.emptyList());
    }

    public static QueueConnection getQueueConnection(Collection<String> trustedPackages) throws JMSException, NotInitializedException {
        final String brokerUrl = BrokerInfo.url();

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTrustedPackages(getTrustedPackages(trustedPackages));

        final QueueConnection connection = connectionFactory.createQueueConnection();
        connection.start();

        return connection;
    }

    public static TopicConnection getTopicConnection() throws NotInitializedException, JMSException {
        return getTopicConnection(Collections.emptyList());
    }

    public static TopicConnection getTopicConnection(Collection<String> trustedPackages) throws JMSException, NotInitializedException {
        final String brokerUrl = BrokerInfo.url();

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTrustedPackages(getTrustedPackages(trustedPackages));

        final TopicConnection connection = connectionFactory.createTopicConnection();
        connection.start();

        return connection;
    }

    private static List<String> getTrustedPackages(Collection<String> trustedPackages){
        final List<String> trusted = new ArrayList<>(trustedPackages.size() + 1);
        trusted.add("lu.uni.serval");
        trusted.addAll(trustedPackages);

        return trusted;
    }
}

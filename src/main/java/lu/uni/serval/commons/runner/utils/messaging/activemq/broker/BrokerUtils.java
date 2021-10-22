package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

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
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import javax.jms.*;
import java.util.*;

public class BrokerUtils {
    private BrokerUtils() {}

    public static QueueConnection getQueueConnection(TransportListener listener, String... trustedPackages) throws JMSException, NotInitializedException {
        final String brokerUrl = BrokerInfo.url();

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTransportListener(listener);
        connectionFactory.setTrustedPackages(getTrustedPackages(trustedPackages));

        final QueueConnection connection = connectionFactory.createQueueConnection();
        connection.start();

        return connection;
    }

    public static TopicConnection getTopicConnection(TransportListener listener, String... trustedPackages) throws JMSException, NotInitializedException {
        final String brokerUrl = BrokerInfo.url();

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setTransportListener(listener);
        connectionFactory.setTrustedPackages(getTrustedPackages(trustedPackages));

        final TopicConnection connection = connectionFactory.createTopicConnection();
        connection.start();

        return connection;
    }

    private static List<String> getTrustedPackages(String... trustedPackages){
        final List<String> trusted = new ArrayList<>(trustedPackages.length + 2);
        trusted.add("lu.uni.serval");
        trusted.add("java.util");
        trusted.addAll(Arrays.asList(trustedPackages));

        return trusted;
    }
}

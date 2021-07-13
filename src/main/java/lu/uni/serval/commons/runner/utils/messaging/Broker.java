package lu.uni.serval.commons.runner.utils.messaging;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.usage.SystemUsage;

import java.net.URI;

public class Broker {
    public static void main(String[] args) throws Exception {
        final String bindAddress = "tcp://localhost:61616";
        final String name = "testBroker";

        final BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName(name);

        brokerService.setPersistent(false);
        brokerService.setUseJmx(false);
        SystemUsage systemUsage = brokerService.getSystemUsage();
        systemUsage.getStoreUsage().setLimit(1024 * 1024 * 8);
        systemUsage.getTempUsage().setLimit(1024 * 1024 * 8);

        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI(bindAddress));
        brokerService.addConnector(connector);
        brokerService.start();

        brokerService.waitUntilStopped();
    }
}

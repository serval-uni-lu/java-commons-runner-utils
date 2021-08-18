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


import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.frame.*;
import lu.uni.serval.commons.runner.utils.messaging.socket.Sender;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.usage.SystemUsage;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.io.*;
import java.net.URI;

public class BrokerProcess implements Closeable, MessageListener {
    private static final Logger logger = LogManager.getLogger(BrokerProcess.class);
    private static final long STORAGE_LIMIT = 1024L * 1024L * 8L;

    private final BrokerService service;
    private final QueueConnection queueConnection;
    private final QueueSession queueSession;
    private final MessageConsumer queueConsumer;

    public static void main(String[] args) throws IOException {
        int remotePort = -1;

        try{
            final Options options = new Options();
            final CommandLineParser parser = new DefaultParser();

            options.addOption("m", "management", true, "Management port number");
            options.addOption("n", "name", true, "Name of the broker");
            options.addOption("b", "brokerUrl", true, "URL for the broker process");

            final CommandLine cmd = parser.parse(options, args);

            remotePort = Integer.parseInt(cmd.getOptionValue("management"));

            final String name = cmd.getOptionValue("name", "activemq-broker");
            final String brokerUrl = cmd.getOptionValue("brokerUrl");

            BrokerInfo.initialize(brokerUrl);
            try(BrokerProcess broker = new BrokerProcess(brokerUrl, name)){
                logger.info("Send ready Frame");
                Sender.sendFrame(Constants.LOCALHOST, remotePort, new ReadyBrokerFrame());
                broker.waitUntilStopped();
            }
        } catch (Exception e) {
            if(remotePort != -1){
                try {
                    Sender.sendFrame(Constants.LOCALHOST, remotePort, new ExceptionFrame(e));
                } catch (Exception ignore) {
                    //ignore
                }
            }

            logger.printf(
                    Level.ERROR,
                    "Broker terminated with error: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );

            System.exit(-1);
        }
        finally {
            Sender.sendFrame(Constants.LOCALHOST, remotePort, new StopFrame());
        }
    }

    private void waitUntilStopped() {
        this.service.waitUntilStopped();
    }

    private BrokerProcess(String bindAddress, String name) throws Exception {
        service = new BrokerService();
        service.setBrokerName(name);

        service.setPersistent(false);
        service.setUseJmx(false);

        SystemUsage systemUsage = service.getSystemUsage();
        systemUsage.getStoreUsage().setLimit(STORAGE_LIMIT);
        systemUsage.getTempUsage().setLimit(STORAGE_LIMIT);

        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI(bindAddress));
        service.addConnector(connector);

        this.service.start();
        this.service.waitUntilStarted();

        queueConnection = BrokerUtils.getQueueConnection("lu.uni.serval.commons.runner.utils.messaging.frame");
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = queueSession.createQueue(name);
        queueConsumer = queueSession.createConsumer(queue);
        queueConsumer.setMessageListener(this);
    }

    @Override
    public void close() {
        if(this.service != null){
            try {
                this.queueConsumer.close();
                this.queueSession.close();
                this.queueConnection.close();
                this.service.stop();
            } catch (Exception e) {
                logger.printf(
                        Level.ERROR,
                        "Failed to properly close broker: [%s] %s",
                        e.getClass().getSimpleName(),
                        e.getMessage()
                );
            }
        }
    }

    @Override
    public void onMessage(Message message) {
        try{
            if(message instanceof ObjectMessage){
                final Frame frame = (Frame)((ObjectMessage)message).getObject();
                if(frame.getCode() == StopFrame.CODE){
                    logger.info("Received Stop Message");
                    close();
                }
            }
        } catch (JMSException e) {
            logger.printf(
                    Level.ERROR,
                    "Failed to process JMS Message: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
    }
}

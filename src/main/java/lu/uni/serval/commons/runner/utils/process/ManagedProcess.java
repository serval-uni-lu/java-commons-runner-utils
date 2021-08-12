package lu.uni.serval.commons.runner.utils.process;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg, Renaud RWEMALIKA
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.*;
import org.apache.activemq.Closeable;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.util.Set;

public abstract class ManagedProcess implements Closeable, ExceptionListener, MessageListener {
    private static final Logger logger = LogManager.getLogger(ManagedProcess.class);

    private String name;

    private TopicConnection topicConnection;
    private TopicSession topicSession;

    private QueueConnection queueConnection;
    private QueueSession queueSession;

    private volatile boolean working = false;

    protected final void doMain(String[] args) throws Exception {
        final CommandLine cmd = processArguments(args);

        connectBroker(cmd);
        working = true;

        try {
            MessageUtils.sendMessageToTopic(topicConnection, name, new ReadyFrame());
            doWork(cmd);
        }
        finally {
            close();
        }
    }

    private CommandLine processArguments(String[] args) throws ParseException {
        final Options options = new Options();
        final CommandLineParser parser = new DefaultParser();

        final Option urlOption = new Option("brokerUrl", true, "URL of the ActiveMQ broker");
        urlOption.setRequired(true);

        final Option queueOption = new Option("name", true, "Name of the queue where to send messages");
        queueOption.setRequired(true);

        options.addOption(urlOption);
        options.addOption(queueOption);

        getOptions().forEach(options::addOption);

        return parser.parse(options, args);
    }

    private void connectBroker(CommandLine cmd) throws JMSException, AlreadyInitializedException, NotInitializedException {
        final String brokerUrl = cmd.getOptionValue("brokerUrl");
        BrokerInfo.initialize(brokerUrl);

        name = cmd.getOptionValue("name");
        queueConnection = BrokerUtils.getQueueConnection();
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = queueSession.createQueue(name);
        final MessageConsumer queueConsumer = queueSession.createConsumer(queue);
        queueConsumer.setMessageListener(this);

        topicConnection = BrokerUtils.getTopicConnection();
        topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        final Topic topic = topicSession.createTopic(Constants.TOPIC_ADMIN);
        final MessageConsumer topicConsumer = topicSession.createConsumer(topic);
        topicConsumer.setMessageListener(this);
    }

    protected boolean isWorking(){
        return working;
    }

    @Override
    public void close() throws JMSException {
        working = false;

        queueSession.close();
        queueConnection.close();

        topicSession.close();
        topicConnection.close();

        try {
            MessageUtils.sendMessageToTopic(name, new ClosingFrame());
        } catch (NotInitializedException e) {
            logger.printf(Level.ERROR,
                    "Failed to send closing frame for %s: [%s] %s",
                    this.getClass().getName(),
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
    }

    @Override
    public void onException(JMSException exception) {
        logger.printf(Level.ERROR,
                "JMS exception raised while processing: [%s] %s",
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }

    @Override
    public void onMessage(Message message) {
        try {
            if(message instanceof ObjectMessage){
                final Frame frame = (Frame)((ObjectMessage)message).getObject();
                if(frame.getCode() == StopFrame.CODE){
                    logger.info("Received Stop Message");
                    working = false;
                }
            }
        } catch (JMSException e) {
            String id;
            try {
                id = message.getJMSMessageID();
            } catch (JMSException ex) {
                id = "UNKNOWN ID";
            }

            logger.printf(Level.ERROR,
                    "Failed to process message '%s': [%s] %s",
                    id,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
    }

    protected abstract Set<Option> getOptions();
    protected abstract void doWork(CommandLine cmd) throws Exception;
}

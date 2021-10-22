package lu.uni.serval.commons.runner.utils.process;

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


import lu.uni.serval.commons.runner.utils.exception.AlreadyInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotSupportedException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerInfo;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.*;
import org.apache.activemq.Closeable;
import org.apache.activemq.transport.TransportListener;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.io.IOException;
import java.util.Set;

public abstract class ManagedProcess implements Closeable, ExceptionListener, MessageListener, TransportListener {
    private static final Logger logger = LogManager.getLogger(ManagedProcess.class);

    private String name;

    private TopicConnection topicConnection;
    private TopicSession topicSession;

    private QueueConnection queueConnection;
    private QueueSession queueSession;

    private Exception registeredException = null;

    private volatile boolean working = false;

    protected final void doMain(String[] args) {
        try {
            final CommandLine cmd = processArguments(args);

            connectBroker(cmd);
            working = true;
            MessageUtils.sendMessageToTopic(topicConnection, name, new ReadyFrame());
            doWork(cmd);
        }
        catch (Exception e){
            logger.printf(Level.ERROR,
                    "Managed process failed: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
        finally {
            close();

            if(registeredException != null){
                logger.printf(Level.ERROR,
                        "Process terminated with error: [%s] %s",
                        registeredException.getClass().getSimpleName(),
                        registeredException.getMessage()
                );

                System.exit(-1);
            }
        }

        System.exit(0);
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
        queueConnection = BrokerUtils.getQueueConnection(this);
        queueConnection.setExceptionListener(this);
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = queueSession.createQueue(name);
        final MessageConsumer queueConsumer = queueSession.createConsumer(queue);
        queueConsumer.setMessageListener(this);

        topicConnection = BrokerUtils.getTopicConnection(this);
        topicConnection.setExceptionListener(this);
        topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        final Topic topic = topicSession.createTopic(Constants.TOPIC_ADMIN);
        final MessageConsumer topicConsumer = topicSession.createConsumer(topic);
        topicConsumer.setMessageListener(this);
    }

    public String getName(){
        return this.name;
    }

    protected boolean isWorking(){
        return working;
    }

    protected void setWorking(boolean isWorking){
        working = isWorking;
    }

    protected void registerException(Exception exception){
        this.registeredException = exception;
    }

    protected void sendMessageToTopic(String topicName, Frame frame) throws JMSException {
        MessageUtils.sendMessageToTopic(topicConnection, topicName, frame);
    }

    protected void sendMessageToQueue(String queueName, Frame frame) throws JMSException {
        MessageUtils.sendMessageToQueue(queueConnection, queueName, frame);
    }

    @Override
    public void close() {
        logger.printf(Level.INFO, "Closing process %s...", getName());

        working = false;

        try {
            queueSession.close();
            queueConnection.close();

            topicSession.close();
            topicConnection.close();

            sendClosingFrame();
        }
        catch (JMSException e){
            logger.printf(Level.ERROR,
                    "Failed to send closing frame for %s: [%s] %s",
                    getName(),
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
        catch (Exception e) {
            logger.printf(Level.ERROR,
                    "Failed to properly close process %s: [%s] %s",
                    getName(),
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }

        logger.printf(Level.INFO, "Process %s is closing", getName());
    }

    void sendClosingFrame(){
        try{
            MessageUtils.sendMessageToTopic(this, name, new ClosingFrame());
        }
        catch (JMSException | NotInitializedException e){
            logger.printf(Level.INFO,
                    "Not sending Closing Frame for process %s because the connection has been terminated",
                    getName()
            );
        }
    }

    @Override
    public void onException(JMSException exception) {
        if(exception.getMessage().equals("java.io.EOFException")){
            logger.info("Lost connection to broker: Closing process");
            stop();
        }
        else {
            logger.printf(Level.ERROR,
                    "JMS exception raised while processing: [%s] %s",
                    exception.getClass().getSimpleName(),
                    exception.getMessage()
            );
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if(message instanceof ObjectMessage){
                final Frame frame = (Frame)((ObjectMessage)message).getObject();

                if(frame.getCode() == StopFrame.CODE){
                    logger.info("Received Stop Message: Closing process");
                    stop();
                }
                else if(frame.getCode() == RequestFrame.CODE){
                    final Frame responseFrame = onRequest((RequestFrame<?>) frame);
                    MessageUtils.sendResponse(queueConnection, message.getJMSReplyTo(), responseFrame, message.getJMSCorrelationID());
                }
                else{
                    onFrame(frame);
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

    @Override
    public void onCommand(Object command) {
        //ignore
    }

    @Override
    public void onException(IOException error) {
        logger.info("Lost connection to broker: Closing process");
        stop();
    }

    @Override
    public void transportInterupted() {
        logger.info("Transport interrupted: Closing process");
        stop();
    }

    @Override
    public void transportResumed() {
        //ignore
    }

    protected void onFrame(Frame frame){
        //ignore
    }

    protected Frame onRequest(RequestFrame<?> requestFrame){
        return new ErrorFrame(NotSupportedException.class, String.format(
                "Received RequestFrame for '%s', but process '%s' does not support requests",
                requestFrame.getTarget().getCanonicalName(),
                getName()
        ));
    }

    protected abstract Set<Option> getOptions();
    protected abstract void doWork(CommandLine cmd);
    protected abstract void stop();
}

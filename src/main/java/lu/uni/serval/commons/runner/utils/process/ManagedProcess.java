package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.frame.ReadyFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.StopFrame;
import org.apache.activemq.Closeable;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.util.Set;

public abstract class ManagedProcess implements Closeable, ExceptionListener, MessageListener {
    private static final Logger logger = LogManager.getLogger(ManagedProcess.class);

    private String queueName;

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
            MessageUtils.sendMessageToTopic(topicConnection, queueName, new ReadyFrame());
            doWork(cmd);
        }
        finally {
            close();
        }
    }

    private CommandLine processArguments(String[] args) throws ParseException {
        final Options options = new Options();
        final CommandLineParser parser = new DefaultParser();

        final Option portOption = new Option("brokerPort", true, "Port number of the broker");
        portOption.setRequired(true);

        final Option hostOption = new Option("brokerHost", true, "Hostname of the broker");
        hostOption.setRequired(true);

        final Option queueOption = new Option("queueName", true, "Name of the queue where to send messages");
        queueOption.setRequired(true);

        options.addOption(portOption);
        options.addOption(hostOption);
        options.addOption(queueOption);

        getOptions().forEach(options::addOption);

        return parser.parse(options, args);
    }

    private void connectBroker(CommandLine cmd) throws JMSException {
        final String brokerHost = cmd.getOptionValue("brokerHost");
        final int brokerPort = Integer.parseInt(cmd.getOptionValue("brokerPort"));

        queueName = cmd.getOptionValue("queueName");
        queueConnection = BrokerUtils.getQueueConnection(brokerHost, brokerPort);
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = queueSession.createQueue(queueName);
        final MessageConsumer queueConsumer = queueSession.createConsumer(queue);
        queueConsumer.setMessageListener(this);

        topicConnection = BrokerUtils.getTopicConnection(brokerHost, brokerPort);
        topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        final Topic topic = topicSession.createTopic(Constants.ADMIN_TOPIC);
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

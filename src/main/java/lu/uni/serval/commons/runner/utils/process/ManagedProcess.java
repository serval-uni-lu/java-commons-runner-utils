package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.messaging.activemq.broker.BrokerUtils;
import org.apache.activemq.Closeable;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.util.Set;

public abstract class ManagedProcess implements Closeable, ExceptionListener, MessageListener {
    private static final Logger logger = LogManager.getLogger(ManagedProcess.class);

    private Connection connection;
    private Session session;

    private volatile boolean working = false;

    protected final void doMain(String[] args) throws Exception {
        final CommandLine cmd = processArguments(args);

        connectBroker(cmd);
        working = true;
        try {
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
        final String queueName = cmd.getOptionValue("queueName");
        final String brokerHost = cmd.getOptionValue("brokerHost");
        final int brokerPort = Integer.parseInt(cmd.getOptionValue("brokerPort"));

        connection = BrokerUtils.connect(brokerHost, brokerPort);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        session.createQueue(queueName);
    }

    protected boolean isWorking(){
        return working;
    }

    @Override
    public void close() throws JMSException {
        working = false;
        session.close();
        connection.close();
    }

    @Override
    public void onException(JMSException exception) {

    }

    @Override
    public void onMessage(Message message) {
        try {
            if(message instanceof TextMessage){
                final String text = ((TextMessage)message).getText();
                logger.printf(Level.ERROR, "Received message: %s", text);

                if(text.equalsIgnoreCase("STOP")){
                    working = false;
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    protected abstract Set<Option> getOptions();
    protected abstract void doWork(CommandLine cmd) throws Exception;
}

package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.EndFrame;
import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.ExceptionFrame;
import lu.uni.serval.commons.runner.utils.messaging.point2point.transfer.Sender;
import lu.uni.serval.commons.runner.utils.messaging.point2point.transfer.Listener;
import lu.uni.serval.commons.runner.utils.messaging.point2point.processor.FrameProcessor;
import lu.uni.serval.commons.runner.utils.messaging.point2point.processor.FrameProcessorFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.usage.SystemUsage;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URI;

public class BrokerProcess implements Runnable, Closeable, FrameProcessorFactory {
    private static final Logger logger = LogManager.getLogger(BrokerProcess.class);

    private final BrokerService service;
    private final Socket managementSocket;

    public static void main(String[] args) {
        Socket managementSocket = null;

        try{
            final Options options = new Options();
            final CommandLineParser parser = new DefaultParser();

            options.addOption("management", true, "Management Port");
            options.addOption("bind", true, "Bind address for the broker");
            options.addOption("name", true, "Broker name");

            final CommandLine cmd = parser.parse(options, args);

            final int management = Integer.parseInt(cmd.getOptionValue("management"));
            final String bind = cmd.getOptionValue("bind", "tcp://localhost:61616");
            final String name = cmd.getOptionValue("name", "activemq-broker");

            managementSocket = new Socket("localhost", management);
            logger.printf(Level.INFO, "Connection established to management socket establish on port %d", managementSocket.getPort());

            try(BrokerProcess broker = new BrokerProcess(bind, name, managementSocket)){
                broker.start();
                broker.waitUntilStopped();
            }
        } catch (Exception e) {
            if(managementSocket != null){
                try {
                    Sender.sendFrame(managementSocket, new ExceptionFrame(e));
                } catch (Exception ignore) {}
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
            if(managementSocket != null){
                try {
                    Sender.sendFrame(managementSocket, new EndFrame("close"));
                    managementSocket.close();
                } catch (Exception e) {
                    logger.printf(
                            Level.ERROR,
                            "Failed to close management socket: [%s] %s",
                            e.getClass().getSimpleName(),
                            e.getMessage()
                    );
                }
            }
        }
    }

    private void start() throws Exception {
        this.service.start();
        this.service.waitUntilStarted();
        new Thread(this).start();

        Sender.sendFrame(managementSocket, new ReadyBrokerFrame());
    }

    private void waitUntilStopped() {
        this.service.waitUntilStopped();
    }

    private BrokerProcess(String bindAddress, String name, Socket managementSocket) throws Exception {
        this.managementSocket = managementSocket;

        service = new BrokerService();
        service.setBrokerName(name);

        service.setPersistent(false);
        service.setUseJmx(false);

        SystemUsage systemUsage = service.getSystemUsage();
        systemUsage.getStoreUsage().setLimit(1024 * 1024 * 8);
        systemUsage.getTempUsage().setLimit(1024 * 1024 * 8);

        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI(bindAddress));
        service.addConnector(connector);
    }

    @Override
    public void run() {
        try {
            logger.info("Start management listening...");
            Listener.listen(managementSocket, this);
        } catch (Exception e) {
            try {
                Sender.sendFrame(managementSocket, new ExceptionFrame(e));
            } catch (IOException ignore) {}

            logger.printf(
                    Level.ERROR,
                    "Management thread stopped abruptly: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
        finally {
            close();
        }
    }

    @Override
    public void close() {
        if(this.service != null){
            try {
                logger.error("Stopping Service service");
                this.service.stop();
            } catch (Exception e) {
                logger.error("Failed to properly stop broker");
            }
        }
    }

    @Override
    public FrameProcessor getFrameProcessor(int code) {
        if(StopBrokerFrame.CODE == code) return frame -> false;

        throw new IllegalArgumentException(String.format("Frame of type %s not supported", code));
    }
}

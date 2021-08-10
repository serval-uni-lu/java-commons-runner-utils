package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.frame.AddressFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.EndFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.ExceptionFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.StopFrame;
import lu.uni.serval.commons.runner.utils.messaging.socket.Sender;
import lu.uni.serval.commons.runner.utils.messaging.socket.Listener;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessor;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessorFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.usage.SystemUsage;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.URI;

public class BrokerProcess implements Runnable, Closeable, FrameProcessorFactory {
    private static final Logger logger = LogManager.getLogger(BrokerProcess.class);
    private static final long STORAGE_LIMIT = 1024L * 1024L * 8L;

    private final BrokerService service;
    private final ServerSocket managementSocket;
    private final int remotePort;

    public static void main(String[] args) {
        ServerSocket managementSocket = null;
        int remotePort = -1;

        try{
            final Options options = new Options();
            final CommandLineParser parser = new DefaultParser();

            options.addOption("management", true, "Management port number");
            options.addOption("name", true, "Name of the broker");
            options.addOption("host", true, "Hostname for the broker process");
            options.addOption("port", true, "Port number for the broker process");

            final CommandLine cmd = parser.parse(options, args);

            remotePort = Integer.parseInt(cmd.getOptionValue("management"));

            final String brokerHost = cmd.getOptionValue("host", Constants.LOCALHOST);
            final int brokerPort = Integer.parseInt(cmd.getOptionValue("port", String.valueOf(Constants.DEFAULT_BROKER_PORT)));
            final String brokerUrl = String.format("tcp://%s:%s", brokerHost, brokerPort);

            final String name = cmd.getOptionValue("name", "activemq-broker");

            managementSocket = new ServerSocket(0);

            try(BrokerProcess broker = new BrokerProcess(brokerUrl, name, managementSocket, remotePort)){
                broker.start();
                broker.waitUntilStarted();


                broker.waitUntilStopped();
            }
        } catch (Exception e) {
            if(remotePort != -1){
                try {
                    Sender.sendFrame(remotePort, new ExceptionFrame(e));
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
                    managementSocket.close();
                    Sender.sendFrame(remotePort, new EndFrame("close"));
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

        Sender.sendFrame(remotePort, new ReadyBrokerFrame());
    }

    private void waitUntilStarted() {
        this.service.waitUntilStarted();
    }

    private void waitUntilStopped() {
        this.service.waitUntilStopped();
    }

    private BrokerProcess(String bindAddress, String name, ServerSocket managementSocket, int remotePort) throws Exception {
        this.managementSocket = managementSocket;
        this.remotePort = remotePort;

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
    }

    @Override
    public void run() {
        try {
            logger.info("Start management listening...");
            Sender.sendFrame(remotePort, new AddressFrame(managementSocket.getLocalPort()));
            Listener.listen(managementSocket, this);
        } catch (Exception e) {
            try {
                Sender.sendFrame(remotePort, new ExceptionFrame(e));
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
                this.service.stop();
            } catch (Exception e) {
                logger.error("Failed to properly stop broker");
            }
        }
    }

    @Override
    public FrameProcessor getFrameProcessor(int code) {
        if(StopFrame.CODE == code) return frame -> false;

        throw new IllegalArgumentException(String.format("Frame of type %s not supported", code));
    }
}

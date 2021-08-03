package lu.uni.serval.commons.runner.utils.messaging;

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
import java.nio.charset.StandardCharsets;

public class Broker implements Runnable, Closeable {
    private static final Logger logger = LogManager.getLogger(Broker.class);

    private final BrokerService service;
    private final Socket socket;

    public static void main(String[] args) {
        try{
            final Options options = new Options();
            final CommandLineParser parser = new DefaultParser();

            options.addOption("management", true, "Management Port");
            options.addOption("bind", true, "Bind address for the broker");
            options.addOption("name", true, "Broker name");

            final CommandLine cmd = parser.parse(options, args);

            final int management = Integer.parseInt(cmd.getOptionValue("management"));
            final String bind = cmd.getOptionValue("bind", "tcp://localhost:61616");
            final String name = cmd.getOptionValue("name", "Activemq-broker");

            try(Broker broker = new Broker(bind, name, management)){
                broker.start();
                broker.waitUntilStopped();
            }
        } catch (Exception e) {
            logger.printf(
                    Level.ERROR,
                    "Broker terminated with error: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );

            System.exit(-1);
        }
    }

    private void start() throws Exception {
        this.service.start();
        this.service.waitUntilStarted();
        new Thread(this).start();

        String readyMessage = "READY" + System.lineSeparator();
        this.socket.getOutputStream().write(readyMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void waitUntilStopped() {
        this.service.waitUntilStopped();
    }

    private Broker(String bindAddress, String name, int managementPort) throws Exception {
        socket = new Socket("localhost", managementPort);
        logger.printf(Level.INFO, "Connection established with port %d", socket.getPort());

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
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            logger.info("Start management listening...");
            while(this.service.isStarted()){
                final String message = reader.readLine();
                if(message == null) break;

                if(message.equalsIgnoreCase("STOP")){
                    break;
                }
            }

        } catch (Exception e) {
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

        if(this.socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                logger.printf(
                        Level.ERROR,
                        "Failed to properly close management socket: [%s] %s",
                        e.getClass().getSimpleName(),
                        e.getMessage()
                );
            }
        }

    }
}

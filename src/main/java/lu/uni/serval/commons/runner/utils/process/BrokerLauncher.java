package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.messaging.Broker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class BrokerLauncher implements Closeable, Runnable {
    private static final Logger logger = LogManager.getLogger(BrokerLauncher.class);

    private final String name;
    private final String bindAddress;
    private final ClassLauncher launcher;
    private final ServerSocket serverSocket;
    private final Set<Runnable> readyRunnables;
    private volatile boolean running = true;

    private Socket socket;

    public BrokerLauncher(String name, String bindAddress) throws IOException {
        this.name = name;
        this.bindAddress = bindAddress;

        this.readyRunnables = new HashSet<>();
        this.serverSocket = new ServerSocket(0);
        this.launcher = new ClassLauncher(Broker.class);

        launcher.withFreeParameter("-management");
        launcher.withFreeParameter(String.valueOf(serverSocket.getLocalPort()));

        launcher.withFreeParameter("-name");
        launcher.withFreeParameter(name);

        launcher.withFreeParameter("-bind");
        launcher.withFreeParameter(bindAddress);
    }

    public void launch() throws IOException, InterruptedException {
        launcher.execute(false);
        socket = serverSocket.accept();
        new Thread(this).start();
    }

    public void onBrokerReady(Runnable runnable){
        readyRunnables.add(runnable);
    }

    @Override
    public void close(){
        try {
            String stopMessage = "STOP" + System.lineSeparator();
            socket.getOutputStream().write(stopMessage.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            running = false;
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            while(running){
                final String message = reader.readLine();

                if(message.equalsIgnoreCase("READY")){
                    readyRunnables.forEach(Runnable::run);
                }
            }
        } catch (Exception e) {
            logger.info("Socket closed!");
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }
}

package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

import lu.uni.serval.commons.runner.utils.messaging.activemq.Observer;
import lu.uni.serval.commons.runner.utils.messaging.frame.AddressFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.EndFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.ExceptionFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.StopFrame;
import lu.uni.serval.commons.runner.utils.messaging.socket.Sender;
import lu.uni.serval.commons.runner.utils.messaging.socket.Listener;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessor;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessorFactory;
import lu.uni.serval.commons.runner.utils.process.ClassLauncher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Broker implements Closeable, Runnable, FrameProcessorFactory {
    private static final Logger logger = LogManager.getLogger(Broker.class);

    private final String name;
    private final ClassLauncher launcher;
    private final ServerSocket serverSocket;
    private final Set<Runnable> readyRunnables;
    private final Set<Runnable> stopRunnables;
    private final Set<Consumer<Exception>> exceptionConsumers;

    private volatile int remotePort;

    public Broker(String name, String host, int port) throws IOException {
        this.name = name;

        this.readyRunnables = new HashSet<>();
        this.stopRunnables = new HashSet<>();
        this.exceptionConsumers = new HashSet<>();
        this.serverSocket = new ServerSocket(0);
        this.launcher = new ClassLauncher(BrokerProcess.class);

        launcher.withFreeParameter("-management");
        launcher.withFreeParameter(String.valueOf(serverSocket.getLocalPort()));

        launcher.withFreeParameter("-name");
        launcher.withFreeParameter(name);

        launcher.withFreeParameter("-host");
        launcher.withFreeParameter(host);

        launcher.withFreeParameter("-port");
        launcher.withFreeParameter(String.valueOf(port));
    }

    public String getName() {
        return name;
    }

    public void execute() throws IOException, InterruptedException {
        launcher.execute(false);
        new Thread(this).start();
    }

    public void executeAndWaitForReady() throws IOException, InterruptedException {
        launcher.execute(false);
        new Thread(this).start();

        final Observer observer = new Observer();
        observer.addRunner(this::onBrokerReady);
        observer.addConsumer(this::onExceptionRaised);

        observer.waitOnMessages();
    }

    public void onBrokerReady(Runnable runnable){
        readyRunnables.add(runnable);
    }

    public void onBrokerStopped(Runnable runnable){
        stopRunnables.add(runnable);
    }

    public void onExceptionRaised(Consumer<Exception> consumer){
        exceptionConsumers.add(consumer);
    }

    @Override
    public void close(){
        try {
            if(launcher.isRunning()){
                Sender.sendFrame(remotePort, new StopFrame());
            }
        } catch (IOException e) {
            logger.printf(
                    Level.ERROR,
                    "Forcibly killing broker because management socket is already closed: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );

            launcher.kill();
        }
    }

    @Override
    public void run() {
        try {
            Listener.listen(serverSocket, this);
        } catch (Exception e) {
            logger.info("Socket closed!");
        } finally {
            closeManagementSocket();
        }
    }

    public boolean isRunning() {
        return launcher.isRunning();
    }

    @Override
    public FrameProcessor getFrameProcessor(int code){
        if(EndFrame.CODE == code) return frame -> {
            launcher.kill();
            stopRunnables.forEach(Runnable::run);
            return false;
        };

        if(AddressFrame.CODE == code) return frame -> {
            this.remotePort = ((AddressFrame)frame).getPort();
            return true;
        };

        if(ExceptionFrame.CODE == code) return frame -> {
            final Exception e = ((ExceptionFrame)frame).getException();
            exceptionConsumers.forEach(c -> c.accept(e));

            logger.printf(
                    Level.ERROR,
                    "Broker terminated with error: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );

            return false;
        };

        if(ReadyBrokerFrame.CODE == code) return frame -> {
            readyRunnables.forEach(Runnable::run);
            return true;
        };

        throw new IllegalArgumentException(String.format("Frame of code %s not supported", code));
    }

    private void closeManagementSocket(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

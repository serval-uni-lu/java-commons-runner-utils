package lu.uni.serval.commons.runner.utils.messaging.activemq;

import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.AddressFrame;
import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.EndFrame;
import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.ExceptionFrame;
import lu.uni.serval.commons.runner.utils.messaging.point2point.transfer.Sender;
import lu.uni.serval.commons.runner.utils.messaging.point2point.transfer.Listener;
import lu.uni.serval.commons.runner.utils.messaging.point2point.processor.FrameProcessor;
import lu.uni.serval.commons.runner.utils.messaging.point2point.processor.FrameProcessorFactory;
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
    private final String bindAddress;
    private final ClassLauncher launcher;
    private final ServerSocket serverSocket;
    private final Set<Runnable> readyRunnables;
    private final Set<Runnable> stopRunnables;
    private final Set<Consumer<Exception>> exceptionConsumers;

    private volatile int remotePort;

    public Broker(String name, String bindAddress) throws IOException {
        this.name = name;
        this.bindAddress = bindAddress;

        this.readyRunnables = new HashSet<>();
        this.stopRunnables = new HashSet<>();
        this.exceptionConsumers = new HashSet<>();
        this.serverSocket = new ServerSocket(0);
        this.launcher = new ClassLauncher(BrokerProcess.class);

        launcher.withFreeParameter("-management");
        launcher.withFreeParameter(String.valueOf(serverSocket.getLocalPort()));

        launcher.withFreeParameter("-name");
        launcher.withFreeParameter(name);

        launcher.withFreeParameter("-bind");
        launcher.withFreeParameter(bindAddress);
    }

    public String getName() {
        return name;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void execute() throws IOException, InterruptedException {
        launcher.execute(false);
        new Thread(this).start();
    }

    public void executeAndWaitForReady() throws IOException, InterruptedException {
        launcher.execute(false);
        final Observer sync = new Observer();

        onBrokerReady(() -> {
            synchronized (sync){
                sync.touch();
                sync.notifyAll();
            }
        });

        onExceptionRaised(e -> {
            synchronized (sync){
                sync.touch();
                sync.notifyAll();
            }
        });

        new Thread(this).start();

        synchronized (sync){
            while (!sync.isTouched()){
                sync.wait();
            }
        }
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
                Sender.sendFrame("localhost", remotePort, new StopBrokerFrame());
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

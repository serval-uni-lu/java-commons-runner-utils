package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

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


import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Observer;
import lu.uni.serval.commons.runner.utils.messaging.frame.*;
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

public class BrokerManager implements Closeable, Runnable, FrameProcessorFactory {
    private static final Logger logger = LogManager.getLogger(BrokerManager.class);

    private final String name;

    private final ClassLauncher launcher;
    private final ServerSocket serverSocket;
    private final Set<Runnable> readyRunnables;
    private final Set<Runnable> stopRunnables;
    private final Set<Consumer<Exception>> exceptionConsumers;

    private volatile int remotePort;

    public BrokerManager(String name) throws IOException, NotInitializedException {
        this.name = name;

        this.readyRunnables = new HashSet<>();
        this.stopRunnables = new HashSet<>();
        this.exceptionConsumers = new HashSet<>();
        this.serverSocket = new ServerSocket(0);
        this.launcher = new ClassLauncher(BrokerProcess.class)
                .withLongNameParameter("management", String.valueOf(serverSocket.getLocalPort()))
                .withLongNameParameter("name", this.name)
                .withLongNameParameter("brokerUrl", BrokerInfo.url());
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
                Sender.sendFrame(Constants.LOCALHOST, remotePort, new StopFrame());
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

    @Override
    public Set<Class<? extends Frame>> getAllowedClasses() {
        final Set<Class<? extends Frame>> allowedClasses = new HashSet<>(4);

        allowedClasses.add(AddressFrame.class);
        allowedClasses.add(EndFrame.class);
        allowedClasses.add(ExceptionFrame.class);
        allowedClasses.add(ReadyBrokerFrame.class);

        return allowedClasses;
    }

    private void closeManagementSocket(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.printf(Level.ERROR,
                    "Failed to close the broker management socket: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
    }
}

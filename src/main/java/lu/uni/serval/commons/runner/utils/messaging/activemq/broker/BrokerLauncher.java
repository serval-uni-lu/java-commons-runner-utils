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


import lu.uni.serval.commons.runner.utils.exception.FrameCodeNotSupported;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotStartedException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Awaiter;
import lu.uni.serval.commons.runner.utils.messaging.frame.*;
import lu.uni.serval.commons.runner.utils.messaging.socket.Listener;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessor;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessorFactory;
import lu.uni.serval.commons.runner.utils.process.ClassLauncher;
import org.apache.activemq.transport.TransportListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BrokerLauncher implements Closeable, Runnable, FrameProcessorFactory, MessageListener, ExceptionListener, TransportListener {
    private static final Logger logger = LogManager.getLogger(BrokerLauncher.class);

    private final String name;

    private final ClassLauncher launcher;
    private final ServerSocket serverSocket;
    private final Set<Runnable> readyRunnables;
    private final Set<Runnable> stopRunnables;
    private final Set<Consumer<Exception>> exceptionConsumers;

    private TopicConnection topicConnection;
    private TopicSession topicSession;
    private final Object connectionLock;
    private boolean isConnected;

    public BrokerLauncher(String name) throws IOException, NotInitializedException {
        this.name = name;

        this.isConnected = false;
        this.connectionLock = new Object();
        this.readyRunnables = new HashSet<>();
        this.stopRunnables = new HashSet<>();
        this.exceptionConsumers = new HashSet<>();
        this.serverSocket = new ServerSocket(0);
        this.launcher = new ClassLauncher(BrokerProcess.class)
                .withLongNameParameter("management", String.valueOf(serverSocket.getLocalPort()))
                .withLongNameParameter("name", this.name)
                .withLongNameParameter("brokerUrl", BrokerInfo.url());

        this.stopRunnables.add(this::closeManagementSocket);
    }

    public String getName() {
        return name;
    }

    public void execute() throws IOException, InterruptedException {
        launcher.execute(false);
        new Thread(this).start();
    }

    public void executeAndWaitForReady() throws IOException, InterruptedException, NotStartedException {
        launcher.execute(false);
        new Thread(this).start();

        final Awaiter awaiter = new Awaiter();
        awaiter.listen(this::onBrokerReady);
        awaiter.listenWithArg(this::onExceptionRaised);

        if(!awaiter.waitOnMessages(15, TimeUnit.SECONDS)){
            throw new NotStartedException("Failed to start broker in the given 15 seconds!");
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
                MessageUtils.sendMessageToTopic(this, Constants.TOPIC_ADMIN, new StopFrame());
                MessageUtils.sendMessageToQueue(this, this.name, new StopFrame());
            }
        } catch (JMSException | NotInitializedException e) {
            logger.printf(
                    Level.ERROR,
                    "Forcibly killing broker because management socket is already closed: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );

            launcher.kill();
            exceptionConsumers.forEach(c -> c.accept(e));
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
    public FrameProcessor getFrameProcessor(int code) throws FrameCodeNotSupported{
        if(StopFrame.CODE == code) return frame -> {
            Awaiter.when(
                    () -> !isRunning(),
                    () -> stopRunnables.forEach(Runnable::run)
            );

            return false;
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
            try {
                synchronized (connectionLock){
                    topicConnection = BrokerUtils.getTopicConnection(this);
                    topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                    topicConnection.setExceptionListener(this);
                    isConnected = true;
                }

                final Topic topic = topicSession.createTopic(Constants.TOPIC_ADMIN);
                final MessageConsumer topicConsumer = topicSession.createConsumer(topic);
                topicConsumer.setMessageListener(this);
                readyRunnables.forEach(Runnable::run);
                return true;
            } catch (NotInitializedException | JMSException e) {
                logger.printf(
                        Level.ERROR,
                        "Failed to create channels and trigger callbacks after receiving ReadyBrokerFrame: [%s] %s",
                        e.getClass().getSimpleName(),
                        e.getMessage()
                );

                isConnected = false;
            }

            return false;
        };

        throw new FrameCodeNotSupported();
    }

    @Override
    public Set<Class<? extends Frame>> getAllowedClasses() {
        final Set<Class<? extends Frame>> allowedClasses = new HashSet<>(4);

        allowedClasses.add(AddressFrame.class);
        allowedClasses.add(StopFrame.class);
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

    private void closeTopicConnection(){
        if(!isConnected){
            return;
        }

        synchronized (connectionLock){
            try {
                isConnected = false;
                topicConnection.close();
            } catch (JMSException e) {
                //ignore
            }
        }
    }

    @Override
    public void onMessage(Message message) {
        Frame frame = null;

        try{
            if(message instanceof ObjectMessage){
                frame = (Frame)((ObjectMessage)message).getObject();
                getFrameProcessor(frame.getCode()).process(frame);
            }
        }
        catch (JMSException e) {
            logger.printf(Level.ERROR,
                    "Failed when receiving message: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
        catch (FrameCodeNotSupported e){
            logger.printf(Level.ERROR,
                    "[%s] %s not supported",
                    e.getClass().getSimpleName(),
                    frame.getClass().getSimpleName()
            );
        }
    }

    @Override
    public void onException(JMSException exception) {
        if(exception.getMessage().equals("java.io.EOFException")){
            logger.info("Broker is closing");
            closeTopicConnection();
        }
    }

    @Override
    public void onCommand(Object command) {
        //nothing to do
    }

    @Override
    public void onException(IOException error) {
        logger.info("Broker is closing");
        closeTopicConnection();
    }

    @Override
    public void transportInterupted() {
        logger.info("Transport was interrupted");
        closeTopicConnection();
    }

    @Override
    public void transportResumed() {
        logger.info("Transport is resuming");
    }
}

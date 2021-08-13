package lu.uni.serval.commons.runner.utils.messaging.socket;

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


import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.socket.processor.FrameProcessorFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {
    private Listener() {}

    private static final Logger logger = LogManager.getLogger(Listener.class);

    public static void listen(ServerSocket serverSocket, FrameProcessorFactory frameProcessorFactory) throws IOException {
        boolean isContinue = true;

        while(isContinue){
            try(Socket socket = serverSocket.accept()){
                isContinue = processMessage(socket.getInputStream(), frameProcessorFactory);
            }
            catch (IOException e) {
                logger.printf(Level.ERROR,
                        "Failed to accept packet while listening on port %d: [%s] %s%n",
                        serverSocket.getLocalPort(),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                );

                if(serverSocket.isClosed()){
                    throw e;
                }
            }
        }
    }

    private static boolean processMessage(InputStream inputStream, FrameProcessorFactory frameProcessorFactory){
        boolean isContinue = true;

        try(ObjectInputStream in = new ObjectInputStream(inputStream)){
            final Frame frame = (Frame) in.readObject();
            isContinue = frameProcessorFactory.getFrameProcessor(frame.getCode()).process(frame);
        }
        catch (EOFException e){
            logger.error("Closing socket");
            isContinue = false;
        }
        catch(IOException | IllegalArgumentException | ClassNotFoundException e) {
            logger.printf(Level.ERROR,
                    "Failed to process message: [%s] %s%n",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }

        return isContinue;
    }
}

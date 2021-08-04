package lu.uni.serval.commons.runner.utils.messaging.point2point.transfer;

import lu.uni.serval.commons.runner.utils.messaging.point2point.frame.Frame;
import lu.uni.serval.commons.runner.utils.messaging.point2point.processor.FrameProcessorFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Listener {
    private Listener() {}

    private static final Logger logger = LogManager.getLogger(Listener.class);

    public static void listen(Socket socket, FrameProcessorFactory frameProcessorFactory) throws IOException {
        boolean isContinue = true;

        while(isContinue){
            try{
                isContinue = processMessage(socket.getInputStream(), frameProcessorFactory);
            }
            catch (IOException e) {
                logger.printf(Level.ERROR,
                        "Failed to accept packet while listening on port %d: [%s] %s%n",
                        socket.getPort(),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                );

                if(socket.isClosed()){
                    throw e;
                }
            }
        }
    }

    private static boolean processMessage(InputStream inputStream, FrameProcessorFactory frameProcessorFactory){
        boolean isContinue = true;

        try(ObjectInputStream in = new ObjectInputStream(inputStream)){
            final Frame frame = (Frame) in.readObject();
            logger.error("Frame Received: " + frame.getClass().getSimpleName());
            isContinue = frameProcessorFactory.getFrameProcessor(frame.getCode()).process(frame);
        }
        catch (EOFException e){
            logger.info("Closing socket");
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

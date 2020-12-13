package lu.uni.serval.commons.runner.utils.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class InputStreamSplitter extends Thread{
    private static final Logger logger = LogManager.getLogger(InputStreamSplitter.class);

    final BufferedReader reader;
    final Set<BlockingQueue<Listener.Message>> queues = new HashSet<>();

    public InputStreamSplitter(InputStream inputStream){
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    void register(Listener listener){
        queues.add(listener.getQueue());
        listener.start();
    }

    @Override
    public void run(){
        try {
            String line;
            while ((line = reader.readLine()) != null){
                final Listener.Message message = Listener.Message.of(line);
                for(BlockingQueue<Listener.Message> queue: queues){
                    queue.add(message);
                }
            }

        } catch (IOException e) {
            logger.error(String.format("Something went wrong processing stream: [%s] %s",
                    e.getClass().getSimpleName(), e.getMessage())
            );
        } finally {
            queues.forEach(q -> q.add(Listener.Message.empty()));
            try {
                reader.close();
            } catch (IOException e) {
                logger.error(String.format("Failed to properly close stream: [%s] %s",
                        e.getClass().getSimpleName(), e.getMessage())
                );
            }
        }
    }
}

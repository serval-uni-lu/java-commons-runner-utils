package lu.uni.serval.commons.runner.utils.process;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg, Renaud RWEMALIKA
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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

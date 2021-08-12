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


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Listener extends Thread{
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    @Override
    public void run(){
        try {
            onStartListening();

            Message message;
            while (true) {
                message = queue.take();
                if(message.isEmpty() || !onMessageReceived(message.get())) break;
            }

            onEndListening();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e){
            onExceptionRaised(e);
        }
    }

    protected abstract void onStartListening();
    protected abstract boolean onMessageReceived(String string);
    protected abstract void onEndListening();
    protected abstract void onExceptionRaised(Exception e);

    public static class Message{
        private final String payload;

        private Message(String payload){
            this.payload = payload;
        }

        public static Message of(String payload) {
            return new Message(payload);
        }

        public static Message empty(){
            return new Message(null);
        }

        public boolean isEmpty(){
            return payload == null;
        }

        public String get() {
            return payload;
        }
    }
}

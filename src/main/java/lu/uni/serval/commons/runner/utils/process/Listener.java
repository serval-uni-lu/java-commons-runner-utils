package lu.uni.serval.commons.runner.utils.process;

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
                if(message.isEmpty()) break;
                if(!onMessageReceived(message.get())) break;
            }

            onEndListening();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e){
            onExceptionRaised(e);
        }
    }

    abstract protected void onStartListening();
    abstract protected boolean onMessageReceived(String string);
    abstract protected void onEndListening();
    abstract protected void onExceptionRaised(Exception e);

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

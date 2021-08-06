package lu.uni.serval.commons.runner.utils.messaging.activemq;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Observer {
    private final Set<Consumer<Runnable>> runnables = new HashSet<>();
    private final Set<Consumer<Consumer>> consumers = new HashSet<>();

    private volatile boolean touched;

    public Observer(){
        touched = false;
    }

    private void touch(){
        touched = true;
    }

    private boolean isTouched(){
        return touched;
    }

    public void addRunner(Consumer<Runnable> callback){
        runnables.add(callback);
    }

    public void addConsumer(Consumer<Consumer> callback){
        consumers.add(callback);
    }

    public void waitOnMessages() throws InterruptedException {
        final Observer sync = new Observer();

        runnables.forEach(m -> m.accept(() -> {
            synchronized (sync){
                sync.touch();
                sync.notifyAll();
            }
        }));

        consumers.forEach(m -> m.accept(e -> {
            synchronized (sync){
                sync.touch();
                sync.notifyAll();
            }
        }));

        synchronized (sync){
            while (!sync.isTouched()){
                sync.wait();
            }
        }
    }
}

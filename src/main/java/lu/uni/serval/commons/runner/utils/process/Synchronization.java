package lu.uni.serval.commons.runner.utils.process;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Synchronization {
    public enum Step{
        INITIALIZED,
        FINISHED
    }

    private final Map<Step, Set<Thread>> threads;

    public Synchronization(){
        threads = new HashMap<>(Step.values().length);

        for(Step step: Step.values()){
            threads.put(step, new HashSet<>());
        }
    }

    public void register(Step step, Synchronizable synchronizable){
        synchronizable.getThread(step).ifPresent(t -> threads.get(step).add(t));
    }

    public void waitFor(Step step) throws InterruptedException {
        for(Thread thread: threads.get(step)){
            thread.join();
        }
    }
}

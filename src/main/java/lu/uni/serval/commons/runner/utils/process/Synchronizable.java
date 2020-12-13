package lu.uni.serval.commons.runner.utils.process;

import java.util.Optional;

public interface Synchronizable {
    Optional<Thread> getThread(Synchronization.Step step);
}

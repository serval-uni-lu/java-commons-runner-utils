package lu.uni.serval.commons.runner.utils.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessSynchronizer extends Listener {
    private static final Logger logger = LogManager.getLogger(ProcessSynchronizer.class);

    private final String name;
    private final String message;

    public ProcessSynchronizer(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    protected void onStartListening() {
        logger.info(String.format("Synchronizing process '%s' on message: '%s'...",
                name, message));
    }

    @Override
    protected boolean onMessageReceived(String string) {
        return !string.contains(message);
    }

    @Override
    protected void onEndListening(){
        logger.info(String.format("Process '%s' synchronized!", name));
    }

    @Override
    protected void onExceptionRaised(Exception e) {

    }
}

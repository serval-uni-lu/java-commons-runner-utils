package lu.uni.serval.commons.runner.utils.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessLogger extends Listener{
    private static final Logger logger = LogManager.getLogger(ProcessSynchronizer.class);

    private final String name;

    public ProcessLogger(String name) {
        this.name = name;
    }

    @Override
    protected void onStartListening() {
        logger.info(String.format("Process '%s' is started", name));
    }

    @Override
    protected boolean onMessageReceived(String line) {
        logger.debug(line);

        if(line.contains("ERROR")){
            logger.error(String.format(" [process:%s] %s", name, line));
        }

        return true;
    }

    @Override
    protected void onEndListening() {
        logger.info(String.format("Process '%s' is ready to terminated", name));
    }

    @Override
    protected void onExceptionRaised(Exception e) {
        logger.error(String.format("Something went wrong when reading stream from process '%s': [%s] %s",
                name, e.getClass().getSimpleName(), e.getMessage()));
    }
}

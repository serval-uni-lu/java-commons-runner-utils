package lu.uni.serval.commons.runner.utils.process;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessLogger extends Listener{
    private static final Logger logger = LogManager.getLogger(ProcessLogger.class);

    private final String name;

    public ProcessLogger(String name) {
        this.name = name;
    }

    @Override
    protected void onStartListening() {
        logger.printf(Level.INFO, "Process '%s' is started", name);
    }

    @Override
    protected boolean onMessageReceived(String line) {
        logger.debug(line);

        if(line.contains("ERROR")){
            logger.printf(Level.ERROR, " [process:%s] %s", name, line);
        }

        return true;
    }

    @Override
    protected void onEndListening() {
        logger.printf(Level.INFO,"Process '%s' is ready to terminated", name);
    }

    @Override
    protected void onExceptionRaised(Exception e) {
        logger.printf(Level.ERROR,
                "Something went wrong when reading stream from process '%s': [%s] %s",
                name, e.getClass().getSimpleName(),
                e.getMessage()
        );
    }
}

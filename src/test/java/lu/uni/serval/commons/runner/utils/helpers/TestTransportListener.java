package lu.uni.serval.commons.runner.utils.helpers;

import org.apache.activemq.transport.TransportListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TestTransportListener implements TransportListener {
    Logger logger = LogManager.getLogger(TestTransportListener.class);

    @Override
    public void onCommand(Object command) {
        logger.printf(Level.INFO, "onCommand: %s", command);
    }

    @Override
    public void onException(IOException error) {
        logger.printf(
                Level.ERROR,
                "transport exception raised: [%s] %s",
                error.getClass().getSimpleName(),
                error.getMessage()
        );
    }

    @Override
    public void transportInterupted() {
        logger.error("Transport Interrupted");
    }

    @Override
    public void transportResumed() {
        logger.error("Transport Resumed");
    }
}

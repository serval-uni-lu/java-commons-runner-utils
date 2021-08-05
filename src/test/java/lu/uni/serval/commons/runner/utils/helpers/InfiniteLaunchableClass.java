package lu.uni.serval.commons.runner.utils.helpers;

import lu.uni.serval.commons.runner.utils.process.ManagedProcess;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;

public class InfiniteLaunchableClass extends ManagedProcess {
    private final static Logger logger = LogManager.getLogger(InfiniteLaunchableClass.class);

    public static void main(String[] args) {
        try {
            new InfiniteLaunchableClass().doMain(args);
        } catch (Exception e) {
            logger.printf(Level.ERROR,
                    "Process terminated with error: [%s] %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );

            System.exit(-1);
        }
    }

    @Override
    protected Set<Option> getOptions() {
        return Collections.emptySet();
    }

    @Override
    protected void doWork(CommandLine cmd) throws Exception {
        while (isWorking()){
            Thread.sleep(100);
        }
    }
}

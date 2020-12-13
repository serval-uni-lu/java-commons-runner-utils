package lu.uni.serval.commons.runner.utils.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger extends Listener {
    private static final Logger logger = LogManager.getLogger(FileLogger.class);

    private final File output;
    private final boolean returnToLine;
    private BufferedWriter writer = null;

    public FileLogger(File output, boolean returnToLine) {
        this.output = output;
        this.returnToLine = returnToLine;
    }

    @Override
    protected void onStartListening() {
        try {
            if(output != null){
                if(output.exists()){
                    output.delete();
                }

                writer = new BufferedWriter(new FileWriter(output));
            }
        } catch (IOException e) {
            logger.error(String.format("Failed to create file writer to log into '%s': [%s] %s",
                    output.getAbsolutePath(),
                    e.getClass().getSimpleName(),
                    e.getMessage()
            ));
            writer = null;
        }
    }

    @Override
    protected boolean onMessageReceived(String string) {
        if(writer != null){
            try {
                writer.write(string);
                if(returnToLine) writer.newLine();
            } catch (IOException e) {
                logger.error(String.format("Failed to write text '%s' to file '%s': [%s] %s",
                        string,
                        output.getAbsolutePath(),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                ));
            }
        }

        return true;
    }

    @Override
    protected void onEndListening() {
        close();
    }

    @Override
    protected void onExceptionRaised(Exception e) {
        close();
    }

    private void close(){
        if(writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("Failed to close file writer for " + output.getAbsolutePath());
            }
        }
    }
}

package lu.uni.serval.commons.runner.utils.process;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

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
                    Files.delete(output.toPath());
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
                writer.flush();
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
                logger.printf(Level.ERROR,
                        "Failed to close file writer for %s",
                        output.getAbsolutePath()
                );
            }
        }
    }
}

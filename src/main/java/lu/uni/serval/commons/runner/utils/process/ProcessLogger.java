package lu.uni.serval.commons.runner.utils.process;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg, Renaud RWEMALIKA
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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

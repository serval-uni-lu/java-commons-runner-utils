package lu.uni.serval.commons.runner.utils.helpers;

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

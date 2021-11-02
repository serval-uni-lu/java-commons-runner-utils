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

import lu.uni.serval.commons.runner.utils.messaging.activemq.MessageUtils;
import lu.uni.serval.commons.runner.utils.messaging.frame.RequestFrame;
import lu.uni.serval.commons.runner.utils.messaging.frame.TextFrame;
import lu.uni.serval.commons.runner.utils.process.ManagedProcess;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RequestClass extends ManagedProcess {
    private static final Logger logger = LogManager.getLogger(RequestClass.class);

    public static void main(String[] args) {
        new RequestClass().doMain(args);
    }

    @Override
    protected Set<Option> getOptions() {
        final Set<Option> options = new HashSet<>();
        options.add(new Option("t", "text", true, "Text to process"));
        options.add(new Option("q", "workerQueueName", true,"Where to send to request"));

        return options;
    }

    @Override
    protected void doWork(CommandLine cmd) {
        final Properties options = new Properties();
        options.setProperty("text", cmd.getOptionValue("text"));

        final RequestFrame<TextFrame> requestFrame = new RequestFrame<>(TextFrame.class, options);

        try {
            final TextFrame responseFrame = MessageUtils.sendRequestSync(cmd.getOptionValue("workerQueueName"), requestFrame, 5, TimeUnit.SECONDS);
            System.out.println(responseFrame.getText());
        } catch (Exception e) {
            logger.printf(
                    Level.ERROR,
                    "[%s]%s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }
    }

    @Override
    protected void stop() {
        setWorking(false);
    }
}

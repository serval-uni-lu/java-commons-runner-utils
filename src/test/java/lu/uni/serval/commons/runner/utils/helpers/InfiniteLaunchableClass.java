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

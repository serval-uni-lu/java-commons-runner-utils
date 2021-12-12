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

import lu.uni.serval.commons.runner.utils.version.Version;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScriptRunner {
    public enum Stage {
        BEFORE,
        AFTER
    }

    private static final Logger logger = LogManager.getLogger(ScriptRunner.class);

    private final Version version;
    private final Stage stage;

    public ScriptRunner(Version version, Stage stage) {
        this.version = version;
        this.stage = stage;
    }

    public void run() throws IOException, InterruptedException {
        final String beforeBuildCommand = version.getMavenConfiguration().getBeforeBuild();

        if(!beforeBuildCommand.isEmpty()){
            logger.printf(Level.INFO, "Start %s Build Script...", stage.name());
            final String name = String.format("Before Build [%s]", beforeBuildCommand.split("\\s+", 2)[0]);
            new ScriptLauncher(name, beforeBuildCommand, version.getMavenConfiguration().getFolder()).executeSync(1, TimeUnit.HOURS);
            logger.printf(Level.INFO, "Finish %s Build Script", stage.name());
        }
    }
}

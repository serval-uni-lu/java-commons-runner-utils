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

import lu.uni.serval.commons.runner.utils.configuration.ScriptConfiguration;
import lu.uni.serval.commons.runner.utils.configuration.Variables;
import lu.uni.serval.commons.runner.utils.version.Version;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScriptRunner {
    public enum Stage {
        BEFORE,
        AFTER
    }

    private static final Logger logger = LogManager.getLogger(ScriptRunner.class);

    private final Version<?> version;
    private final Stage stage;

    public ScriptRunner(Version<?> version, Stage stage) {
        this.version = version;
        this.stage = stage;
    }

    public void run() throws IOException, InterruptedException {
        final ScriptConfiguration configuration = getConfiguration();
        final File directory = getDirectory(configuration);

        if(!configuration.getCommand().isEmpty()){
            logger.printf(Level.INFO, "Start %s Build Script...", stage.name());
            final String name = String.format("Before Build [%s]", configuration.getCommand().split("\\s+", 2)[0]);
            new ScriptLauncher(name, configuration.getCommand(), directory).executeSync(1, TimeUnit.HOURS);
            logger.printf(Level.INFO, "Finish %s Build Script", stage.name());
        }
    }

    private ScriptConfiguration getConfiguration() throws IOException {
        ScriptConfiguration configuration;

        if(stage == Stage.BEFORE){
            configuration = version.getBuildConfiguration().getBeforeBuild();
        }
        else if(stage == Stage.AFTER) {
            configuration = version.getBuildConfiguration().getAfterBuild();
        }
        else{
            throw new IOException(String.format("Script stage %s is not implemented", stage.name()));
        }

        return configuration;
    }

    private File getDirectory(ScriptConfiguration configuration){
        final String pathName = configuration.getResolved(configuration.getDirectory());

        File directory = new File(pathName);

        if(!directory.isAbsolute()){
            String configurationFolder = configuration.getResolved("{" + Variables.CONFIGURATION_FOLDER + "}");
            directory = new File(configurationFolder, pathName);
        }

        return directory;
    }
}

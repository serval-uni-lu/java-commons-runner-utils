package lu.uni.serval.commons.runner.utils.configuration;

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


import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationParser {
    private ConfigurationParser() {}

    private static final Logger logger = LogManager.getLogger(ConfigurationParser.class);

    public static <T extends Configuration, U extends BuildConfiguration> T parse(String config, Class<T> main, Class<U> build) throws IOException {
        File file = new File(config);

        if(!file.exists()){
            throw new IOException(String.format("Configuration file '%s' does not exist!", file.getAbsolutePath()));
        }

        final SimpleModule buildModule = new SimpleModule("BuildModule");
        buildModule.addDeserializer(BuildConfiguration.class, new BuildDeserializer<>(build));

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new Jdk8Module(), buildModule);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        final T configuration = mapper.readValue(file, main);

        configuration.registerVariable( Variables.CONFIGURATION_FOLDER, file.getParentFile().getAbsolutePath());

        logger.printf(Level.INFO,
                "Configuration loaded from '%s'",
                config
        );

        return configuration;
    }

    public static class BuildDeserializer<U extends BuildConfiguration> extends JsonDeserializer<U> {
        private final Class<U> build;

        public BuildDeserializer(Class<U> build) {
            this.build = build;
        }

        @Override
        public U deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return jp.readValueAs(build);
        }
    }
}

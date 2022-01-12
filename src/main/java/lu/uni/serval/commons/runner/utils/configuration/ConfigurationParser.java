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
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
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
        mapper.registerModules(new Jdk8Module(), buildModule, new FolderModule<>(file.getParentFile(), main));
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        final T configuration = mapper.readValue(file, main);

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


    public static class FolderModule<T extends Configuration> extends SimpleModule {
        private final Class<T> type;
        private final File folder;

        FolderModule(File folder, Class<T> type){
            this.folder = folder;
            this.type = type;
        }

        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.addBeanDeserializerModifier(new BeanDeserializerModifier()
            {
                @Override public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
                {
                    if (type.isAssignableFrom(beanDesc.getBeanClass())){
                        return new Modifier<>(type, (JsonDeserializer<T>)deserializer, folder);
                    }

                    return deserializer;
                }
            });
        }
    }

    public static class Modifier<T extends Configuration> extends StdDeserializer<T> implements ResolvableDeserializer{
        private final transient JsonDeserializer<T> defaultDeserializer;
        private final File folder;

        public Modifier(Class<T> type, JsonDeserializer<T> defaultDeserializer, File folder) {
            super(type);

            this.defaultDeserializer = defaultDeserializer;
            this.folder = folder;
        }

        @Override
        public void resolve(DeserializationContext context) throws JsonMappingException {
            ((ResolvableDeserializer) defaultDeserializer).resolve(context);
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext context) throws IOException {
            T configuration = defaultDeserializer.deserialize(p, context);
            configuration.setFolder(this.folder);

            return configuration;
        }
    }

}

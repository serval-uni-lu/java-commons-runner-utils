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


import lu.uni.serval.commons.runner.utils.configuration.Configuration;
import lu.uni.serval.commons.runner.utils.configuration.ConfigurationParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class Helpers {
    public static <T extends Configuration> T parseConfiguration(String resources, Class<T> type){
        try {
            final URL resourceUrl = Helpers.class.getClassLoader().getResource(resources);
            final String path = Paths.get(resourceUrl.toURI()).toFile().getAbsolutePath();

            return ConfigurationParser.parse(path, type);
        } catch (IOException | URISyntaxException e) {
            fail(String.format(
                    "Failed to parse configuration %s: [%s] %s",
                    resources,
                    e.getClass().getSimpleName(),
                    e.getMessage())
            );
        }

        return null;
    }
}

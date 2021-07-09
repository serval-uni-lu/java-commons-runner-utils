package lu.uni.serval.commons.runner.utils.helpers;

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

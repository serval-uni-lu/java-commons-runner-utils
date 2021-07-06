package lu.uni.serval.commons.runner.utils.configuration;

import lu.uni.serval.commons.runner.utils.Helpers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GitConfigurationTest {
    @Test
    void testMinimalConfiguration() throws IOException {
        Helpers.parseConfiguration("configurations/config-git-1.json", GitConfiguration.class);
    }
}
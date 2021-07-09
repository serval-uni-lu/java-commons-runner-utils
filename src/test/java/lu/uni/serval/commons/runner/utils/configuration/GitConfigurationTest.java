package lu.uni.serval.commons.runner.utils.configuration;

import lu.uni.serval.commons.git.utils.Frequency;
import lu.uni.serval.commons.runner.utils.helpers.Helpers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitConfigurationTest {
    @Test
    void testMinimalConfiguration() {
        final GitConfiguration configuration = Helpers.parseConfiguration("configurations/config-git-1.json", GitConfiguration.class);
        assertNotNull(configuration);

        assertEquals(1, configuration.getRepositories().size());
        final RepositoryConfiguration repositoryConfiguration = configuration.getRepositories().iterator().next();
        assertEquals("https://github.com/kabinja/simple-spring-web.git", repositoryConfiguration.getLocation());
        assertEquals(Frequency.LATEST, repositoryConfiguration.getFrequency());
    }
}
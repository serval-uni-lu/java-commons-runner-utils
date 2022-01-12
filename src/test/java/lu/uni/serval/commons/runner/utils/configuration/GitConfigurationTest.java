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


import lu.uni.serval.commons.git.utils.Frequency;
import lu.uni.serval.commons.runner.utils.build.maven.MavenConfiguration;
import lu.uni.serval.commons.runner.utils.helpers.Helpers;
import lu.uni.serval.commons.runner.utils.helpers.SimpleConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitConfigurationTest {
    @Test
    void testMinimalConfiguration() {
        final SimpleConfiguration configuration = Helpers.parseConfiguration("configurations/config-git-1.json", SimpleConfiguration.class, MavenConfiguration.class);
        assertNotNull(configuration);

        assertEquals(1, configuration.getGit().getRepositories().size());
        final RepositoryConfiguration<MavenConfiguration> repositoryConfiguration = configuration.getGit().getRepositories().iterator().next();
        assertEquals("https://github.com/kabinja/simple-spring-web.git", repositoryConfiguration.getLocation());
        assertEquals(Frequency.LATEST, repositoryConfiguration.getFrequency());
        assertEquals("clean", repositoryConfiguration.getBuildConfiguration().getGoals().get(0));
    }
}

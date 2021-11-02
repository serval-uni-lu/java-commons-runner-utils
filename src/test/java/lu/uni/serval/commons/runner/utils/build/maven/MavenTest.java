package lu.uni.serval.commons.runner.utils.build.maven;

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

import lu.uni.serval.commons.runner.utils.helpers.Helpers;
import lu.uni.serval.commons.runner.utils.version.Version;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MavenTest {
    private static Version dummyMultiModules;
    private static Version dummySingleModule;

    @BeforeAll
    static void initializeVersions() throws URISyntaxException {
        final File dummyMultiModulesFolder = Helpers.getResourcesFile("dummy-maven-multi-module");

        dummyMultiModules = new Version(
                "dummy-maven-multi-module",
                dummyMultiModulesFolder,
                LocalDateTime.now(),
                "1",
                "",
                null
                );

        final File dummySingleModuleFolder = Helpers.getResourcesFile("dummy-maven-single-module");

        dummySingleModule = new Version(
                "dummy-maven-single-module",
                dummySingleModuleFolder,
                LocalDateTime.now(),
                "1",
                "",
                null
        );
    }

    @Test
    void testModuleNameDiscoveryMultiModule() throws IOException, InterruptedException {
        final Maven maven = new Maven(dummyMultiModules);
        final List<ModuleInfo> moduleInfoList = maven.getModuleInfoList();

        assertEquals(3, moduleInfoList.size());
        assertEquals("dummy-project", moduleInfoList.get(0).getArtifactId());
        assertEquals("pom", moduleInfoList.get(0).getPackaging());

        assertTrue(moduleInfoList.stream().anyMatch(m -> m.getArtifactId().equals("module1")));
        assertTrue(moduleInfoList.stream().anyMatch(m -> m.getArtifactId().equals("module2")));
    }

    @Test
    void testModuleNameDiscoverySingleModule() throws IOException, InterruptedException {
        final Maven maven = new Maven(dummySingleModule);
        final List<ModuleInfo> moduleNames = maven.getModuleInfoList();

        assertEquals(1, moduleNames.size());
        assertEquals("dummy-project", moduleNames.get(0).getArtifactId());
        assertEquals("jar", moduleNames.get(0).getPackaging());
    }
}

package lu.uni.serval.commons.runner.utils.build.maven;

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
        final List<String> moduleNames = maven.getModuleNames();

        assertEquals(3, moduleNames.size());
        assertEquals("dummy-project", moduleNames.get(0));
        assertTrue(moduleNames.contains("module1"));
        assertTrue(moduleNames.contains("module2"));
    }

    @Test
    void testModuleNameDiscoverySingleModule() throws IOException, InterruptedException {
        final Maven maven = new Maven(dummySingleModule);
        final List<String> moduleNames = maven.getModuleNames();

        assertEquals(1, moduleNames.size());
        assertEquals("dummy-project", moduleNames.get(0));
    }
}
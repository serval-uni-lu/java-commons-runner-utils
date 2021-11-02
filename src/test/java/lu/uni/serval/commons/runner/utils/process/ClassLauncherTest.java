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


import lu.uni.serval.commons.runner.utils.helpers.*;
import lu.uni.serval.commons.runner.utils.listener.StringLogger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ClassLauncherTest {
    @Test
    void testLaunchProcessFromClass() throws IOException, InterruptedException {
        final StringLogger stringLogger = new StringLogger();
        final ClassLauncher classLauncher = new ClassLauncher(SimpleLaunchableClass.class);

        classLauncher.addListener(stringLogger);
        classLauncher.executeSync(20, TimeUnit.SECONDS);

        assertTrue(stringLogger.getOut().contains("Hello from process with arguments: []"));
        assertEquals("", stringLogger.getErr().trim());
    }

    @Test
    void testAddToPathEnv() throws URISyntaxException, IOException, InterruptedException {
        final URL resourceUrl = Helpers.class.getClassLoader().getResource("configurations");
        final File path = Paths.get(resourceUrl.toURI()).toFile();

        final StringLogger stringLogger = new StringLogger();
        final ClassLauncher classLauncher = new ClassLauncher(PrintPathClass.class);

        classLauncher.addListener(stringLogger);
        classLauncher.addPathEntry(path);
        classLauncher.executeSync(20, TimeUnit.SECONDS);

        System.out.println(stringLogger.getOut());

        assertTrue(stringLogger.getOut().contains(path.getAbsolutePath()));
        assertEquals("", stringLogger.getErr().trim());
    }

    @Test
    void testTimeout() throws IOException, InterruptedException {
        final ClassLauncher classLauncher = new ClassLauncher(InfiniteClass.class);
        classLauncher.executeSync(1, TimeUnit.SECONDS);
        assertFalse(classLauncher.isRunning());
    }
}

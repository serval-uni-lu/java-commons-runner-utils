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


import lu.uni.serval.commons.runner.utils.helpers.SimpleLaunchableClass;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClassLauncherTest {
    @Test
    void launchProcessFromClass() throws IOException, InterruptedException {
        final StringLogger stringLogger = new StringLogger();
        final ClassLauncher classLauncher = new ClassLauncher(SimpleLaunchableClass.class);

        classLauncher.addListener(stringLogger);
        classLauncher.execute(true);

        assertEquals("Hello from process with arguments: []", stringLogger.getOut().trim());
        assertEquals("", stringLogger.getErr().trim());
    }
}

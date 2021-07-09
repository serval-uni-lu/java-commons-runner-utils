package lu.uni.serval.commons.runner.utils.process;

import lu.uni.serval.commons.runner.utils.helpers.InfiniteLaunchableClass;
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

    @Test
    void killProcessRunningForEver() throws IOException, InterruptedException {
        final ClassLauncher classLauncher = new ClassLauncher(InfiniteLaunchableClass.class);
        classLauncher.execute(false);
        assertTrue(classLauncher.isRunning());
        classLauncher.kill();
        assertFalse(classLauncher.isRunning());
    }
}
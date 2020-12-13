package lu.uni.serval.commons.runner.utils.os;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class OsUtils {
    private static final String OS  = System.getProperty("os.name");

    public static boolean isWindows() {
        return OS.startsWith("Windows");
    }

    public static File getTmpFolder() throws IOException {
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"), "git-provider");

        if(tmpFolder.exists()){
            FileUtils.deleteDirectory(tmpFolder);
        }

        if(!tmpFolder.mkdir()){
            throw new IOException(String.format("Failed to create directory: %s", tmpFolder.getAbsolutePath()));
        }

        return tmpFolder;
    }
}

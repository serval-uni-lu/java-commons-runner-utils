package lu.uni.serval.commons.runner.utils.os;

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


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class OsUtils {
    private OsUtils() {}

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

    public static String addValueToPath(String path, String value){
        String delimiter = "";
        path = path == null ? "" : path;

        if(!path.trim().isEmpty()){
            delimiter = isWindows() ? ";" : ":";
        }

        return value + delimiter + path;
    }
}

package lu.uni.serval.commons.runner.utils.version;

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


import lu.uni.serval.commons.runner.utils.configuration.MavenConfiguration;

import java.io.File;
import java.time.LocalDateTime;

public class Version {
    private final String id;
    private final File location;
    private final LocalDateTime date;
    private final String commitId;
    private final String difference;
    private final MavenConfiguration mavenConfiguration;

    public Version(String id, File location, LocalDateTime date, String commitId, String difference, MavenConfiguration mavenConfiguration) {
        this.id = id;
        this.location = location;
        this.date = date;
        this.commitId = commitId;
        this.difference = difference;
        this.mavenConfiguration = mavenConfiguration;
    }

    public String getId() {
        return id;
    }

    public File getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getDifference() {
        return difference;
    }

    public MavenConfiguration getMavenConfiguration() {
        return mavenConfiguration;
    }
}

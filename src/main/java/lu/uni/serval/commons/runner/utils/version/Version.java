package lu.uni.serval.commons.runner.utils.version;

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

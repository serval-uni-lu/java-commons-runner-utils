package lu.uni.serval.commons.runner.utils.messaging.frame;

import java.time.LocalDateTime;

public class VersionFrame implements Frame {
    public static final int CODE = VersionFrame.class.getCanonicalName().hashCode();

    private final String project;
    private final LocalDateTime date;
    private final String commitId;
    private final String difference;

    public VersionFrame(String project, LocalDateTime date, String commitId, String difference) {
        this.project = project;
        this.date = date;
        this.commitId = commitId;
        this.difference = difference;
    }

    public String getProject() {
        return project;
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

    @Override
    public int getCode() {
        return CODE;
    }
}

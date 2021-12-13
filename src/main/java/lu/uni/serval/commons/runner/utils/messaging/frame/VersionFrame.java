package lu.uni.serval.commons.runner.utils.messaging.frame;

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


import java.time.LocalDateTime;

public class VersionFrame implements Frame {
    public static final int CODE = VersionFrame.class.getCanonicalName().hashCode();

    private final String project;
    private final LocalDateTime date;
    private final String commitId;
    private final String difference;
    private final String previousProject;
    private final String previousCommitId;

    public VersionFrame(String project, LocalDateTime date, String commitId, String difference, String previousProject, String previousCommitId) {
        this.project = project;
        this.date = date;
        this.commitId = commitId;
        this.difference = difference;
        this.previousProject = previousProject;
        this.previousCommitId = previousCommitId;
    }

    public VersionFrame(String project, LocalDateTime date, String commitId){
        this(project, date, commitId, "", "", "");
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

    public String getPreviousProject() {
        return previousProject;
    }

    public String getPreviousCommitId() {
        return previousCommitId;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}

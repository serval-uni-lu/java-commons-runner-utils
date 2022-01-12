package lu.uni.serval.commons.runner.utils.build.maven;

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

import lu.uni.serval.commons.runner.utils.exception.InvalidFrameException;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;
import lu.uni.serval.commons.runner.utils.exception.ResponseException;
import lu.uni.serval.commons.runner.utils.messaging.activemq.Constants;
import lu.uni.serval.commons.runner.utils.version.Version;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public abstract class MavenRunner {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    private final String phase;
    private final Maven maven;
    private final Version<MavenConfiguration> version;
    private final File logsFolder;

    protected MavenRunner(String phase, Version<MavenConfiguration> version, File logsFolder) {
        this.phase = phase;
        this.version = version;
        this.maven = new Maven(this.version);
        this.logsFolder = logsFolder;
    }

    protected Maven getMaven() {
        return maven;
    }

    protected Version<MavenConfiguration> getVersion() {
        return version;
    }

    protected String getFormattedDate(){
        return this.version.getDate().format(formatter);
    }

    protected String getMavenJobName(String phase){
        return String.format("%s-%s%s.log",
                phase,
                version.getLocation().getName(),
                version.getCommitId().isEmpty() ? "" : "-" + version.getCommitId()
        ).replace(".git", "");
    }

    protected MavenLauncher getMavenLauncher(String jobId){
        final File logFile = new File(logsFolder, getMavenJobName(phase) + (jobId.trim().isEmpty() ? "" : "-" + jobId));

        return new MavenLauncher()
                .withJavaParameter("maven.test.failure.ignore", "true")
                .usingJavaVersion(new File(version.getBuildConfiguration().getJavaHome()))
                .withMavenOptions(version.getBuildConfiguration().getMavenOptions())
                .withJavaToolOptions(version.getBuildConfiguration().getJavaToolOptions())
                .withEnvironmentVariables(version.getBuildConfiguration().getEnvironment())
                .withJavaParameters(version.getBuildConfiguration().getArguments())
                .usingProfile(version.getBuildConfiguration().getProfiles())
                .inDirectory(version.getLocation())
                .writeOutputTo(logFile);
    }

    protected MavenLauncher getMavenLauncher(){
        return getMavenLauncher("");
    }

    public abstract void run() throws IOException, InterruptedException, NotInitializedException, JMSException, TimeoutException, ResponseException, InvalidFrameException, SAXException, DocumentException;
}

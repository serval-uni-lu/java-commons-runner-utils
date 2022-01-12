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

import lu.uni.serval.commons.runner.utils.os.OsUtils;
import lu.uni.serval.commons.runner.utils.version.Version;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Maven {
    private final Version<MavenConfiguration> version;

    public Maven(Version<MavenConfiguration> version) {
        this.version = version;
    }

    public void addAgentToPom(String agent) throws DocumentException, IOException, SAXException {
        PomUtils.modifyArgLineAgent(this.version.getLocation(), agent, PomUtils.Action.ADD);
    }

    public void removeAgentFromPom(String agent) throws DocumentException, IOException, SAXException {
        PomUtils.modifyArgLineAgent(this.version.getLocation(), agent, PomUtils.Action.REMOVE);
    }

    public List<ModuleInfo> getModuleInfoList() throws IOException, InterruptedException {
        final String prefix = "[MODULE]";
        final String command = OsUtils.isWindows() ? "/c,echo,":"";
        final String info = "${project.artifactId}:::${project.packaging}";
        final String payload = String.format("%s%s%s", command, prefix, info);

        final String output = getLauncher()
                .withJavaParameter("exec.executable", OsUtils.isWindows() ? "cmd" : "echo")
                .withJavaParameter("exec.args", payload)
                .forGoals("exec:exec", "-q")
                .executeSync(30, TimeUnit.SECONDS);


        final List<ModuleInfo> moduleInfos = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new StringReader(output));

        String line;
        while ((line = reader.readLine()) != null){
            if(line.startsWith(prefix)){
                final String[] particles = line.replace(prefix, "").trim().split(":::");

                if(particles.length == 2){
                    moduleInfos.add(new ModuleInfo(particles[0], particles[1]));
                }
            }
        }

        return moduleInfos;
    }

    private MavenLauncher getLauncher(){
        return new MavenLauncher().inDirectory(this.version.getLocation());
    }
}

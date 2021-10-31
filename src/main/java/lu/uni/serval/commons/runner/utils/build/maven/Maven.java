package lu.uni.serval.commons.runner.utils.build.maven;

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
    private final Version version;

    public Maven(Version version) {
        this.version = version;
    }

    public void writeAgentToPom(String agent) throws DocumentException, IOException, SAXException {
        PomUtils.writeAgentToPom(this.version.getLocation(), agent);
    }

    public List<String> getModuleNames() throws IOException, InterruptedException {
        final String prefix = "[MODULE]";
        final String output = getLauncher()
                .withJavaParameter("exec.executable", OsUtils.isWindows() ? "cmd" : "echo")
                .withJavaParameter("exec.args", (OsUtils.isWindows() ? "/c,echo,":"") + prefix + "${project.artifactId}")
                .forGoals("exec:exec", "-q")
                .executeSync(30, TimeUnit.SECONDS);

        final List<String> moduleNames = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new StringReader(output));

        String line;
        while ((line = reader.readLine()) != null){
            if(line.startsWith(prefix)){
                moduleNames.add(line.replace(prefix, "").trim());
            }
        }

        return moduleNames;
    }

    private MavenLauncher getLauncher(){
        return new MavenLauncher().inDirectory(this.version.getLocation());
    }
}

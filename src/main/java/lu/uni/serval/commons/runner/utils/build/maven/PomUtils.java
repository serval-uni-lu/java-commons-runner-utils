package lu.uni.serval.commons.runner.utils.build.maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.xml.sax.SAXException;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

class PomUtils {
    private PomUtils() {}

    public static void writeAgentToPom(File projectFolder, String agent) throws DocumentException, SAXException, IOException {
        for(File pom: PomUtils.findPoms(projectFolder)){
            final Document document = PomUtils.buildPomWithAgent(pom, agent);

            if(PomUtils.hasNode(document, "argLine")){
                try(FileWriter writer = new FileWriter(pom, false)) {
                    writer.write(document.asXML());
                }
            }
        }
    }

    public static Collection<File> findPoms(final File root){
        return FileUtils.listFiles(root, new NameFileFilter("pom.xml"), TrueFileFilter.INSTANCE);
    }

    public static Document buildPomWithAgent(File pom, String agentString) throws SAXException, DocumentException {
        final SAXReader xmlReader = new SAXReader();
        xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        final Document document = xmlReader.read(pom);
        final List<Node> nodes = document.selectNodes("//*[local-name()='argLine']");

        nodes.forEach(n -> n.setText(String.format("%s -javaagent:%s", n.getText(), agentString)));

        return document;
    }

    public static boolean hasNode(Document document, String name){
        return !document.selectNodes(String.format("//*[local-name()='%s']", name)).isEmpty();
    }
}

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
    enum Action {
        ADD,
        REMOVE
    }

    private PomUtils() {}

    public static void modifyArgLineAgent(File projectFolder, String agent, Action action) throws DocumentException, SAXException, IOException {
        for(File pom: PomUtils.findPoms(projectFolder)){
            final Document document = buildPomWithAgentModified(pom, agent, action);

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

    public static Document buildPomWithAgentModified(File pom, String agentString, Action action) throws SAXException, DocumentException {
        final SAXReader xmlReader = new SAXReader();
        xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        final Document document = xmlReader.read(pom);
        final List<Node> nodes = document.selectNodes("//*[local-name()='argLine']");

        switch (action){
            case ADD:
                nodes.forEach(n -> n.setText(String.format("%s -javaagent:%s", n.getText(), agentString)));
                break;
            case REMOVE:
                final String agentArg = String.format(" -javaagent:%s", agentString);
                nodes.forEach(n -> n.setText(n.getText().replace(agentArg, "")));
                break;
        }


        return document;
    }

    public static boolean hasNode(Document document, String name){
        return !document.selectNodes(String.format("//*[local-name()='%s']", name)).isEmpty();
    }
}

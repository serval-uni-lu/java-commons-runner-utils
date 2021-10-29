package lu.uni.serval.commons.runner.utils.build;

import lu.uni.serval.commons.runner.utils.helpers.Helpers;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PomUtilsTest {
    @Test
    void testFindPoms() throws URISyntaxException {
        final File resourcesFile = Helpers.getResourcesFile("dummy-maven");
        final Collection<File> poms = PomUtils.findPoms(resourcesFile);

        assertEquals(3, poms.size());
    }

    @Test
    void testBuildPomWithAgentWithArgLine() throws URISyntaxException, SAXException, DocumentException {
        final File pom = Helpers.getResourcesFile("dummy-maven/module2/pom.xml");
        final Document document = PomUtils.buildPomWithAgent(pom, "-javaagent:/path/to/agent=arg1,arg2");

        assertTrue(PomUtils.hasNode(document, "argLine"));
        assertTrue(document.asXML().contains("-javaagent:/path/to/agent=arg1,arg2"));
    }

    @Test
    void testBuildPomWithoutAgentWithArgLine() throws URISyntaxException, DocumentException, SAXException {
        final File pom = Helpers.getResourcesFile("dummy-maven/module1/pom.xml");
        final Document document = PomUtils.buildPomWithAgent(pom, "-javaagent:/path/to/agent=arg1,arg2");

        assertFalse(PomUtils.hasNode(document, "argLine"));
        assertFalse(document.asXML().contains("-javaagent:/path/to/agent=arg1,arg2"));
    }
}
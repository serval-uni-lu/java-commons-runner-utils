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
        final File resourcesFile = Helpers.getResourcesFile("dummy-maven-multi-module");
        final Collection<File> poms = PomUtils.findPoms(resourcesFile);

        assertEquals(3, poms.size());
    }

    @Test
    void testBuildPomWithAgentWithArgLine() throws URISyntaxException, SAXException, DocumentException {
        final File pom = Helpers.getResourcesFile("dummy-maven-multi-module/module2/pom.xml");
        final Document document = PomUtils.buildPomWithAgentModified(pom, "-javaagent:/path/to/agent=arg1,arg2", PomUtils.Action.ADD);

        assertTrue(PomUtils.hasNode(document, "argLine"));
        assertTrue(document.asXML().contains("-javaagent:/path/to/agent=arg1,arg2"));
    }

    @Test
    void testBuildPomWithoutAgentWithArgLine() throws URISyntaxException, DocumentException, SAXException {
        final File pom = Helpers.getResourcesFile("dummy-maven-multi-module/module1/pom.xml");
        final Document document = PomUtils.buildPomWithAgentModified(pom, "-javaagent:/path/to/agent=arg1,arg2", PomUtils.Action.ADD);

        assertFalse(PomUtils.hasNode(document, "argLine"));
        assertFalse(document.asXML().contains("-javaagent:/path/to/agent=arg1,arg2"));
    }
}

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
            default:
                throw new IllegalArgumentException("Can only add or remove javaagent not: " + action.name());
        }


        return document;
    }

    public static boolean hasNode(Document document, String name){
        return !document.selectNodes(String.format("//*[local-name()='%s']", name)).isEmpty();
    }
}

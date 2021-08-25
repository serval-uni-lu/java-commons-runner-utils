package lu.uni.serval.commons.runner.utils.version;

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


import lu.uni.serval.commons.runner.utils.configuration.FolderConfiguration;
import lu.uni.serval.commons.runner.utils.configuration.MavenConfiguration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderProvider implements VersionProvider {
    private final File rootFolder;
    private final FolderConfiguration.NameFormat nameFormat;
    private final String dateFormat;
    private final MavenConfiguration mavenConfiguration;

    public FolderProvider(File rootFolder, FolderConfiguration.NameFormat nameFormat, String dateFormat, MavenConfiguration mavenConfiguration) {
        this.rootFolder = rootFolder;
        this.nameFormat = nameFormat;
        this.dateFormat = dateFormat;
        this.mavenConfiguration = mavenConfiguration;
    }

    @Override
    public void clean() {
        //nothing to do, the folders are read only.
    }

    @Override
    public Iterator<Version> iterator() {
        return new FolderIterator();
    }

    class FolderIterator implements Iterator<Version> {
        private final List<File> targetFolders = getTargetFolders();
        private final Iterator<File> fileIterator = targetFolders.iterator();

        @Override
        public boolean hasNext() {
            return fileIterator.hasNext();
        }

        @Override
        public Version next() {
            final File folder = fileIterator.next();
            LocalDateTime date;

            if(nameFormat == FolderConfiguration.NameFormat.DATE) {
                date = LocalDateTime.parse(folder.getName(), DateTimeFormatter.ofPattern(dateFormat));
            }
            else {
                date = LocalDateTime.now();
            }

            return new Version(
                    folder.getName(),
                    folder,
                    date,
                    "",
                    "",
                    mavenConfiguration);
        }

        private List<File> getTargetFolders(){
            if(nameFormat == FolderConfiguration.NameFormat.SINGLE){
                return Collections.singletonList(rootFolder);
            }

            return Stream.of(Objects.requireNonNull(rootFolder.listFiles(
                            (File current, String name) -> new File(current, name).isDirectory())
                    ))
                    .sorted(this::sortSubFolder)
                    .collect(Collectors.toList());
        }

        private int sortSubFolder(File file1, File file2) {
            final String name1 = file1.getName();
            final String name2 = file2.getName();

            int compare;

            if(nameFormat == FolderConfiguration.NameFormat.DATE){
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
                final LocalDate date1 = LocalDate.from(dateTimeFormatter.parse(name1));
                final LocalDate date2 = LocalDate.from(dateTimeFormatter.parse(name2));
                compare = date1.compareTo(date2);
            }
            else {
                compare = name1.compareToIgnoreCase(name2);
            }

            return compare;
        }
    }
}

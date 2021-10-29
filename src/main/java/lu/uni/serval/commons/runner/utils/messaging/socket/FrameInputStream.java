package lu.uni.serval.commons.runner.utils.messaging.socket;

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

import lu.uni.serval.commons.runner.utils.messaging.frame.Frame;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

public class FrameInputStream extends ObjectInputStream {
    private final Set<String> allowedClasses;

    public FrameInputStream(InputStream in, Set<Class<? extends Frame>> allowedClasses) throws IOException {
        super(in);
        this.allowedClasses = allowedClasses.stream().map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
        if(!osc.getName().startsWith("java.util")
                && !osc.getName().startsWith("java.lang")
                && !allowedClasses.contains(osc.getName())
        ){
            throw new InvalidClassException("Unauthorized deserialization", osc.getName());
        }

        return super.resolveClass(osc);
    }
}

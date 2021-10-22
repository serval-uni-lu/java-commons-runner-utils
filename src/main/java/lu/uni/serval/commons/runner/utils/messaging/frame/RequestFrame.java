package lu.uni.serval.commons.runner.utils.messaging.frame;

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

import javax.jms.Message;
import java.util.Optional;
import java.util.Properties;

public class RequestFrame<T extends Frame> implements Frame {
    public static final int CODE = RequestFrame.class.getCanonicalName().hashCode();

    private final Class<T> target;
    private final Properties options;

    private transient Message message;

    public RequestFrame(Class<T> target, Properties options) {
        this.target = target;
        this.options = options;
    }

    @Override
    public int getCode() {
        return CODE;
    }

    public Class<T> getTarget() {
        return target;
    }

    public Properties getOptions() {
        return options;
    }

    public Optional<String> getOption(String key){
        if(!options.containsKey(key)){
            return Optional.empty();
        }

        return Optional.of(options.getProperty(key));
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}

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

import java.util.Map;

public class RequestFrame implements Frame {
    public static final int CODE = RequestFrame.class.getCanonicalName().hashCode();

    private final Class<? extends Frame> target;
    private final Map<String, String> options;

    public RequestFrame(Class<? extends Frame> target, Map<String, String> options) {
        this.target = target;
        this.options = options;
    }

    @Override
    public int getCode() {
        return CODE;
    }

    public Class<? extends Frame> getTarget() {
        return target;
    }

    public Map<String, String> getOptions() {
        return options;
    }
}

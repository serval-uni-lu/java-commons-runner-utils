package lu.uni.serval.commons.runner.utils.messaging.frame;

/*-
 * #%L
 * Runner Utils
 * %%
 * Copyright (C) 2021 University of Luxembourg, Renaud RWEMALIKA
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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


public class EndFrame implements Frame{
    public static final int CODE = EndFrame.class.getCanonicalName().hashCode();

    private final String origin;

    public EndFrame(String origin){
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}

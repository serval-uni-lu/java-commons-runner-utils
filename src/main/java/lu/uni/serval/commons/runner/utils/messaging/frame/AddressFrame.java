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


public class AddressFrame implements Frame{
    public static final int CODE = AddressFrame.class.getCanonicalName().hashCode();

    private final String host;
    private final int port;

    public AddressFrame(int port) {
        this.host = "localhost";
        this.port = port;
    }

    public AddressFrame(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int getCode() {
        return CODE;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

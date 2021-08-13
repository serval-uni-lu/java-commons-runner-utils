package lu.uni.serval.commons.runner.utils.messaging.activemq.broker;

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


import lu.uni.serval.commons.runner.utils.exception.AlreadyInitializedException;
import lu.uni.serval.commons.runner.utils.exception.NotInitializedException;

public class BrokerInfo {
    private final String url;

    private static BrokerInfo instance = null;

    private BrokerInfo(String brokerUrl) {
        this.url = brokerUrl;
    }

    public static void initialize(String protocol, String host, int port) throws AlreadyInitializedException {
        final String brokerUrl = formatUrl(protocol, host, port);
        initialize(brokerUrl);
    }

    public static void initialize(String brokerUrl) throws AlreadyInitializedException {
        if(instance != null){
            if(!brokerUrl.equals(instance.url)){
                throw new AlreadyInitializedException(String.format(
                        "Trying to create broker with url '%s', but broker with url '%s' already exists.",
                        brokerUrl,
                        instance.url
                ));
            }
        }
        else {
            instance = new BrokerInfo(brokerUrl);
        }
    }

    public static String url() throws NotInitializedException {
        checkNotInitialized();
        return instance.url;
    }

    private static String formatUrl(String protocol, String host, int port){
        return String.format("%s://%s:%d", protocol, host, port);
    }

    private static void checkNotInitialized() throws NotInitializedException {
        if(instance == null){
            throw new NotInitializedException("Trying to access broker information, but it has not been initialized.");
        }
    }
}

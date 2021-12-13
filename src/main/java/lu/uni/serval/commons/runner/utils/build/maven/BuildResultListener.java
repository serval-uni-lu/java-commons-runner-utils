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

import lu.uni.serval.commons.runner.utils.listener.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuildResultListener extends Listener {
    private static final List<String> OPTIONS;
    private static final String SUCCESS = "[INFO] BUILD SUCCESS";
    private static final String FAILURE = "[INFO] BUILD FAILURE";

    static {
        OPTIONS = new ArrayList<>(2);
        OPTIONS.add(SUCCESS);
        OPTIONS.add(FAILURE);
    }

    private final List<String> results = new ArrayList<>();

    public boolean isSuccess(){
        if(results.isEmpty()){
            return false;
        }

        return Objects.equals(results.get(results.size() - 1), SUCCESS);
    }

    public boolean isFailure(){
        return !isSuccess();
    }

    @Override
    protected void onStartListening() {
        //nothing to do
    }

    @Override
    protected boolean onMessageReceived(String string) {
        final String clean = string.trim();

        if(OPTIONS.contains(clean)){
            results.add(clean);
        }

        return true;
    }

    @Override
    protected void onEndListening() {
        // nothing to do
    }

    @Override
    protected void onExceptionRaised(Exception e) {
        // nothing to do
    }
}

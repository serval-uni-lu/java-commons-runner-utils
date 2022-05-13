package lu.uni.serval.commons.runner.utils.configuration;

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


public abstract class Configuration {
    protected  Configuration parent;
    private final Variables variableList = new Variables();

    public void registerVariable(String key, String value){
        getVariables().register(key, value);
    }

    public String getResolved(String text){
        return getVariables().resolve(text);
    }

    protected Variables getVariables(){
        if(parent != null){
            return parent.getVariables();
        }

        return variableList;
    }

    public void setParent(Configuration parent){
        this.parent = parent;
    }
}

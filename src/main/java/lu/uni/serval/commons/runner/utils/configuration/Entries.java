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


import java.util.*;

public class Entries implements Collection<Entry> {
    private final List<Entry> entryList;

    public Entries(){
        entryList = new ArrayList<>();
    }

    public Entries(int size){
        entryList = new ArrayList<>(size);
    }

    public List<String> format(String separator){
        return format("", separator, "");
    }

    public List<String> format(String prefix, String separator){
        return format(prefix, separator, "");
    }

    public List<String> format(String prefix, String separator, String suffix){
        final List<String> formatted = new LinkedList<>();

        for(Entry entry: entryList){
            final String formattedEntry = entry.format(prefix, separator, suffix);
            formatted.addAll(Arrays.asList(formattedEntry.split("\\s+")));
        }

        return formatted;
    }

    public void put(String key, String value){
        entryList.add(new Entry(key, value));
    }

    public void putAll(Entries extraArguments) {
        entryList.addAll(extraArguments.entryList);
    }

    public void putAll(List<Entry> extraArguments) {
        entryList.addAll(extraArguments);
    }

    @Override
    public int size() {
        return entryList.size();
    }

    public boolean isEmpty(){
        return entryList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return entryList.contains(o);
    }

    @Override
    public Iterator<Entry> iterator() {
        return entryList.iterator();
    }

    @Override
    public Object[] toArray() {
        return entryList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return entryList.toArray(a);
    }

    @Override
    public boolean add(Entry entry) {
        if(entry != null){
            return entryList.add(entry);
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        return entryList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return entryList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Entry> c) {
        return entryList.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return entryList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return entryList.retainAll(c);
    }

    @Override
    public void clear() {
        entryList.clear();
    }
}

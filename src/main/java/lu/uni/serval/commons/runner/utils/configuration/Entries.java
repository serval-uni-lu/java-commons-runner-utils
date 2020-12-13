package lu.uni.serval.commons.runner.utils.configuration;

import java.util.*;

public class Entries implements Collection<Entry> {
    private final List<Entry> entryList;

    public Entries(){
        entryList = new ArrayList<>();
    }

    public Entries(int size){
        entryList = new ArrayList<>(size);
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

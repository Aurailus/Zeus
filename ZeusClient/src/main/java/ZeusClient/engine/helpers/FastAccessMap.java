package ZeusClient.engine.helpers;

import java.util.*;

//extending sequential because it bases itself of the ListIterator(int) and size() implementation
public class FastAccessMap<K, T> extends AbstractSequentialList<T> implements RandomAccess {

    private class Entry<K, T> {
        private K key;
        private T val;

        public K getKey() {
            return key;
        }

        public T getVal() {
            return val;
        }
    }

    private List<Entry<K, T>> list = new ArrayList<>();
    private Map<K, T> map = new HashMap<>();


    public int size(){
        return list.size();
    }

    public boolean contains(Object o){//what it's about
        return map.containsKey(o);
    }

    public ListIterator listIterator(int i){
        return new ConIterator(list.listIterator(i));
    }

    /*for iterator()*/
    private class ConIterator implements ListIterator<Entry<K, T>> {

        Entry<K, T> obj;
        ListIterator<Entry<K, T>> it;

        private ConIterator(ListIterator<Entry<K, T>> it){
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        public Entry<K, T> next(){
            return obj=it.next();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        public Entry<K, T> previous(){
            return obj=it.previous();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }

        public void remove(){
            it.remove();//remove from both
            map.remove(obj.getKey());
        }

        public void set(Entry<K, T> t){
            it.set(t);
            map.remove(obj.key);
            map.put(obj.key, obj.val);
        }

        public void add(Entry<K, T> t){
            it.add(t);
            map.put(obj.key, obj.val);
        }
    }
}
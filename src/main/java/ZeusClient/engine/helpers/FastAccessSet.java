package ZeusClient.engine.helpers;

import java.util.*;

//extending sequential because it bases itself of the ListIterator(int) and size() implementation
public class FastAccessSet<T> extends AbstractSequentialList<T> implements RandomAccess {

    private List<T> list=new ArrayList<T>();
    private Set<T> set=new HashSet<T>();


    public int size(){
        return list.size();
    }

    public boolean contains(Object o){//what it's about
        return set.contains(o);
    }

    public ListIterator<T> listIterator(int i){
        return new ConIterator(list.listIterator(i));
    }

    /*for iterator()*/
    private class ConIterator implements ListIterator<T> {

        T obj;
        ListIterator<T> it;

        private ConIterator(ListIterator<T> it){
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        public T next(){
            return obj=it.next();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        public T previous(){
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
            set.remove(obj);
        }

        public void set(T t){
            it.set(t);
            set.remove(obj);
            set.add(obj=t);
        }

        public void add(T t){
            it.add(t);
            set.add(t);
        }

        //hasNext and hasPrevious + indexes still to be forwarded to it
    }
}
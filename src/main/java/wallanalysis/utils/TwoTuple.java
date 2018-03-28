package wallanalysis.utils;

/**
 * @author sunlggggg
 * @date 2016/12/22
 */
public class TwoTuple<T,V> {
    T first;
    V second;
    public TwoTuple() {
    }

    public TwoTuple(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }
}

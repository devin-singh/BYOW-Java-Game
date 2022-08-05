package byow.WorldGen;


import java.util.HashMap;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> {
    private int maxElem; //stores 1 + last -- lowest element in the heap
    private T[] items; // all this elements
    private double[] pr; // all priorities
    private HashMap itemsHash;

    public ArrayHeapMinPQ() {
        maxElem = 1;
        items = (T[]) new Object[16];
        pr = new double[16];
        itemsHash = new HashMap(16);
    }
    private int rChild(int pos) {
        return (pos * 2) + 1;
    }
    private int lChild(int pos) {
        return pos * 2;
    }
    private int parent(int pos) {
        return pos / 2;
    }
    /**
     * Returns whether the position is a valid filled/to-be-filled element position
     * in the heap.
     * @param pos
     * @return true = valid; false = invalid
     */
    private boolean validate(int pos) {
        if (pos > 0 && pos < maxElem) {
            return true;
        }
        return false;
    }
    /**
     * Makes the recently-added element at the bottom find it's rightful place.
     * Follows through with recursion on the position it replaced with.
     * @param pos
     */
    private void ascension(int pos) {
        int par = parent(pos);
        if (validate(par)) {
            if (pr[pos] < pr[par]) {
                replace(pos, par);
                ascension(par);
            }
        }
    }
    /**
     * Makes a greater priority sink down by comparison with it's child positions.
     * Recursion with the position it got replaced with.
     * @param pos
     */
    private void dethrone(int pos) {
        double least = pr[pos];
        int best = pos;
        if (validate(rChild(pos)) && pr[rChild(pos)] < least) {
            best = rChild(pos);
            least = pr[best];
        }
        if (validate(lChild(pos)) && pr[lChild(pos)] < least) {
            best = lChild(pos);
            least = pr[best];
        }
        if (best != pos) {
            replace(pos, best);
            dethrone(best);
        }
    }
    /**
     * Replace elements in both items[] and pr[] in the two positions.
     * @param pos1
     * @param pos2
     */
    private void replace(int pos1, int pos2) {
        if (pos1 != pos2) {
            double tempPr = pr[pos1];
            T tempIt = items[pos1];
            pr[pos1] = pr[pos2];
            items[pos1] = items[pos2];
            pr[pos2] = tempPr;
            items[pos2] = tempIt;
            itemsHash.replace(items[pos1], pos1);
            itemsHash.replace(items[pos2], pos2);
        }
    }

    private void resize(int newSize) {
        T[] tempIt = (T[]) new Object[newSize];
        double[] tempPr = new double[newSize];
        System.arraycopy(items, 0, tempIt, 0, maxElem);
        System.arraycopy(pr, 0, tempPr, 0, maxElem);
        items = tempIt;
        pr = tempPr;
    }

    
    public void add(T item, double priority) {
        if (contains(item) || item == null) {
            throw new IllegalArgumentException("Item already exists");
        }
        if (maxElem >= items.length) {
            resize(items.length * 2);
        }
        items[maxElem] = item;
        pr[maxElem] = priority;
        itemsHash.put(item, maxElem);
        ascension(maxElem);
        maxElem += 1;
    }
    private int finderHash(T item) {
        if (item == null) {
            return 0;
        }
        Object val = itemsHash.get(item);
        if (val == null) {
            return 0;
        }
        return (int) val;
    }

    
    public boolean contains(T item) {
        if (item == null) {
            return false; //anything else?
        }
        int pos = finderHash(item);
        return pos != 0;
    }
    
    public T getSmallest() {
        if (maxElem == 1) {
            throw new NoSuchElementException("Empty MinHeap");
        }
        return items[1];
    }
    
    public T removeSmallest() {
        T temp = getSmallest();
        maxElem -= 1;
        replace(1, maxElem);
        itemsHash.remove(items[maxElem]);
        items[maxElem] = null;
        pr[maxElem] = 0.0;
        dethrone(1);
        double filled = (double) maxElem / items.length;
        if (filled <= 0.25 && items.length > 32) {
            resize(items.length / 4);
        }
        return temp;
    }

    
    public int size() {
        return maxElem - 1;
    }

    
    public void changePriority(T item, double priority) {
        int pos = finderHash(item);
        if (pos != 0) {
            pr[pos] = priority;
            ascension(pos);
            dethrone(pos);
            /*
            if (priority < pr[parent(pos)]) {
                ascension(pos);
            } else {
                dethrone(pos);
            }

            If either is required, the other would not perform and hence the position is untouched
             */
        } else {
            throw new NoSuchElementException("Item does not exist");
        }
    }
}

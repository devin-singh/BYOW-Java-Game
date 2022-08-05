package byow.InputDemo;

import edu.princeton.cs.introcs.StdDraw;
import java.util.LinkedList;

public class ThreadboardInputSource implements InputSource, Runnable {
    private static final boolean PRINT_TYPED_KEYS = false;
    //private boolean nextKey = false;
    private LinkedList keys;
    public ThreadboardInputSource() {
        keys = new LinkedList();
    }

    public char getNextKey() {
        if (possibleNextInput()) {
            char c = (char) keys.removeLast();
            return c;
        }
        return '`'; //some waste one?
    }

    public boolean possibleNextInput() {
        return keys.size() > 0;
    }

    @Override
    public void run() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                //nextKey = true;
                keys.add(c);
            }
        }
    }

    public void wipe() {
        keys = new LinkedList();
    }

}

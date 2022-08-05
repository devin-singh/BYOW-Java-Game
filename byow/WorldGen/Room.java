package byow.WorldGen;


import java.util.List;
import java.util.ArrayList;

public class Room {
    /** Height of Room. In the 0-1 offset convention **/
    private int height;
    /** Width of Room. In the 0-1 offset convention **/
    private int width;
    /** List of registered exits to the Room **/
    private List<Exit> exits;
      
    private Point bottomLeftPos; //bottom left pos
    private Point topRightPos;

    public Point bottomLeftPos() {
        return bottomLeftPos;
    }

    public Point topRightPos() {
        return topRightPos;
    }

    public int width() {
        return width;
    }
    public int height() {
        return height;
    }

    public List<Exit> exits() {
        return exits;
    }


    public Room(Point bottomLeftPos, int h, int w) {
        this.bottomLeftPos = bottomLeftPos;
        this.topRightPos = new Point(bottomLeftPos, w, h);
        height = h;
        width = w;
        exits = new ArrayList<Exit>();

    }

    public void addExit(Exit e) {
        exits.add(e);
    }
}

package byow.WorldGen;

import java.util.Objects;

public class Point {
    /** Grid 2-d array x-coordinate **/
    private int xPos;
    /** Grid 2-d array y-coordinate **/
    private int yPos;
    /** The room within which the Point is located, if any. null when in void. Wall
     * Points also count for their corresponding room. */
    Room room;

    /**
     * One Point object registered to each location on the grid.
     * @param x X-coordinate in corresponding inverted format
     * @param y Y-coordinate in corresponding inverted format
     * @param r The room it is situated in, if any.
     */
    public Point(int x, int y, Room r) {
        xPos = x;
        yPos = y;
        room = r;
    }

    public Point(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public Point(Point point, int xOff, int yOff) {
        xPos = point.xPos + xOff;
        yPos = point.yPos + yOff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return xPos == point.xPos &&
                yPos == point.yPos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xPos, yPos);
    }
}

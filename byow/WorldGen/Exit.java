package byow.WorldGen;

public class Exit {
    /** The two Rooms this Exit connects **/
    private Room[] bridge;

    private Direction direction;
    private Point location;

    public Direction direction() {
        return this.direction;
    }

    public Exit(Point point, Direction direction) {
        this.location = point;
        this.direction = direction;
    }

}

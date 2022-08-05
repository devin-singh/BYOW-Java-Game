package byow.WorldGen;

public class Hallway extends Room {
    private Direction direction;

    public Hallway(Point p, int h, int w, Direction direction) {
        super(p, h, w);
        this.direction = direction;
        //need to figure out how the walls will be ordered
        //set all positions to be set to wall and plains

    }

    public Direction getDirection() {
        return direction;
    }
}

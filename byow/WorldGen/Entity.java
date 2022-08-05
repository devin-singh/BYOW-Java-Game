package byow.WorldGen;

import java.util.ArrayList;
import java.util.LinkedList;

public class Entity {
    private PathFinder paths;
    private boolean chasing;
    private LinkedList<Point> moves;
    private World world;
    private ArrayList<Point> pointsOpen;

    public Entity(ArrayList<Point> openPoints, World w) {
        paths = new PathFinder(openPoints, true, w);
        world = w;
        pointsOpen = openPoints;
    }

    public Point nextMove() {
        Point entityPos = new Point(world.getEntityXPos(), world.getEntityYPos());
        Point playerPos = new Point(world.getPlayerXPos(), world.getPlayerYPos());
        paths = new PathFinder(pointsOpen, true, world);
        LinkedList<Point> temp = paths.updatePos(entityPos, playerPos);
        moves = temp;
        Point p = moves.removeFirst();
        //p = moves.removeFirst();
        //System.out.println(p.getxPos() + "  " + p.getyPos() + "  Movement");
        world.entityPosUpdate(p.getxPos(), p.getyPos());
        return p;
    }

}

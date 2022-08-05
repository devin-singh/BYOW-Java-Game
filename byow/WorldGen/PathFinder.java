package byow.WorldGen;

import byow.lab12.RenderWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PathFinder {
    ArrayList<Point> openPoints;
    Point endPos;
    Point startPos;
    boolean evadeOrChase; //true for chase
    World worldObj;
    private ArrayHeapMinPQ<Point> points;
    private HashMap<Point, Node> paths;
    private boolean searching = true;

    private class Node {
        Point loc;
        int weight;
        Node prev;
        Node(Point p, int w) {
            loc = p;
            weight = w;
        }
        public void changePrev(Node newP) {
            prev = newP;
        }

    }

    public PathFinder(ArrayList<Point> points, boolean e, World w) {
        openPoints = points;
        evadeOrChase = e;
        worldObj = w;
    }

    public LinkedList<Point> updatePos(Point start, Point end) {
        if (end.equals(endPos)) {
            return null; //no need in changing course
        } else if (end.equals(start)) {
            LinkedList<Point> temp = new LinkedList<>();
            temp.addFirst(start);
            return temp;
        }
        endPos = end;
        startPos = start;
        LinkedList<Point> temp = new LinkedList<>();
        pathSolve();
        Node loopStart = paths.get(endPos); //end is having a null node
        if (loopStart == null) {
            System.out.println("WHY");
        }
        while (true) {
            if (loopStart == null || loopStart.loc.equals(startPos)) {
                break;
            }
            temp.addFirst(loopStart.loc);
            //System.out.println(temp.size());
            loopStart = loopStart.prev;
        }
        //System.out.println(temp);
        //System.out.println("Next move " + temp.getFirst().getxPos() + "  " + temp.getFirst().getyPos());
        paths = null;
        points = null;
        return temp;
    }

    private void pathSolve() {
        points = new ArrayHeapMinPQ<>();
        paths = new HashMap<>();
        Point start = startPos;
        points.add(start, 0);
        paths.put(start, new Node(start, 0));
        paths.put(endPos, new Node(endPos, 1000000000));
        while (searching) {
            if (points.size() == 0) {
                throw new IndexOutOfBoundsException("What?");
            }
            recurPath(points.removeSmallest());
        }
    }



    private void recurPath(Point curr) {
        if (curr.equals(endPos)) {
            searching = false;
        }
        //relax all neighbors
        List<Point> temp = List.of(new Point(curr, 0, 1), new Point(curr, 0, -1), new Point(curr, 1, 0), new Point(curr, -1, 0));
        for (Point p: temp) {
            if (openPoint(p)) {
                if (!paths.containsKey(p)) {
                    paths.put(p, new Node(p, 1000000000));
                }
                relaxPoint(p, curr);
            }
        }
    }

    private void relaxPoint(Point curr, Point prev) {
        Node currPoint = paths.get(curr);
        Node contender = paths.get(prev);
        int newWeight = contender.weight + 1;
        if (currPoint.weight > newWeight) {
            currPoint.changePrev(contender);
            currPoint.weight = newWeight; // could change this to a function but who cares
            if (points.contains(currPoint.loc)) {
                points.changePriority(currPoint.loc, newWeight + heuristic(currPoint.loc));
            } else {
                points.add(currPoint.loc, newWeight + heuristic(currPoint.loc)); //here
            }
        }
    }

    public int heuristic(Point p) {
        int dist = (int) (Math.pow(p.getxPos() - endPos.getxPos(), 2) + Math.pow(p.getyPos() - endPos.getyPos(), 2));
        dist = (int) Math.sqrt(dist);
        return dist;
    }
    public Direction respDir(Point init, Point result) {
        int initX = init.getxPos();
        int initY = init.getyPos();
        int resX = result.getxPos();
        int resY = result.getyPos();

        if (initX != resX && initY != resY) {
            throw new IllegalArgumentException("Not 1 block away");
        } else if (Math.abs(initX - resX) > 1 || Math.abs(initY - resY) > 1) {
            throw new IllegalArgumentException("Not 1 block away");
        }

        if (initX > resX) {
            return Direction.WEST;
        } else if (initX < resX) {
            return Direction.EAST;
        } else if (initY > resY) {
            return Direction.SOUTH;
        } else if (initY < resY) {
            return Direction.SOUTH;
        }
        throw new IllegalArgumentException("IDK");
    }

    public boolean openPoint(Point p) {
        return worldObj.moveAble(p.getxPos(), p.getyPos());
        //second should be the same, just with the inclusion of the avatar's position
    }

    public static void main(String[] args) {
        World world = RenderWorld.renderWorld(127343, 3);
        PathFinder path = new PathFinder(world.getOpenPoints(), true, world);
        LinkedList<Point> moves = path.updatePos(new Point(world.getEntityXPos(), world.getEntityYPos()), new Point(world.getPlayerXPos(), world.getPlayerYPos()));
        world.entityPosUpdate(moves.getFirst().getxPos(), moves.getFirst().getyPos());
        moves.removeFirst();
        path = new PathFinder(world.getOpenPoints(), true, world);
        moves = path.updatePos(new Point(world.getEntityXPos(), world.getEntityYPos()), new Point(world.getPlayerXPos(), world.getPlayerYPos()));
        world.entityPosUpdate(moves.getFirst().getxPos(), moves.getFirst().getyPos());
        moves.removeFirst();
        //System.out.println("pathopenPoints");
    }
}


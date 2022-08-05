package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.WorldGen.Room;
import byow.WorldGen.Hallway;
import byow.WorldGen.Point;
import byow.WorldGen.Exit;
import byow.WorldGen.Direction;
import byow.WorldGen.World;


import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;


public class RenderWorld {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static Random random;
    private static Stack<Room> rooms;
    private static Stack<Hallway> hallways;
    private static int numRooms;
    private static ArrayList<Point> openPoints;
    private static World worldObj;

    public static World renderWorld(long seed, int level) {
        //Initialize all Objects to use
        long seedLong = seed;
        openPoints = new ArrayList<>();
        random = new Random(seedLong);
        rooms = new Stack<Room>();
        hallways = new Stack<Hallway>();
        numRooms = 0;

        // initialize tiles and Points array
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        drawFirstRoom(world);
        //System.out.println("First Room");
        //World Generation loop that maxes out at 30 rooms
        for (int i = 0; i < 400; i++) {
            //System.out.println("GenLoop" + i);
            if (numRooms > 30) {
                break;
            }
            try {
                Room recentlyCreatedRoom = rooms.peek();
                if (recentlyCreatedRoom.exits().size() > 2) {
                    rooms.pop();
                    continue;
                }
                openExitAndAddHall(world, recentlyCreatedRoom, false);
            } catch (Exception e) {

            }
            roomLoop(world, 0);
        }
        genAvatarDoor(world);
        //System.out.println("AvatarDoor");
        if (level == 2) { //altering levels here
            coinGen(world);
            //System.out.println("Coin");
            worldFill(openPoints.size(), world);
            //System.out.println("RandomFill");
        } else if (level == 1) {
            worldObj.reduceLevel(1);
            arcadeCreate(world);
        } else if (level == 3) {
            worldObj.reduceLevel(1);
            chasingEntity(world);
        }
        worldObj.worldUpdate(world);
        //System.out.println("Done");
        worldObj.addOpenPoints(openPoints);
        return worldObj;
    }

    public static void chasingEntity(TETile[][] world) {
        for (Point p: openPoints) {
            int x = worldObj.getDoorXPos();
            int y = worldObj.getDoorYPos();
            int maxDist = 10;
            double dist = Math.pow(x - p.getxPos(), 2) + Math.pow(y - p.getyPos(), 2);
            if (dist < maxDist) {
                if (worldObj.moveAble(p.getxPos(), p.getyPos())) {
                    //world[p.getxPos()][p.getyPos()] = Tileset.AVATARHURT;
                    worldObj.entityPosUpdate(p.getxPos(), p.getyPos());
                    return;
                }
            }
        }
    }
    public static void coinGen(TETile[][] world) {
        int num = 20; //number of coins scattered around
        int size = openPoints.size();
        for (int x = 0; x < num; x += 1) {
            Point pos = openPoints.get(randomNumberBetween(0, size));
            while (world[pos.getxPos()][pos.getyPos()] == Tileset.COIN && pos.getyPos() == worldObj.getPlayerYPos() && pos.getxPos() == worldObj.getPlayerXPos()) {
                pos = openPoints.get(randomNumberBetween(0, size));
            }
            world[pos.getxPos()][pos.getyPos()] = Tileset.COIN;
        }
    }
    public static void portalGen(TETile[][] world) {
        ArrayList<Point> deadEnds = new ArrayList<>();
        for (Point p: openPoints) {
            int count = 0;
            if (world[p.getxPos() + 1][p.getyPos()] != Tileset.WALL) {
                deadEnds.add(p);
            }
        }
        while (deadEnds.size() > 1) {
            Point cons = deadEnds.remove(0);
            for (Point p: deadEnds) {
                if (Math.abs(p.getyPos() - cons.getyPos()) > 10 || Math.abs(p.getxPos() - cons.getxPos()) > 10) {
                    System.out.println("Creating Portal");
                }
            }
        }
    }
    public static void arcadeCreate(TETile[][] world) {
        int lockedX = 0;
        int lockedY = 0;
        while (true) {
            lockedX = randomNumberBetween(0, WIDTH);
            lockedY = randomNumberBetween(0, HEIGHT);
            if (world[lockedX][lockedY] == Tileset.WALL && reachable(world, lockedX, lockedY)) {
                break;
            }
        }
        world[lockedX][lockedY] = Tileset.ARCADE;
    }
    private static boolean reachable(TETile[][] world, int x, int y) {
        if (x + 1 < world.length && world[x + 1][y] == Tileset.FLOOR) {
            return true;
        } else if (x - 1 > 0 && world[x - 1][y] == Tileset.FLOOR) {
            return true;
        } else if (y + 1 < world[0].length && world[x][y + 1] == Tileset.FLOOR) {
            return true;
        } else if (y - 1 > 0 && world[x][y - 1] == Tileset.FLOOR) {
            return true;
        }
        return false;
    }
    public static void nameRooms() {

    }
    public static void worldFill(int size, TETile[][] world) {
        for (int x = 0; x < size / 3; x += 1) {
            Point pos = openPoints.get(randomNumberBetween(0, size));
            TETile t =  world[pos.getxPos()][pos.getyPos()];
            if (worldObj.moveAble(pos.getxPos(), pos.getyPos()) && world[pos.getxPos()][pos.getyPos()] != Tileset.COIN) {
                world[pos.getxPos()][pos.getyPos()] = randomTile(randomNumberBetween(0, 10));
            }
        }
    }
    public static void genAvatarDoor(TETile[][] world) {
        int lockedX = 0;
        int lockedY = 0;
        while (true) {
            lockedX = randomNumberBetween(0, WIDTH);
            lockedY = randomNumberBetween(0, HEIGHT);
            if (world[lockedX][lockedY] == Tileset.WALL && reachable(world, lockedX, lockedY)) {
                world[lockedX][lockedY] = Tileset.LOCKED_DOOR;
                worldObj = new World(world, lockedX, lockedY); // -----------------------
                break;
            }
        }

        Point furthest;
        double maxDist = 0;
        for (Point p: openPoints) {
            double dist = Math.pow(lockedX - p.getxPos(), 2) + Math.pow(lockedY - p.getyPos(), 2);
            if (dist > maxDist) {
                maxDist = dist;
                furthest = p;
            }
        }
        maxDist = maxDist / 2;
        //faced the issue that the world is unpredictably convoluted that it's best to keep the conditions for generating
        //avatar position based on it's own limits rather than set conditions
        while (true) {
            Point p = openPoints.get(randomNumberBetween(0, openPoints.size() - 1));
            double dist = Math.pow(lockedX - p.getxPos(), 2) + Math.pow(lockedY - p.getyPos(), 2);
            if (dist > maxDist) {
                worldObj.addPlayer(p.getxPos(), p.getyPos());
                break;
            }
        }
    }
    public static TETile randomTile(int x) {
        switch (x) {
            case 0: return Tileset.HEALTH;
            case 1:
            case 2:
                return Tileset.LAVA;
            case 3:
            case 4:
            case 7:
                return Tileset.ICE;
            case 5:
            case 6:
                return Tileset.WATER;
            default: return Tileset.FLOOR;
        }
    }
    public static void roomLoop(TETile[][] world, int i) {
        try {
            addRoomToHall(world);
            numRooms += 1;
        } catch (Exception e) {
            //System.out.println("Can't add room to hall");
            if (i >= 3) {
                if (hallways.size() > 0) {
                    Hallway h = hallways.pop();
                    connectHall(h, world);

                }
                return;
            }
            roomLoop(world, i + 1);
        }
    }
    private static void connectHall(Hallway h, TETile[][] world) {
        openExitAndAddHall(world, h, true);
        //take
    }
    private static void addRoomToHall(TETile[][] world) {
        Hallway recentlyCreatedHallway = hallways.peek();
        Direction hallwayDirection = recentlyCreatedHallway.getDirection();

        Room roomToAdd;
        int[] pos = new int[4];
        int height = randomNumberBetween(7, 13);
        int width = randomNumberBetween(7, 13);
        if (hallwayDirection == Direction.NORTH) {
            roomToAdd = new Room(new Point(recentlyCreatedHallway.topRightPos().getxPos() - (width - width / 2), recentlyCreatedHallway.topRightPos().getyPos()), height, width);
            pos[0] = recentlyCreatedHallway.topRightPos().getxPos() - 2;
            pos[1] = recentlyCreatedHallway.topRightPos().getyPos();
            pos[2] = recentlyCreatedHallway.topRightPos().getxPos() - 2;
            pos[3] = recentlyCreatedHallway.topRightPos().getyPos() - 1;

        } else if (hallwayDirection == Direction.SOUTH) {
            roomToAdd = new Room(new Point(recentlyCreatedHallway.bottomLeftPos().getxPos() - (width - 5), recentlyCreatedHallway.bottomLeftPos().getyPos() - height), height, width);
            pos[0] = recentlyCreatedHallway.bottomLeftPos().getxPos() + 1;
            pos[1] = recentlyCreatedHallway.bottomLeftPos().getyPos();
            pos[2] = recentlyCreatedHallway.bottomLeftPos().getxPos() + 1;
            pos[3] = recentlyCreatedHallway.bottomLeftPos().getyPos() - 1;

        } else if (hallwayDirection == Direction.WEST) {
            roomToAdd = new Room(new Point(recentlyCreatedHallway.bottomLeftPos().getxPos() - width, recentlyCreatedHallway.bottomLeftPos().getyPos() - height / 2), height, width);
            pos[0] = recentlyCreatedHallway.bottomLeftPos().getxPos();
            pos[1] = recentlyCreatedHallway.bottomLeftPos().getyPos() + 1;
            pos[2] = recentlyCreatedHallway.bottomLeftPos().getxPos() - 1;
            pos[3] = recentlyCreatedHallway.bottomLeftPos().getyPos() + 1;
        } else {
            roomToAdd = new Room(new Point(recentlyCreatedHallway.topRightPos().getxPos(), recentlyCreatedHallway.bottomLeftPos().getyPos() - height / 2), height, width);
            pos[0] = recentlyCreatedHallway.topRightPos().getxPos();
            pos[1] = recentlyCreatedHallway.bottomLeftPos().getyPos() + 1;
            pos[2] = recentlyCreatedHallway.topRightPos().getxPos() - 1;
            pos[3] = recentlyCreatedHallway.bottomLeftPos().getyPos() + 1;
        }

        try {
            drawRoom(roomToAdd, world, Tileset.WALL, Tileset.FLOOR);
        } catch (Exception e) {
            //System.out.println("Failed to add room to hall");
            throw new IndexOutOfBoundsException('d');
        }
        world[pos[0]][pos[1]] = Tileset.FLOOR;
        world[pos[2]][pos[3]] = Tileset.FLOOR;
        openPoints.add(new Point(pos[0], pos[1], roomToAdd));
        openPoints.add(new Point(pos[2], pos[3], roomToAdd));

        rooms.add(roomToAdd);
        hallways.pop();
    }
    // Move the exit opening to occur after the hall is added or not to prevent random openings.
    private static void openExitAndAddHall(TETile[][] world, Room recentlyCreatedRoom, boolean hall) {


        Direction direction;
        if (hall) {
            direction = Direction.next(((Hallway) recentlyCreatedRoom).getDirection());
        } else {
            direction = Direction.pickRandomDirection(random);
        }

        int offSetWidth = randomNumberBetween(2, recentlyCreatedRoom.width() - 1);
        int offSetHeight = randomNumberBetween(2, recentlyCreatedRoom.height() - 1);

        int x, y;
        if (direction == Direction.NORTH) {
            x = recentlyCreatedRoom.topRightPos().getxPos() - offSetWidth;
            y = recentlyCreatedRoom.topRightPos().getyPos() - 1;
        } else if (direction == Direction.EAST) {
            x = recentlyCreatedRoom.topRightPos().getxPos() - 1;
            y = recentlyCreatedRoom.topRightPos().getyPos() - offSetHeight;

        } else if (direction == Direction.WEST) {
            x = recentlyCreatedRoom.bottomLeftPos().getxPos();
            y = recentlyCreatedRoom.bottomLeftPos().getyPos() + (offSetHeight) - 1;

        } else {
            x = recentlyCreatedRoom.bottomLeftPos().getxPos() + (offSetWidth) - 1;
            y = recentlyCreatedRoom.bottomLeftPos().getyPos();
        }

        Exit addedExit = new Exit(new Point(x, y), direction);
        recentlyCreatedRoom.exits().add(addedExit);

        int width = 3;
        int length = randomNumberBetween(3, 6);
        addHall(direction, new Point(x, y), world, width, length);
    }
    private static void addHall(Direction direction, Point point, TETile[][] world, int width, int length)  {
        Hallway hallToAdd;
        if (direction == Direction.NORTH) {
            hallToAdd = new Hallway(new Point(point.getxPos() - 1, point.getyPos() + 1), length, width, direction);
            try {
                drawRoom(hallToAdd, world, Tileset.WALL, Tileset.FLOOR);
                world[point.getxPos()][point.getyPos() + 1] = Tileset.FLOOR;

            } catch (Exception e) {
                //System.out.println("Conflicting hallway.");
                return;
            }

        } else if (direction == Direction.SOUTH) {
            hallToAdd = new Hallway(new Point(point.getxPos() - 1, point.getyPos() - (length)), length, width, direction);
            try {
                drawRoom(hallToAdd, world, Tileset.WALL, Tileset.FLOOR);
                world[hallToAdd.topRightPos().getxPos() - 2][hallToAdd.topRightPos().getyPos() - 1] = Tileset.FLOOR;

            } catch (Exception e) {
                //System.out.println("Conflicting hallway.");
                return;
            }


        } else if (direction == Direction.EAST) {
            hallToAdd = new Hallway(new Point(point.getxPos() + 1, point.getyPos() - 1), width, length, direction);
            try {
                drawRoom(hallToAdd, world, Tileset.WALL, Tileset.FLOOR);
                world[hallToAdd.bottomLeftPos().getxPos()][hallToAdd.bottomLeftPos().getyPos() + 1] = Tileset.FLOOR;

            } catch (Exception e) {
                //System.out.println("Conflicting hallway.");
                return;
            }


        } else {
            hallToAdd = new Hallway(new Point(point.getxPos() - length, point.getyPos() - 1), width, length, direction);
            try {
                drawRoom(hallToAdd, world, Tileset.WALL, Tileset.FLOOR);
                world[hallToAdd.topRightPos().getxPos() - 1][hallToAdd.topRightPos().getyPos() - 2] = Tileset.FLOOR;
            } catch (Exception e) {
                //System.out.println("Conflicting hallway.");
                return;
            }
        }

        world[point.getxPos()][point.getyPos()] = Tileset.FLOOR;
        hallways.add(hallToAdd);
    }
    private static void drawFirstRoom(TETile[][] world) {
        int xOff = randomNumberBetween(2, 4);
        int yOff = randomNumberBetween(2, 4);
        int height = randomNumberBetween(6, 11);
        int width = randomNumberBetween(6, 11);
        Room roomToAdd = new Room(new Point(WIDTH / 2 - xOff, HEIGHT / 2 - yOff), height, width);
        rooms.add(roomToAdd);

        try {
            drawRoom(roomToAdd, world, Tileset.WALL, Tileset.FLOOR);
        } catch (Exception e) {
            throw new IndexOutOfBoundsException("The first room had a bounds issue. This error should never occur.");
        }
    }
    private static int randomNumberBetween(int lowerBound, int upperBound) {
        return (int) (lowerBound + ((upperBound - lowerBound) * random.nextDouble()));
    }
    private static void drawRoom(Room room, TETile[][] world, TETile wallTile, TETile worldTile) {

        try {
            verifyDraw(room.width(), room.bottomLeftPos(), world, wallTile, HORIZONTAL);
            verifyDraw(room.height() - 1, new Point(room.bottomLeftPos(), 0, 1), world, wallTile, VERTICAL);
            verifyDraw(room.width() - 1, new Point(room.bottomLeftPos(), 1, room.height() - 1), world, wallTile, HORIZONTAL);
            verifyDraw(room.height() - 2, new Point(room.topRightPos(), -1, -room.height() + 1), world, wallTile, VERTICAL);
        } catch (Exception e) {
            throw new IndexOutOfBoundsException("Conflicting objects or out of bounds.");
        }

        drawLine(room.width(), room.bottomLeftPos(), world, wallTile, HORIZONTAL);
        drawLine(room.height() - 1, new Point(room.bottomLeftPos(), 0, 1), world, wallTile, VERTICAL);
        drawLine(room.width() - 1, new Point(room.bottomLeftPos(), 1, room.height() - 1), world, wallTile, HORIZONTAL);
        drawLine(room.height() - 2, new Point(room.topRightPos(), -1, -room.height() + 1), world, wallTile, VERTICAL);
        fillIn(room, worldTile, world);
    }
    private static void fillIn(Room room, TETile worldTile, TETile[][] world) {
        for (int x = room.bottomLeftPos().getxPos(); x < room.topRightPos().getxPos(); x += 1) {
            for (int y = room.bottomLeftPos().getyPos(); y < room.topRightPos().getyPos(); y += 1) {
                if (world[x][y] == Tileset.NOTHING) {
                    world[x][y] = worldTile;
                    openPoints.add(new Point(x, y, room));
                }
            }
        }
    }
    private static void verifyDraw(int length, Point p, TETile[][] world, TETile tile, boolean direction) {
        int verticalHeight = 1;
        int horizontalLength = 1;

        if (direction == VERTICAL) {
            verticalHeight = length;
        } else {
            horizontalLength = length;
        }

        for (int x = p.getxPos(); x < p.getxPos() + horizontalLength; x += 1) {
            for (int y = p.getyPos(); y < p.getyPos() + verticalHeight; y += 1) {
                if (world[x][y] != Tileset.NOTHING || x > world.length - 1 || x < 0 || y < 0 || y > world.length) {
                    throw new IndexOutOfBoundsException("There is already an object here");
                }
            }
        }
    }
    private static void drawLine(int length, Point p, TETile[][] world, TETile tile, boolean direction) {
        int verticalHeight = 1;
        int horizontalLength = 1;

        if (direction == VERTICAL) {
            verticalHeight = length;
        } else {
            horizontalLength = length;
        }

        for (int x = p.getxPos(); x < p.getxPos() + horizontalLength; x += 1) {
            for (int y = p.getyPos(); y < p.getyPos() + verticalHeight; y += 1) {
                if (world[x][y] != Tileset.NOTHING || x > world.length - 1 || x < 0 || y < 0 || y > world.length) {
                    throw new IndexOutOfBoundsException("There is already an object here");
                }
            }
        }

        for (int x = p.getxPos(); x < p.getxPos() + horizontalLength; x += 1) {
            for (int y = p.getyPos(); y < p.getyPos() + verticalHeight; y += 1) {
                world[x][y] = tile;
            }
        }
    }

}

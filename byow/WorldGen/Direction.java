package byow.WorldGen;

import java.util.Random;

public enum Direction {
    NORTH, SOUTH, WEST, EAST;

    public static Direction opposite(Direction d) {
        if (d == NORTH) {
            return SOUTH;
        } else if (d == SOUTH) {
            return NORTH;
        } else if (d == WEST) {
            return EAST;
        } else {
            return WEST;
        }
    }

    public static Direction next(Direction d) {
        if (d == NORTH) {
            return WEST;
        } else if (d == SOUTH) {
            return EAST;
        } else if (d == WEST) {
            return NORTH;
        } else {
            return SOUTH;
        }
    }

    public static Direction pickRandomDirection(Random random) {
        double randDouble = random.nextDouble();
        if (randDouble < 0.25) {
            return Direction.NORTH;
        } else if (randDouble < 0.5) {
            return Direction.SOUTH;
        } else if (randDouble < 0.75) {
            return Direction.EAST;
        } else {
            return Direction.WEST;
        }
    }
}

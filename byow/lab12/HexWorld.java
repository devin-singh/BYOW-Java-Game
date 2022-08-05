package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;
    private static TETile[][] world;

    private static void addHexagonOfLength(int L) {
//        int offSet = 0;
//        for (int r = 10; r < (L) + 10; r++) {
//            for (int c = 10; c < HEIGHT; c++) {
//                world[c][r] = Tileset.GRASS;
//            }
//            //offSet += 1;
//        }
    }

    private static void drawLineOfLength(int length, int x, int y) {
//        for (int i = x; x < length + x; i++) {
//            world[i][y] = Tileset.GRASS;
//        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        drawLineOfLength(10, 1, 40);

        // fills in a block 14 tiles wide by 4 tiles tall


        // draws the world to the screen
        ter.renderFrame(world);
    }

}

package byow.TileEngine;

import byow.Core.InteractWorld;
import byow.Core.SnakeGame;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    private TETile pressed;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        pressed = Tileset.NOTHING;
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }
    }
    public void renderHUD(InteractWorld data, TETile[][] world) {
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.blue);
        StdDraw.picture(40, 4, "byow/TileEngine/Tilesets/HUD/HUD2.png");
        healthBar(data.getHealth(), data.isBurnt());
        thirstBar(data.getThirst());
        showPotions(data.numPotions());
        showCoins(data.getCoins());
        if (data.isBurnt()) {
            StdDraw.picture(5, 4, "byow/TileEngine/Tilesets/HUD/playerburn.png");
        } else {
            StdDraw.picture(5, 4, "byow/TileEngine/Tilesets/HUD/player.png");
        }
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        StdDraw.textLeft(70, 5, "BLOCK: " + (x) + "  " + (y - 8));
        if (y >= 8 && x < 80 && x >= 0 && y < (height + 8)) {
            try {
                StdDraw.textLeft(70, 3, "BLOCK: " + world[x][y - 8].description());
                if (StdDraw.isMousePressed()) {
                    pressed = world[x][y - 8];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Cursor outside world");
            }

        }

        if (data.isFirstPerson()) {
            sideMap(data.getDynamicWorld());
            pressedTile();
            StdDraw.textLeft(55, 45, "SEED:" + data.getWorldSeed());
            if (data.isCrouch()) {
                StdDraw.textLeft(55, 41, "CROUCHED");
            }
            if (data.isBurnt()) {
                StdDraw.textLeft(58, 41, "BURNT");
            }
            if (data.isGodMode()) {
                StdDraw.textLeft(55, 39, "GOD-MODE");
            }
        } else if (data.getmapOpen()) { //some condition to check whether the map is opened
            miniMap(world);
        }
    }
    public void miniMap(TETile[][] world) {
        int refX = 45;
        int refY = 8;

        double multiplier = 0.4;
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(refX + ((world.length / 2) * multiplier), refY + ((world[0].length / 2) * multiplier),
                ((world.length / 2) * multiplier) + 0.5, ((world[0].length / 2) * multiplier) + 0.5);
        StdDraw.setPenColor(Color.RED);
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                double xVal = x * multiplier;
                double yVal = y * multiplier;
                if (world[x][y] == Tileset.WALL) {
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.215);
                } else if (world[x][y] == Tileset.AVATAR) {
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.215);
                    StdDraw.setPenColor(Color.RED);
                } else if (world[x][y] == Tileset.LOCKED_DOOR || world[x][y] == Tileset.UNLOCKED_DOOR) {
                    StdDraw.setPenColor(Color.YELLOW);
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.215);
                    StdDraw.setPenColor(Color.RED);
                } else if (world[x][y] == Tileset.AVATARHURT) {
                    StdDraw.setPenColor(Color.BLUE);
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.215);
                    StdDraw.setPenColor(Color.RED);
                }
            }
        }

    }
    public void sideMap(TETile[][] world) {
        //outline
        int refX = 37;
        int refY = 10;

        double multiplier = 0.5;
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(refX + 20, refY + 10, 20 + 0.5, 10 + 0.5);
        StdDraw.setPenColor(Color.RED);
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                double xVal = x * multiplier;
                double yVal = y * multiplier;
                if (world[x][y] == Tileset.WALL) {
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.25);
                } else if (world[x][y] == Tileset.AVATAR) {
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.25);
                    StdDraw.setPenColor(Color.RED);
                } else if (world[x][y] == Tileset.LOCKED_DOOR || world[x][y] == Tileset.UNLOCKED_DOOR) {
                    StdDraw.setPenColor(Color.YELLOW);
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.25);
                    StdDraw.setPenColor(Color.RED);
                } else if (world[x][y] == Tileset.AVATARHURT) {
                    StdDraw.setPenColor(Color.BLUE);
                    StdDraw.filledSquare(refX + xVal, refY + yVal, 0.25);
                    StdDraw.setPenColor(Color.RED);
                }
            }
        }
    }
    public void renderMaster(TETile[][] world, InteractWorld data) {
        renderFrame(world);
        renderHUD(data, world);
        StdDraw.show();
    }
    public void thirstBar(int thirst) {
        int temp;
        double xCoord = 8;
        double yCoord = 3;
        for (int x = 2; x <= 20; x += 2) {
            temp = thirst - x;
            int num;
            if (temp >= 0) {
                num = 2;
            } else if (temp == -1) {
                num = 1;
            } else {
                num = 0;
            }
            if (x == 2) {
                //first
                StdDraw.picture(xCoord, yCoord, "byow/TileEngine/Tilesets/HUD/first" + num + ".png");

            } else if (x == 20) {
                //last
                StdDraw.picture(xCoord, yCoord, "byow/TileEngine/Tilesets/HUD/last" + num + ".png");
            } else {
                //mid
                StdDraw.picture(xCoord, yCoord, "byow/TileEngine/Tilesets/HUD/mid" + num + ".png");
            }
            xCoord += 1.2;
        }
    }
    public void healthBar(int health, boolean burnt) {
        int temp;
        double xCoord = 8;
        double yCoord = 5;
        for (int x = 2; x <= 20; x += 2) {
            temp = health - x;
            int num;
            if (temp >= 0) {
                num = 2;
            } else if (temp == -1) {
                num = 1;
            } else {
                num = 0;
            }
            StdDraw.picture(xCoord, yCoord, "byow/TileEngine/Tilesets/HUD/heart" + num + ".png");
            xCoord += 1.8;
        }
    }
    public void showCoins(int numCoins) {
        int first = Math.floorDiv(numCoins, 10);
        int second = Math.floorMod(numCoins, 10);
        double initX = 32.5;
        double initY = 4;
        int diff = 3;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/coin.png");
        initX += diff;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/x.png");
        initX += diff;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/" + first + ".png");
        initX += diff;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/" + second + ".png");
    }
    public void showPotions(int numPotions) {
        int first = Math.floorDiv(numPotions, 10);
        int second = Math.floorMod(numPotions, 10);
        double initX = 50;
        double initY = 4;
        int diff = 3;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/potion.png");
        initX += diff;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/x.png");
        initX += diff;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/" + first + ".png");
        initX += diff;
        StdDraw.picture(initX, initY, "byow/TileEngine/Tilesets/HUD/" + second + ".png");
    }
    public void minigameRender(TETile[][] world, InteractWorld data, SnakeGame snakes) {
        renderFrame(world);
        renderHUD(data, world);
        snakes.display();
        StdDraw.show();
    }
    public void pressedTile() {
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.textLeft(55, 43, "PRESSED BLOCK: " + pressed.description());
    }
}

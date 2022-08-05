package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you",
            "byow/TileEngine/Tilesets/player3.png");
    public static final TETile AVATARWATER = new TETile('@', Color.white, Color.black, "you're in water!",
            "byow/TileEngine/Tilesets/playerwater.png");
    public static final TETile AVATARLAVA = new TETile('@', Color.white, Color.black, "you're in lava!",
            "byow/TileEngine/Tilesets/playerlava.png");
    public static final TETile AVATARICE = new TETile('@', Color.white, Color.black, "you're on ice!",
            "byow/TileEngine/Tilesets/playerice.png");
    public static final TETile AVATARICEHURT = new TETile('@', Color.white, Color.black, "you're on ice!",
            "byow/TileEngine/Tilesets/playericehurt.png");
    public static final TETile AVATARHURT = new TETile('@', Color.white, Color.black, "you're hurt!",
            "byow/TileEngine/Tilesets/player4.png");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", "byow/TileEngine/Tilesets/wall7.png");
    public static final TETile FLOOR = new TETile('.', new Color(128, 192, 128), Color.black,
            "floor", "byow/TileEngine/Tilesets/floor6.png");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water",
            "byow/TileEngine/Tilesets/water4.png");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    public static final TETile COIN = new TETile('✪', Color.yellow, Color.black, "coin",
            "byow/TileEngine/Tilesets/coin2.png");
    public static final TETile LAVA = new TETile('≈', Color.red, Color.black, "lava",
            "byow/TileEngine/Tilesets/lava2.png");
    public static final TETile ICE = new TETile('▒', Color.cyan, Color.black, "ice",
            "byow/TileEngine/Tilesets/ice2.png");
    public static final TETile HEALTH = new TETile('+', Color.green, Color.black,
            "health potion", "byow/TileEngine/Tilesets/health5.png");
    public static final TETile ARCADE = new TETile('+', Color.green, Color.black,
            "Snake Game arcade", "byow/TileEngine/Tilesets/portal1.png");
}



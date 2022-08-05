package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.WorldGen.Entity;
import byow.lab12.RenderWorld;
import byow.WorldGen.World;
import byow.WorldGen.Direction;
import byow.WorldGen.Point;

import byow.InputDemo.StringInputDevice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;


public class InteractWorld {
    private static final String FILENAME = "save.txt";
    private static final String FILENAME2 = "save2.txt";
    public static final int MAXLEVEL = 3;
    private static boolean thirstMechanic = false;
    private static boolean burntMechanic = true;
    private String prevLevelSeed;
    private String prevLeveMovements;
    private String worldSeed;
    private String movements;
    private String quickSave; //helps for both quicksave and levels
    private int level;
    private boolean toSave = false;
    private boolean retrieveSuccess = true;
    private World world;
    private HashMap directions;

    private boolean burnt = false;
    private int health;
    private int thirst;

    private static final int FPSIZE = 21;
    private TETile[][] fpWorld;
    private static final int FPHEIGHT = 40;
    private static final int FPWIDTH = 80;
    private boolean firstPerson = false;
    private boolean mapOpen = false;
    private boolean gameOverLev = false;
    private boolean realTime = false;
    private Entity entity;
    private boolean crouch = false;
    private boolean godMode = false;
    private Direction prevMoveDir;

    public InteractWorld(String seed, int lev, boolean time) {
        directions = new HashMap(4);
        directions.put('W', Direction.NORTH);
        directions.put('A', Direction.WEST);
        directions.put('S', Direction.SOUTH);
        directions.put('D', Direction.EAST);
        worldSeed = seed;

        health = 20;
        thirst = 20;
        movements = "";
        quickSave = "";
        prevLevelSeed = "";
        prevLeveMovements = "";
        realTime = time;
        level = lev;
        if (level == 1) {
            thirstMechanic = false;
            burntMechanic = false;
        } else if (level != 1) {
            throw new IllegalArgumentException("We start with 1 level");
        }

        StringInputDevice seedChars = new StringInputDevice(worldSeed);
        initWorldRender(seedChars);


        fpWorld = new TETile[FPWIDTH][FPHEIGHT];
        for (int x = 0; x < FPWIDTH; x += 1) {
            for (int y = 0; y < FPHEIGHT; y += 1) {
                fpWorld[x][y] = Tileset.NOTHING;
            }
        }

        //world either by generating or accessing save
        //while has next input
        //if player movement command -
        //  move player and add to instance string
        //otherwise check for :Q
        //throw exception
    }
    public void initWorldRender(StringInputDevice seedChars) {
        if (seedChars.getNextKey() == 'L') {
            //retrieve the saved thing
            try {
                BufferedReader br;
                if (realTime) {
                    br = new BufferedReader(new FileReader(FILENAME2));
                } else {
                    br = new BufferedReader(new FileReader(FILENAME));
                }
                String s = br.readLine();
                StringInputDevice savedReader = new StringInputDevice(s);
                if (savedReader.getNextKey() != 'N') {
                    throw new IllegalArgumentException();
                }
                retrieveInitWorld(savedReader);
                movementCompute(savedReader);

            } catch (IOException e) {
                System.out.println("Error occurred during file read");
                retrieveSuccess = false;
                return;
            }
        } else {
            retrieveInitWorld(seedChars);
        }
        movementCompute(seedChars); //resume commands if save was retrieved
    }
    /**
     * Given a string seed, this function retrieves the initial state of the world from RenderWorld
     * @param seedChars
     */
    public void retrieveInitWorld(StringInputDevice seedChars) {
        //automatically assuming it's N and a new seed
        String seed = "";
        char curr = seedChars.getNextKey();
        while (curr != 'S') {
            seed += curr;
            curr = seedChars.getNextKey();
        }
        //System.out.println("Interpreted the seed into" + seed);
        worldSeed = "N" + seed + "S";
        if (level == 1) {
            prevLevelSeed = worldSeed;
        }
        world = RenderWorld.renderWorld(Long.parseLong(seed), level); //something like this
        if (level == 3) {
            entity = new Entity(world.getOpenPoints(), world);
            entity.nextMove();

        }
    }
    public void movementCompute(StringInputDevice seedChars) {
        while (seedChars.possibleNextInput()) {
            char c = seedChars.getNextKey();
            Direction prevDir = interpretChar(c);
            moveEntity(); //insert movement of entities here ---- asdfg
            if (prevDir != null) {
                valuesTrack();
                while (prevDir != null) {
                    prevDir = tileEffect(prevDir); //including the sand and other stuff
                    moveEntity();
                    levelUp();
                }
            }
        }
    }
    /**
     * This function is a bit flexibble and ignores all irregular commands
     * If a movement occurs, it's direction is returned.
     * If the movement was not viable or an irregular command was entered, null is returned
     * null basically means that the screen needn't be refreshed
     * @param c
     * @return
     */
    public Direction interpretChar(char c) {
        if (toSave) {
            if (c == 'Q') {
                saveWorld();
            } else if (c != '`') {
                toSave = false;
            }
        }
        if (c == ':') {
            toSave = true;
        } else if (directions.containsKey(c)) {
            Direction currDir = (Direction) directions.get(c);
            if (world.playerMove(currDir)) {
                movements += c;
                prevMoveDir = currDir;
                return currDir;
            }
        } else if (c == 'V') {
            firstPerson = !firstPerson;
            mapOpen = false;
        } else if (c == 'C') {
            crouch = !crouch;
            movements += c;
            thirst += 1;
            return prevMoveDir;
        } else if (c == 'M') {
            mapOpen = !mapOpen;
        } else if (c == 'Q') {
            quickSave = prevLeveMovements + movements; //may need to edit ----
        } else if (c == 'U') {
            if (world.getNumPotions() > 0 && health < 20) {
                world.usePotion();
                health = 20;
                movements += c;
            }
        } else if (c == '`' && !toSave) {
            movements += c;
        } else if (c == 'G') {
            thirstMechanic = false;
            burntMechanic = false;
            world.godMode();
            godMode = true;
            movements += c;
        }
        return null;
    }
    /**
     * Considers whether the player is currently on an Effect tile. Takes according action.
     * @param dir
     */
    public Direction tileEffect(Direction dir) {
        /*
        Dir is the direction of the previous movement
        Example: sand
        If the player is on sand, then try playerMove again
        If playerMove returns false, try playerMove at the opposite direction, 
        which realistically should never return false
        This would mimic bouncing against the wall when nowhere to slide
         */
        TETile current = world.getStaticWorld()[world.getPlayerXPos()][world.getPlayerYPos()];
        if (current == Tileset.ICE && !crouch) {
            if (world.playerMove(dir)) {
                return dir;
            } else {
                world.playerMove(Direction.opposite(dir));
                return Direction.opposite(dir);
            }
        } else if (current == Tileset.COIN) {
            world.getCoin();
            //updates the on-screen message based on
        } else if (current == Tileset.LAVA) {
            //Idea: Time-based stuff is irrelevant in a game like this, but steps can be counted
            //Starts a counter of steps --
            //Burnt, must reach water within 20 steps -- or gameover
            world.updateburn(true);
            if (burnt && burntMechanic) {
                health -= 4;
            } else {
                burnt = true;
            }
        } else if (current == Tileset.WATER) {
            //heals the player? read above
            world.updateburn(false);
            burnt = false;
            thirst = 20;
        } else if (current == Tileset.HEALTH) {
            world.getPotion();
        } else if (current == Tileset.UNLOCKED_DOOR) {
            gameOverLev = true;
        }
        return null;
    }

    public void valuesTrack() {
        TETile current = world.getStaticWorld()[world.getPlayerXPos()][world.getPlayerYPos()];
        if (current != Tileset.WATER && current != Tileset.HEALTH && burnt && burntMechanic) {
            health -= 1;
        }
        if (current != Tileset.WATER && thirstMechanic) {
            thirst -= 1;
        }
    }
    public void levelUp() {
        if (gameOverLev) {
            long nextSeed = worldSeedval();
            if (nextSeed <= 1) {
                nextSeed += 1;
            } else {
                nextSeed -= 1;
            }
            prevLeveMovements += movements;
            level += 1;
            if (level == 2) {
                thirstMechanic = true;
                burntMechanic = true;
            } else if (level == 3) {
                thirstMechanic = false;
                burntMechanic = false;
            }
            crouch = false;
            godMode = false;
            burnt = false;
            movements = "";
            gameOverLev = false;
            StringInputDevice strSeed = new StringInputDevice("N" + nextSeed + "S");
            initWorldRender(strSeed);
        }
    }
    /**
     * To be safe, this completely overwrites the save file with the current worldSeed string
     */
    public void saveWorld() {
        try {
            BufferedWriter myWriter;
            if (realTime) {
                myWriter = new BufferedWriter(new FileWriter(FILENAME2));
            } else {
                myWriter = new BufferedWriter(new FileWriter(FILENAME));
            }
            myWriter.write(prevLevelSeed + prevLeveMovements + movements);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Error occurred during save");
        }
    }

    public void moveEntity() {
        //System.out.println("Run");
        if (level >= 3) {
            entity.nextMove();
        }
    }

    public TETile[][] getWorld() {
        if (firstPerson) {
            return getFPWorld();
        } else {
            return getDynamicWorld();
        }
    }
    /**
     * Returns the dynamic world with the player for rendering
     * @return
     */
    public TETile[][] getDynamicWorld() { //effectively the one to return and render
        TETile[][] toReturn = world.getStaticWorld().clone();
        //can put lighting
        toReturn[world.getPlayerXPos()] = world.getStaticWorld()[world.getPlayerXPos()].clone();
        if (!world.playerAtDoor()) {
            toReturn[world.getPlayerXPos()][world.getPlayerYPos()] = world.playerAvatar();
        }
        toReturn[world.getEntityXPos()] = toReturn[world.getEntityXPos()].clone();
        toReturn[world.getEntityXPos()][world.getEntityYPos()] = Tileset.AVATARHURT;
        return toReturn;
    }
    public TETile[][] getFPWorld() {
        int orientX = 4;
        int orientY = 2;
        int proj = (FPSIZE - 1) / 2;
        TETile[][] temp = getDynamicWorld();
        int startX = world.getPlayerXPos() - proj;
        int startY = world.getPlayerYPos() - proj;
        for (int x = 0; x < FPSIZE; x += 1) {
            for (int y = 0; y < FPSIZE; y += 1) {
                int x1 = startX + x;
                int y1 = startY + y;
                TETile t = Tileset.NOTHING;
                if (x1 > 0 && x1 < 80 && y1 > 0 && y1 < 40) { // ----------uses assumed dimensions
                    fpWorld[orientX + x][orientY + y] = temp[x1][y1];
                } else {
                    fpWorld[orientX + x][orientY + y] = t;
                }
            }
        }
        return fpWorld;
    }
    public void lightingUpdate() {
        ArrayList<Point> litUp = new ArrayList<>();
        Direction playerDir = Direction.NORTH;
        Point temp = new Point(world.getPlayerXPos(), world.getPlayerYPos());
        litUp.add(temp);


        int straight = 0;
    }
    public boolean burnEvent() {
        return burnt;
    }
    public int endDetermine() {
        if (world.playerAtDoor() && level == MAXLEVEL) {
            return -1; //win
        } else if (thirst <= 0 || health <= 0) {
            return -3; //lose
        } else if (level == 3 && world.entityAtPlayer()) {
            return -3;
        } else {
            return -2; //ongoing
        }
    }
    public int getHealth() {
        return health;
    }
    public int getThirst() {
        return thirst;
    }
    public int getCoins() {
        return world.numCoins();
    }
    public boolean getToSave() {
        return toSave;
    }
    public boolean retrievalSuccess() {
        return retrieveSuccess;
    }
    public boolean isBurnt() {
        return burnt;
    }
    public boolean isFirstPerson() {
        return firstPerson;
    }
    public String getMovements() {
        return movements;
    }
    public String getWorldSeed() {
        return worldSeed;
    }
    public boolean getmapOpen() {
        return mapOpen;
    }
    public int getLevel() {
        return level;
    }
    public String getQuickSave() {
        return prevLevelSeed + quickSave;
    }
    public long worldSeedval() {
        try {
            long temp = Long.parseLong(worldSeed.substring(1, worldSeed.length() - 1));
            return temp;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public String prevSeed() {
        return prevLevelSeed + prevLeveMovements;
    }
    public String initSeed() {
        return prevLevelSeed;
    }
    public String allMovements() {
        return prevLeveMovements + movements;
    }
    public int numPotions() {
        return world.getNumPotions();
    }
    public boolean playerArcade() {
        return world.playerAtArcade();
    }
    public boolean isRealTime() {
        return realTime;
    }
    public World get1World() {
        return world;
    }
    public boolean isCrouch() {
        return crouch;
    }
    public boolean isGodMode() {
        return godMode;
    }
}

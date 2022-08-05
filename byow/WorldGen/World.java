package byow.WorldGen;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;

public class World {
    private TETile[][] renderMaster;

    private int doorXPos;
    private int doorYPos;

    private int numPlayers;
    private int playerXPos;
    private int playerYPos;
    private int coinsLeft;
    private int numPotions;
    private boolean burnt = false;
    private int entityXPos;
    private int entityYPos;
    private ArrayList<Point> openPoints;


    public static final TETile DEFAULTAVATAR = Tileset.AVATAR;


    public World(TETile[][] world, int xDoor, int yDoor) {
        renderMaster = world;
        doorXPos = xDoor;
        doorYPos = yDoor;
        numPlayers = 0;
        numPotions = 1; //start with 1
        coinsLeft = 20;
    }



    /**
     * Considers the movement and returns whether the change must be registered - is it a valid move
     * @param d
     * @return
     */
    public boolean playerMove(Direction d) {

        int xPos = playerXPos;
        int yPos = playerYPos;
        if (d == Direction.NORTH) {
            yPos += 1;
        } else if (d == Direction.SOUTH) {
            yPos -= 1;
        } else if (d == Direction.EAST) {
            xPos += 1;
        } else if (d == Direction.WEST) {
            xPos -= 1;
        }
        if (moveAble(xPos, yPos)) {
            playerXPos = xPos;
            playerYPos = yPos;
            return true;
        }
        return false;
    }


    public void addPlayer(int xPos, int yPos) {
        if (numPlayers == 1) {
            throw new ArrayIndexOutOfBoundsException("Player limit exceeded");
        }
        if (moveAble(xPos, yPos)) {
            playerXPos = xPos;
            playerYPos = yPos;
        } else {
            throw new UnsupportedOperationException("Illegal Player placement");
        }
    }
    public boolean moveAble(int xPos, int yPos) {
        if (xPos < 0 || xPos > renderMaster.length || yPos < 0 || yPos > renderMaster[0].length) {
            return false;
        }
        TETile t = renderMaster[xPos][yPos];
        if (Tileset.NOTHING == t || Tileset.WALL == t || Tileset.LOCKED_DOOR == t) {
            return false;
        }
        //if (xPos == playerXPos && yPos == playerYPos) {
        //    return false;
        //}
        return true;
    }
    public void getCoin() {
        if (renderMaster[playerXPos][playerYPos] != Tileset.COIN) {
            throw new IllegalCallerException("No coin here?");
        }
        renderMaster[playerXPos][playerYPos] = Tileset.FLOOR;
        coinsLeft -= 1;
        if (coinsLeft == 0) {
            renderMaster[doorXPos][doorYPos] = Tileset.UNLOCKED_DOOR;
        }
    }
    public void getPotion() {
        if (renderMaster[playerXPos][playerYPos] != Tileset.HEALTH) {
            throw new IllegalCallerException("No Potion here?");
        }
        renderMaster[playerXPos][playerYPos] = Tileset.FLOOR;
        numPotions += 1;
    }
    public void usePotion() {
        if (numPotions > 0) {
            numPotions -= 1;
        }
    }

    public void worldUpdate(TETile[][] t) {
        renderMaster = t;
    }
    public TETile[][] getStaticWorld() {
        return renderMaster;
    }
    public int getPlayerXPos() {
        return playerXPos;
    }
    public int getPlayerYPos() {
        return playerYPos;
    }
    public int getNumPlayers() {
        return numPlayers;
    }
    public int numCoins() {
        return 20 - coinsLeft;
    }
    public int getNumPotions() {
        return numPotions;
    }

    public boolean playerAtDoor() {
        return (playerYPos == doorYPos && playerXPos == doorXPos);
    }
    public boolean playerAtArcade() {
        return renderMaster[playerXPos][playerYPos] == Tileset.ARCADE;
    }
    
    public TETile playerAvatar() {
        TETile current = renderMaster[playerXPos][playerYPos];
        if (current == Tileset.ICE) {
            if (burnt) {
                return Tileset.AVATARICEHURT;
            }
            return Tileset.AVATARICE;
        } else if (current == Tileset.LAVA) {
            return Tileset.AVATARLAVA;
        } else if (current == Tileset.WATER) {
            return Tileset.AVATARWATER;
        } else if (burnt) {
            return Tileset.AVATARHURT;
        }
        return DEFAULTAVATAR;
    }

    public void reduceLevel(int level) {
        if (level == 1) {
            renderMaster[doorXPos][doorYPos] = Tileset.UNLOCKED_DOOR;
        } //2 is the default, we can add additional functionality for the later levels
    }

    public void updateburn(boolean b) {
        burnt = b;
    }
    public int getDoorXPos() {
        return doorXPos;
    }
    public int getDoorYPos() {
        return doorYPos;
    }
    public int getEntityXPos() {
        return entityXPos;
    }
    public int getEntityYPos() {
        return entityYPos;
    }
    public void entityPosUpdate(int x, int y) {
        //System.out.println("Someone's changing position to " + x + "  " + y);
        entityXPos = x;
        entityYPos = y;
    }

    public boolean entityAtPlayer() {
        return entityXPos == playerXPos && entityYPos == playerYPos;
    }
    public void addOpenPoints(ArrayList<Point> p) {
        openPoints = p;
    }
    public ArrayList<Point> getOpenPoints() {
        return openPoints;
    }
    public void godMode() {
        renderMaster[doorXPos][doorYPos] = Tileset.UNLOCKED_DOOR;
    }

}

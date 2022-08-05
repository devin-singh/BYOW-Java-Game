package byow.Core;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



import byow.InputDemo.StringInputDevice;
import byow.InputDemo.ThreadboardInputSource;
import byow.TileEngine.TERenderer;

import byow.WorldGen.Direction;
import edu.princeton.cs.introcs.StdDraw;

public class Menu implements Runnable {
    private static int height = 40;
    private static int width = 80;

    private ThreadboardInputSource keyInput;
    private static boolean quit = false;
    private static TERenderer ter;

    public Menu(ThreadboardInputSource inputSource) {
        keyInput = inputSource;
    }

    public void run() {
        init();
    }
    public void init() {
        StdDraw.setCanvasSize(width * 16, height * 16);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        ter = new TERenderer();
        ter.initialize(width, height + 8, 0, 8);

        while (!quit) {
            mainMenu();
            char input = keyInput.getNextKey();
            if (input == 'Q') {
                quit = true;
            } else if (input == 'N') {
                seedEnter(false);
            } else if (input == 'L') {
                if (!gameLoop("L", false)) {
                    noSeedMsg();
                }
            } else if (input == 'G') {
                realTimeMenu();
            }
        }
        System.exit(1);
    }
    public void mainMenu() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 90));
        StdDraw.textLeft(25, 30, "THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));

        StdDraw.textLeft(30, 25, "New Game (N)");
        StdDraw.textLeft(30, 20, "Load Game (L)");
        StdDraw.textLeft(33, 15, "Quit (Q)");
        StdDraw.show();
    }
    public void realTimeMenu() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 90));
        StdDraw.textLeft(20, 30, "THE REAL GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));

        StdDraw.textLeft(30, 25, "New Game (N)");
        StdDraw.textLeft(30, 20, "Load Game (L)");
        StdDraw.textLeft(33, 15, "Exit (E)");
        StdDraw.show();
        HashSet options = new HashSet();
        options.addAll(List.of('N', 'L', 'E'));
        char choice = multChoice(options);
        if (choice == 'N') {
            seedEnter(true);
        } else if (choice == 'L') {
            if (!gameLoop("L", true)) {
                noSeedMsg();
            }
        }
    }

    public boolean gameLoop(String input, boolean time) {
        StdDraw.clear(new Color(0, 0, 0));
        //look into what happens when there's no save
        InteractWorld gameWorld = new InteractWorld(input, 1, time); //renderWorld(812259);
        if (!gameWorld.retrievalSuccess()) {
            return false;
        }
        // draws the world to the screen
        HashSet validMoves = new HashSet();
        if (time) {
            validMoves.addAll(List.of('W', 'A', 'S', 'D', 'U', '`'));
        } else {
            validMoves.addAll(List.of('W', 'A', 'S', 'D', 'U'));
        }

        gameWorld.interpretChar('Q');
        while (gameWorld.endDetermine() == -2) {
            ter.renderMaster(gameWorld.getWorld(), gameWorld);
            if (keyInput.possibleNextInput() || gameWorld.isRealTime()) {
                char c = keyInput.getNextKey();
                if (c == 'E') { //Exit
                    if (exitConfirm()) {
                        System.out.println("Here");
                        return true;
                    }
                    ter.renderMaster(gameWorld.getWorld(), gameWorld);
                } else if (c == 'R') { //Replay
                    char choice = replayOption();
                    if (choice == 'R') {
                        String wseed = gameWorld.prevSeed();
                        String wmove = gameWorld.getMovements();
                        if (wmove.length() > 0) {
                            moveReplay(wseed, wmove, gameWorld.isFirstPerson(), time);
                        }
                    } else if (choice == 'N') {
                        String iseed = gameWorld.initSeed();
                        String imove = gameWorld.allMovements();
                        if (imove.length() > 0) {
                            moveReplay(iseed, imove, gameWorld.isFirstPerson(), time);
                        }
                    }
                    //E automatically exits
                } else if (c == 'B') { //BACK -- UNDO //needs work, this recursion will result in a stackOverFlow Exception
                    String moves = gameWorld.allMovements();
                    if (moves.length() > 0) {
                        moves = moves.substring(0, moves.length() - 1);
                        gameLoop(gameWorld.initSeed() + moves, time);
                    }
                } else if (c == 'H') { //Help
                    helpScreen();
                } else if (c == 'P' && gameWorld.playerArcade()) {
                    snakeGame(gameWorld.isFirstPerson(), gameWorld, ter);
                }
                Direction prevDir = gameWorld.interpretChar(c);
                if (validMoves.contains(c)) {
                    //System.out.println(gameWorld.get1World().getEntityXPos() + " main1  " + gameWorld.get1World().getEntityYPos());
                    gameWorld.moveEntity();
                    //System.out.println(gameWorld.get1World().getEntityXPos() + " main2  " + gameWorld.get1World().getEntityYPos());
                }
                ter.renderMaster(gameWorld.getWorld(), gameWorld);
                if ((c == 'Q' && gameWorld.getToSave())) {
                    return true;
                }
                //checking whether key is valid
                if (prevDir != null) {
                    gameWorld.valuesTrack();
                    //movement of entities
                    while (prevDir != null) {
                        prevDir = gameWorld.tileEffect(prevDir); //including the sand and other stuff
                        //gameWorld.moveEntity();
                        gameWorld.levelUp();
                        StdDraw.pause(100);
                        //time-based ai movement
                        ter.renderMaster(gameWorld.getWorld(), gameWorld);
                    }
                    keyInput.wipe();
                }
            }
            //time-based ai movement
            //keyInput.wipe(); //could do this for seamless movement and no glitches
            StdDraw.pause(100);
        }

        if (gameWorld.endDetermine() == -1) {
            winMsg();
            HashSet temp = new HashSet();
            temp.add('Q');
            multChoice(temp);
        } else if (gameWorld.endDetermine() == -3) {
            String recent = gameWorld.getQuickSave();
            loseMsg(recent);
            HashSet temp = new HashSet();
            temp.addAll(List.of('R', 'Q', 'N'));
            char ans = multChoice(temp);
            if (ans == 'R' && recent.length() > 0) {
                return gameLoop(recent, time);
            } else if (ans == 'N') {
                seedEnter(time);
            }
        }
        return true;
    }
    public char replayOption() {
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(40, 20, 15, 8);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 70));
        StdDraw.textLeft(30, 25, "REPLAY");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.textLeft(30, 20, "Level Replay (R)");
        StdDraw.textLeft(30, 17, "Game Replay (N)");
        StdDraw.textLeft(33, 14, "Cancel (E)");
        HashSet options = new HashSet();
        options.addAll(List.of('E', 'N', 'R'));
        StdDraw.show();
        return multChoice(options);
    }
    public void moveReplay(String seed, String moves, boolean fP, boolean timeThing) {
        //look into what happens when there's no save
        InteractWorld gameWorld = new InteractWorld(seed, 1, timeThing); //renderWorld(812259);
        if (!gameWorld.retrievalSuccess()) {
            return;
        }
        // draws the world to the screen
        if (fP) {
            gameWorld.interpretChar('V');
        }
        ter.renderMaster(gameWorld.getWorld(), gameWorld);
        StringInputDevice replayMoves = new StringInputDevice(moves);
        int time = 8000 / moves.length();
        if (time > 50) {
            time = 30;
        }
        while (replayMoves.possibleNextInput()) {
            if (keyInput.getNextKey() != '`') {
                return;
            }
            Direction prevDir = gameWorld.interpretChar(replayMoves.getNextKey());
            gameWorld.moveEntity();
            ter.renderMaster(gameWorld.getWorld(), gameWorld);

            if (prevDir != null) {
                gameWorld.valuesTrack();
                while (prevDir != null) {
                    prevDir = gameWorld.tileEffect(prevDir); //including the sand and other stuff
                    gameWorld.moveEntity();
                    gameWorld.levelUp();
                    StdDraw.pause(time);
                    ter.renderMaster(gameWorld.getWorld(), gameWorld);
                }
            }
            StdDraw.pause(time);
        }
        keyInput.wipe();
    }
    public void snakeGame(boolean fP, InteractWorld main, TERenderer render) {
        HashMap directions = new HashMap();
        directions = new HashMap(4);
        directions.put('W', Direction.NORTH);
        directions.put('A', Direction.WEST);
        directions.put('S', Direction.SOUTH);
        directions.put('D', Direction.EAST);
        SnakeGame game = new SnakeGame(!fP);
        game.init();
        game.display();
        StdDraw.show();
        while (!game.isGameOver()) {
            if (keyInput.possibleNextInput()) {
                char c = keyInput.getNextKey();
                Direction d = (Direction) directions.get(c);
                if (directions.containsKey(c) && d != game.prevDir()) {
                    //System.out.println("Movement" + c);
                    game.movement(d);
                }
            } else {
                game.movement(game.prevDir());
            }
            game.display();
            render.minigameRender(main.getWorld(), main, game);
            //render.renderMaster(main.getWorld(), main);
            StdDraw.pause(100);
        }
        game.getScore();
        anyKey();
    }
    public void seedEnter(boolean time) {
        //page when the seed is being entered
        String seed = "";
        StdDraw.setPenColor(Color.WHITE);
        //StdDraw.textLeft(23, 13, "Cannot exceed 9,223,372,036,854,775,807");
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
            StdDraw.textLeft(30, 25, "ENTER SEED");
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
            StdDraw.textLeft(15, 15, "Only numbers. Press D to delete and S when done");
            StdDraw.textLeft(30, 20, seed);
            StdDraw.show();
            char c = keyInput.getNextKey();
            if ((c == 'S' && numCheck(seed)) || seed.length() == 18) {
                break;
            } else if (c == 'E') {
                if (exitConfirm()) {
                    return;
                }
                continue;
            } else if (c == 'D' && seed.length() > 0) {
                seed = seed.substring(0, seed.length() - 1);
                continue;
            }
            String nextIn = "" + c;
            if (numCheck(nextIn)) {
                seed += nextIn;
            }
        }
        //introCredits();
        //introHelp();
        gameLoop("N" + seed + "S", time);
    }
    public void helpScreen() {
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(40, 27, 25, 16);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        StdDraw.textLeft(38, 40, "HELP");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.textLeft(21, 36, "Player 1");
        StdDraw.textLeft(23, 34, "U (Up)");
        StdDraw.line(24.5, 33, 24.5, 29);
        StdDraw.line(22.5, 31, 26.5, 31);
        StdDraw.textLeft(16, 31, "A (Left)");
        StdDraw.textLeft(27, 31, "D (Right)");
        StdDraw.textLeft(21, 28, "S (Down)");

        StdDraw.textLeft(47, 36, "E - Exit");
        StdDraw.textLeft(47, 33, "V - Change View");
        StdDraw.textLeft(47, 30, "M - Open Map");
        StdDraw.textLeft(47, 27, "U - Use Potion");
        StdDraw.textLeft(47, 24, "B - Undo");
        StdDraw.textLeft(47, 21, "Q - QuickSave");
        StdDraw.textLeft(46, 18, ":Q - Save & Quit");

        StdDraw.textLeft(30, 13, "Press any Key to Continue");
        StdDraw.show();
        anyKey();
    }
    public void introHelp() {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        StdDraw.textLeft(29, 37, "Starting Game...");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.textLeft(20, 30, "Press H for information about the Controls");
        StdDraw.textLeft(20, 27, "The Quick Save function applies only for your current session");
        StdDraw.textLeft(20, 24, "Click on a tile to view it's information");
        StdDraw.textLeft(20, 21, "You must overcome all obstacles to open and enter the door");
        StdDraw.textLeft(20, 14, "Press Any Key to continue");
        StdDraw.show();
        anyKey();
    }
    public void introCredits() {
        //this has the intro credits
    }
    public char multChoice(HashSet options) {
        char n;
        while (true) {
            n = keyInput.getNextKey();
            if (options.contains(n)) {
                return n;
            }
        }
    }
    public void winMsg() {
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(40, 23, 15, 6);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 70));
        StdDraw.textLeft(30, 25, "YOU WIN!");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.textLeft(35, 20, "QUIT (Q)");

        StdDraw.show();
    }
    public void loseMsg(String save) {
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(40, 20, 15, 8);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 70));
        StdDraw.textLeft(30, 25, "YOU LOSE");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.textLeft(35, 20, "Quit (Q)");
        StdDraw.textLeft(33, 17, "New Game (N)");
        if (save.length() > 0) {
            StdDraw.textLeft(30, 14, "Most Recent Save (R)");
        }

        StdDraw.show();
    }
    public boolean numCheck(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void noSeedMsg() {
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(40, 23, 15, 6);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        StdDraw.textLeft(28, 25, "No Save Found");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.textLeft(35, 20, "OK (K)");
        StdDraw.show();
        HashSet options = new HashSet();
        options.add('K');
        multChoice(options);
    }
    public boolean exitConfirm() {
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(40, 20, 22, 9);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        StdDraw.textLeft(36, 25, "EXIT");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.textLeft(36, 20, "YES (E)");
        StdDraw.textLeft(21, 15, "PRESS ANY OTHER CHARACTER TO RESUME");
        StdDraw.show();
        return anyKey() == 'E';
    }
    public char anyKey() {
        char c = keyInput.getNextKey();
        while (c == '`') {
            c = keyInput.getNextKey();
        }
        System.out.println(c);
        return c;
    }
    public static void main(String[] args) {
        //gonna implement in a sec
        ThreadboardInputSource keys = new ThreadboardInputSource();
        Thread gameStart = new Thread(new Menu(keys));
        Thread keyStart = new Thread(keys);
        keyStart.start();
        gameStart.start();
    }
}

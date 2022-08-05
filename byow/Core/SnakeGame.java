package byow.Core;

import byow.WorldGen.Direction;

import java.awt.Font;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

import byow.WorldGen.Point;
import edu.princeton.cs.introcs.StdDraw;

public class SnakeGame {
    private int score;
    private boolean gameOver;
    private boolean size;
    private HashMap directions;
    private Direction currDir;

    private LinkedList<Point> snake;
    private Point nextCoin;
    private Random coins;

    private Point[][] world;

    private static final double LARGEX = 20;
    private static final double LARGEY = 10;
    private static final double  LARGEMULT = 0.6;
    private static int worldSize = 40;
    private ArrayList<Point> wall;

    private static final double SMALLX = 5;
    private static final double SMALLY = 30;
    private static final double  SMALLMULT = 0.4;

    public SnakeGame(boolean large) {
        size = large;
        currDir = Direction.NORTH;

        snake = new LinkedList<>();
        coins = new Random();
        world = new Point[worldSize][worldSize];

        for (int x = 0; x < worldSize; x += 1) {
            for (int y = 0; y < worldSize; y += 1) {
                world[x][y] = new Point(x, y);
            }
        }
        wall = new ArrayList<>();
        for (int x = -1; x <= worldSize; x += 1) {
            wall.add(new Point(x, -1));
            wall.add(new Point(x, worldSize));
            wall.add(new Point(x, worldSize + 1));
            wall.add(new Point(-1, x));
            wall.add(new Point(worldSize, x));
        }
    }

    private void genCoin() {
        Point temp = world[coins.nextInt(worldSize)][coins.nextInt(worldSize)];
        while (!validate(temp)) {
            temp = world[coins.nextInt(worldSize)][coins.nextInt(worldSize)];
        }
        nextCoin = temp;
    }
    public void init() {
        snake.add(new Point(20, 20));
        genCoin();
    }
    public void movement(Direction d) {
        if (d != Direction.opposite(currDir)) {
            Point nextPoint = dirApply(snake.getFirst(), d);
            if (nextPoint == null) {
                gameOver = true;
            } else {
                snake.addFirst(nextPoint);
                if (!nextPoint.equals(nextCoin)) {
                    snake.removeLast();
                } else {
                    score += 1;
                    genCoin();
                }
            }
        }
    }

    public void display() {
        double startX;
        double startY;
        double halfLength;
        double mult;
        double tileSize;
        int text;

        if (size) {
            startX = LARGEX;
            startY = LARGEY;
            mult = LARGEMULT;
            text = 20;
        } else {
            startX = SMALLX;
            startY = SMALLY;
            mult = SMALLMULT;
            text = 10;
        }
        halfLength = (worldSize * mult) / 2;
        tileSize = mult / 2; //some equation
        //black
        StdDraw.setPenColor(Color.black);
        StdDraw.filledSquare(startX + halfLength, startY + halfLength, halfLength + mult);
        //draw border
        StdDraw.setPenColor(Color.blue);
        for (Point p: wall) {
            double xPos = (p.getxPos() * mult) + startX;
            double yPos = (p.getyPos() * mult) + startY;
            StdDraw.filledSquare(xPos + mult, yPos + mult, tileSize);
        }
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, text);
        StdDraw.setFont(font);
        double textX = (2 * mult) + startX;
        double textY = (worldSize * mult) + startY + mult;
        StdDraw.textLeft(textX, textY, "SCORE: " + score);

        for (Point p: snake) {
            StdDraw.setPenColor(Color.red);
            double xPos = (p.getxPos() * mult) + startX;
            double yPos = (p.getyPos() * mult) + startY;
            StdDraw.filledSquare(xPos + mult, yPos + mult, tileSize);
        }
        StdDraw.setPenColor(Color.YELLOW);
        double xPos = (nextCoin.getxPos() * mult) + startX;
        double yPos = (nextCoin.getyPos() * mult) + startY;
        StdDraw.filledSquare(xPos + mult, yPos + mult, tileSize);
    }
    public boolean validate(Point p) {
        if (p.getyPos() < 0 || p.getyPos() >= worldSize || p.getxPos() < 0 || p.getxPos() >= worldSize) {
            //System.out.println("inValid position");
            return false;
        } else if (snake.contains(p)) {
            //System.out.println("collision imminent");
            return false;
        }
        return true;
    }
    public Point dirApply(Point p, Direction d) {
        int xChange = 0;
        int yChange = 0;
        if (d == Direction.NORTH) {
            yChange = 1;
        } else if (d == Direction.SOUTH) {
            yChange = -1;
        } else if (d == Direction.EAST) {
            xChange = 1;
        } else {
            xChange = -1;
        }
        Point temp = new Point(p.getxPos() + xChange, p.getyPos() + yChange);
        if (validate(temp)) {
            currDir = d;
            return world[p.getxPos() + xChange][p.getyPos() + yChange];
        } else {
            return null;
        }
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void getScore() {
        double startX;
        double startY;
        double halfLength;
        double mult;
        double tileSize;
        int text;

        if (size) {
            startX = LARGEX;
            startY = LARGEY;
            mult = LARGEMULT;
            text = 20;
        } else {
            startX = SMALLX;
            startY = SMALLY;
            mult = SMALLMULT;
            text = 10;
        }
        halfLength = (worldSize * mult) / 2;
        tileSize = mult / 2; //some equation


        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(startX + halfLength, startY + halfLength, (halfLength + mult) / 2, (halfLength + mult) / 4);

        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, text);
        StdDraw.setFont(font);
        double textX = startX + halfLength - (5 * mult);
        double textY = startY + halfLength;
        StdDraw.textLeft(textX, textY, "SCORE: " + score);
        StdDraw.show();
    }
    public Direction prevDir() {
        return currDir;
    }
    public void startMenu() {
        double startX;
        double startY;
        double halfLength;
        double mult;
        double tileSize;
        int text;

        if (size) {
            startX = LARGEX;
            startY = LARGEY;
            mult = LARGEMULT;
            text = 20;
        } else {
            startX = SMALLX;
            startY = SMALLY;
            mult = SMALLMULT;
            text = 10;
        }
        halfLength = (worldSize * mult) / 2;
        tileSize = mult / 2; //some equation


        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.filledRectangle(startX + halfLength, startY + halfLength, (halfLength + mult) / 2, (halfLength + mult) / 3);

        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, text);
        StdDraw.setFont(font);
        double textX = startX + halfLength - (5 * mult);
        double textY = startY + halfLength + (3 * mult);
        StdDraw.textLeft(textX, textY, "SNAKE GAME");
        StdDraw.textLeft(textX, textY - (3 * mult), "Start (S)");
        StdDraw.show();

    }
}

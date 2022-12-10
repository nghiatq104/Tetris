package com.nghiatq.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Board extends JPanel implements KeyListener {
    public static int GAME_PLAY = 0;
    public static int GAME_PAUSE = 1;
    public static int GAME_OVER = 2;

    private int state = GAME_PLAY;

    private static final int FPS = 60;
    private static final int delay = FPS / 1000;

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int BLOCK_SIZE = 30;

    private Random random;

    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];

    private Color[] colors = {Color.pink, Color.red, Color.blue, Color.YELLOW, Color.ORANGE, Color.GREEN, Color.GRAY};

    private Timer looper;

    private Shape[] shapes = new Shape[7];

    private Shape currentShape, nextShape;

    public static int score = 0;

    private void update() {
        if(state == GAME_PLAY){
            currentShape.update();
        }
    }

    public Board() {

        random = new Random();

        //I shape
        shapes[0] = new Shape(new int[][]{
                {1, 1, 1, 1}
        }, this, colors[0]);

        //T shape
        shapes[1] = new Shape(new int[][]{
                {1, 1, 1}, {0, 1, 0}
        }, this, colors[1]);

        //L shape
        shapes[2] = new Shape(new int[][]{
                {1, 1, 1}, {0, 0, 1}
        }, this, colors[2]);

        //Z shape
        shapes[3] = new Shape(new int[][]{
                {1, 1, 0}, {0, 1, 1}
        }, this, colors[3]);

        //S shape
        shapes[4] = new Shape(new int[][]{
                {0, 1, 1}, {1, 1, 0}
        }, this, colors[4]);

        //J shape
        shapes[5] = new Shape(new int[][]{
                {1, 1, 1}, {1, 0, 0}
        }, this, colors[5]);

        //O shape
        shapes[6] = new Shape(new int[][]{
                {1, 1}, {1, 1}
        }, this, colors[6]);

        currentShape = shapes[random.nextInt(shapes.length)];

        looper = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        looper.start();
    }

    public void setCurrentShape(){
        currentShape = shapes[random.nextInt(shapes.length)];
        currentShape.reset();
        gameOver();
    }

    private void gameOver(){
        int[][] coords = currentShape.getCoords();
        for(int row = 0;row<coords.length;row++){
            for(int col = 0;col<coords[row].length;col++){
                if(coords[row][col]!=0){
                    if(board[row+currentShape.getY()][col+currentShape.getX()] != null){
                        state = GAME_OVER;
                        score = 0;
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        currentShape.render(g);

        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board[row].length; col++){
                if(board[row][col] != null){
                    g.setColor(board[row][col]);
                    g.fillRect(BLOCK_SIZE * col, BLOCK_SIZE * row, BLOCK_SIZE , BLOCK_SIZE);
                }

            }


        g.setColor(Color.white);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        }
        for (int col = 0; col <= BOARD_WIDTH; col++) {
            g.drawLine(BLOCK_SIZE * col, 0, BLOCK_SIZE * col, BLOCK_SIZE * BOARD_HEIGHT);
        }

        if(state == GAME_OVER){
            g.setColor(Color.white);
            g.setFont(getFont().deriveFont(30.0f));
            g.drawString("GAME OVER!",50,WindowGame.HEIGHT/2);
        }
        if(state == GAME_PAUSE){
            g.setColor(Color.white);
            g.setFont(getFont().deriveFont(20.0f));
            g.drawString("PRESS SPACE TO RESUME!",25,WindowGame.HEIGHT/2);
        }

        g.setColor(Color.white);
        g.setFont(getFont().deriveFont(20.0f));
        g.drawString("SCORE",310,290);
        g.drawString(""+score,310,310);
    }

    public void checkLine(){
        int bottomLine = board.length - 1;
        for(int i = board.length - 1;i>0;i--){
            int count = 0;
            for(int j = 0;j<board[0].length;j++){
                if(board[i][j]!=null){
                    count++;
                }
                board[bottomLine][j] = board[i][j];
            }
            if(count<board[0].length){
                bottomLine--;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentShape.goFast();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentShape.goRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentShape.goLeft();
        }else if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentShape.rotateShape();
        }

        if(state == GAME_OVER){
            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                for (int row = 0; row < board.length; row++){
                    for (int col = 0; col < board[row].length; col++){
                        board[row][col] = null;
                    }
                }
                setCurrentShape();
                state = GAME_PLAY;
            }
        }
        if(state == GAME_PLAY){
            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                state = GAME_PAUSE;
            }
        }else if(state == GAME_PAUSE){
            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                state = GAME_PLAY;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentShape.goNormal();
        }
    }
    public void setNextShape() {
        int index = random.nextInt(shapes.length);
        int colorIndex = random.nextInt(colors.length);
        nextShape = new Shape(shapes[index].getCoords(), this, colors[colorIndex]);
    }

//    public void setCurrentShape() {
//        currentShape = nextShape;
//        setNextShape();
//
//        for (int row = 0; row < currentShape.getCoords().length; row++) {
//            for (int col = 0; col < currentShape.getCoords()[0].length; col++) {
//                if (currentShape.getCoords()[row][col] != 0) {
//                    if (board[currentShape.getY() + row][currentShape.getX() + col] != null) {
//                        currentShape.reset();
//                        gameOver();
//                    }
//                }
//            }
//        }
//
//    }

    public Color[][] getBoard(){
        return board;
    }


}

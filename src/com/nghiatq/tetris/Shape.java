package com.nghiatq.tetris;

import java.awt.*;

public class Shape {
    private int x = 3, y = 0;
    private int normalSpeed = 600;
    private int fastSpeed = 50;

    private int delayTimeMovement = normalSpeed;
    private long beginTime = 0;

    private int deltaX = 0;

    private boolean boardBottom = false;

    private int[][] coords;

    private Board board;

    private Color color;

    public Shape(int[][] coords,Board board, Color color){
        this.coords = coords;
        this.board = board;
        this.color = color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int[][] getCoords() {
        return coords;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void reset(){
        this.x = 3;
        this.y = 0;
        boardBottom = false;
    }

    boolean point = false;
    public void update(){
        if(boardBottom){
            for (int row = 0; row < coords.length; row++){
                for (int col = 0; col < coords[0].length; col++) {
                    if(coords[row][col]!=0){
                        board.getBoard()[y+row][x+col] = color;
                    }
                }
            }
            board.checkLine();
            board.setCurrentShape();
            return;
        }

        boolean moveX = true;
        if(!(x+deltaX+coords[0].length>10)&&!(x+deltaX<0)){
            for (int row = 0; row < coords.length; row++)
                for (int col = 0; col < coords[row].length; col++){
                    if(coords[row][col]!=0){
                        if (board.getBoard()[y+row][x+col+deltaX] != null){
                            moveX = false;
                        }
                    }
                }
            if(moveX){
                x+=deltaX;
            }
        }
        deltaX = 0;
        if (System.currentTimeMillis() - beginTime > delayTimeMovement) {
            if(!(y+1+coords.length>Board.BOARD_HEIGHT)){
                for (int row = 0; row < coords.length; row++)
                    for (int col = 0; col < coords[row].length; col++){
                        if(coords[row][col]!=0){
                            if (board.getBoard()[y+row+1][x+col+deltaX] != null){
                                boardBottom = true;
                            }
                        }
                    }
                if(!boardBottom){
                    y++;
                }
            } else {
                boardBottom = true;
            }
            beginTime = System.currentTimeMillis();
        }
    }

    public void rotateShape(){
        int[][] rotateShape = transMatrix(coords);
        reverseRow(rotateShape);

        if(x+rotateShape[0].length>Board.BOARD_WIDTH){
            x =x-2;
        }
        if(x+rotateShape[0].length<0){
            x =x+2;
        }

        if(y+rotateShape.length>20){
            return;
        }

        for (int row = 0;row<rotateShape.length;row++)
            for (int col = 0;col<rotateShape[row].length;col++){
                if(board.getBoard()[y+row][x+col] != null){
                    return;
                }
            }
        coords = rotateShape;
    }

    private int[][] transMatrix(int[][] matrix){
        int[][] temp = new int[matrix[0].length][matrix.length];
        for (int row = 0;row<matrix.length;row++)
            for (int col = 0;col<matrix[row].length;col++){
                temp[col][row] = matrix[row][col];
            }
        return temp;
    }

    private void reverseRow(int[][] matrix){
        int mid = matrix.length / 2;
        for(int row = 0;row<mid;row++){
            int[] temp = matrix[row];
            matrix[row] = matrix[matrix.length - row - 1];
            matrix[matrix.length - row - 1] = temp;
        }
    }

    public void render(Graphics g){
        for (int row = 0; row < coords.length; row++)
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    g.setColor(color);
                    g.fillRect(col * Board.BLOCK_SIZE + x * Board.BLOCK_SIZE, row * Board.BLOCK_SIZE + y * Board.BLOCK_SIZE,
                            Board.BLOCK_SIZE, Board.BLOCK_SIZE);
                }
            }
    }

    public void goFast(){
        delayTimeMovement = fastSpeed;
    }

    public void goNormal(){
        delayTimeMovement = normalSpeed;
    }

    public void goLeft(){
        deltaX = -1;
    }

    public void goRight(){
        deltaX = 1;
    }


}

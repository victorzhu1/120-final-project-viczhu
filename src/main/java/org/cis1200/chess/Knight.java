package org.cis1200.chess;

public class Knight implements Piece{

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    // Constructor for a king piece
    public Knight(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void moveTo(int endX, int endY) {
        this.x = endX;
        this.y = endY;
    }

    @Override
    public String getType() {
        return "Knight";
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public int[] getPosition() {
        return new int[] {x, y};
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // Cannot stay in same position
        if (dx == 0 && dy == 0) {
            return false;
        }


        // Can jump in L shape (2 up/down, 1 left/right, and vice versa)
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            return true;
        }

        // Everything else is invalid
        return false;
    }
}

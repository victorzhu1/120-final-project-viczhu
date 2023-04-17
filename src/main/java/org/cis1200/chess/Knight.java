package org.cis1200.chess;

public class Knight implements Piece{

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    // Constructor for a Knight piece
    public Knight(int y, int x, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void moveTo(int endY, int endX) {
        this.y = endY;
        this.x = endX;
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
        return new int[] {y, x};
    }

    @Override
    public boolean isValidMove(int startY, int startX, int endY, int endX, Chess board) {
        Piece target = board.getCell(endY, endX);

        int dy = Math.abs(endY - startY);
        int dx = Math.abs(endX - startX);

        // Cannot stay in same position
        if (dx == 0 && dy == 0) {
            return false;
        }


        // Can jump in L shape (2 up/down, 1 left/right, and vice versa)
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            if (target == null) {
                return true;
            }
            if (target.getColor().equals(this.getColor())) {
                return false;
            }
            System.out.println("Capture!");
            return true;
        }

        // Everything else is invalid
        return false;
    }

    public String toString() {
        return "K";
    }
}

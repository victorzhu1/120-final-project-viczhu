package org.cis1200.chess;

public class King implements Piece {

    // Current position of piece
    private int x;
    private int y;

    private boolean canCastle;
    // Color of piece
    private String color;

    // Constructor for a King piece
    public King(int y, int x, String color) {
        this.y = y;
        this.x = x;
        this.color = color;
        this.canCastle = false;
    }

    @Override
    public void moveTo(int endY, int endX) {
        this.y = endY;
        this.x = endX;
    }

    @Override
    public String getType() {
        return "King";
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public int[] getPosition() {
        return new int[] { y, x };
    }

    public boolean getCanCastle() {
        return canCastle;
    }
    public void setCanCastle(boolean b) {
        canCastle = b;
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

        // Can move one square up, down, left, or right
        if (dx <= 1 && dy <= 1) {
            if (target == null) {
                return true;
            }
            if (target.getColor().equals(color)) {
                return false;
            }
            return true;
        }

        // Everything else invalid
        return false;
    }

    public String toString() {
        return "K";
    }
}

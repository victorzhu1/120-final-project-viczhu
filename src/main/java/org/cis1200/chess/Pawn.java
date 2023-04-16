package org.cis1200.chess;

public class Pawn implements Piece{

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    // Constructor for a king piece
    public Pawn(int x, int y, String color) {
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
        return "Queen";
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
        int dy = endY - startY;

        // Must move vertically
        if (dy == 0) {
            return false;
        }

        // If white pawn:
        if (color.equals("White")) {
            // Must move upward
            if (dy < 0) {
                return false;
            }

            // dx must move either 1 or 0
            if (dy == 1) {
                return (dx == 1 || dx == 0);
            }
        }

        // If black pawn
        if (color.equals("Black")) {
            // Must move downward
            if (dy > 0) {
                return false;
            }

            // dx must move either 1 or 0
            if (dy == -1) {
                return (dx == 1 || dx == 0);
            }
        }

        // Everything else is invalid
        return false;
    }
}

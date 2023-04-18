package org.cis1200.chess;

public class Bishop implements Piece{

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    // Constructor for a Bishop piece
    public Bishop(int y, int x, String color) {
        this.y = y;
        this.x = x;
        this.color = color;
    }

    @Override
    public void moveTo(int endY, int endX) {
        this.y = endY;
        this.x = endX;
    }

    @Override
    public String getType() {
        return "Bishop";
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

        boolean blocked = false;

        int dy = Math.abs(endY - startY);
        int dx = Math.abs(endX - startX);

        // Cannot stay in same position
        if (dx == 0 && dy == 0) {
            return false;
        }

        if (dx != dy) {
            return false;
        }

        if (dx == dy) {
            for (int i = 1; i < dx; i++) {
                if (endY < startY) {
                    if (endX < startX) {
                        if (board.getCell(startY - i, startX - i) != null) {
                            blocked = true;
                        }
                    } else {
                        if (board.getCell(startY - i, startX + i) != null) {
                            blocked = true;
                        }
                    }
                } else {
                    if (endX < startX) {
                        if (board.getCell(startY + i, startX - i) != null) {
                            blocked = true;
                        }
                    } else {
                        if (board.getCell(startY + i, startX + i) != null) {
                            blocked = true;
                        }
                    }
                }
            }
        }

        if (!blocked) {
            if (target == null) {
                return true;
            }
            if (target.getColor().equals(color)) {
                return false;
            }
            return true;
        }



        // Everything else is invalid
        return false;
    }

    public String toString() {
        return "B";
    }
}

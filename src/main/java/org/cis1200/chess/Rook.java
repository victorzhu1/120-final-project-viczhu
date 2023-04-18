package org.cis1200.chess;

public class Rook implements Piece{

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    // Constructor for a Rook piece
    public Rook(int y, int x, String color) {
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
        return "Rook";
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

        if (!(dx == 0 || dy == 0)) {
            return false;
        }

        if (dx == 0) {
            if (endY < startY) {
                for (int i = startY - 1; i > endY; i--) {
                    if (board.getCell(i, startX) != null) {
                        blocked = true;
                    }
                }
            } else {
                for (int i = startY + 1; i < endY; i++) {
                    if (board.getCell(i, startX) != null) {
                        blocked = true;
                    }
                }
            }
        }

        if (dy == 0) {
            if (endX < startX) {
                for (int i = startX - 1; i > endX; i--) {
                    if (board.getCell(startY, i) != null) {
                        blocked = true;
                    }
                }
            } else {
                for (int i = startX + 1; i < endX; i++) {
                    if (board.getCell(startY, i) != null) {
                        blocked = true;
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
            System.out.println("Capture!");
            return true;
        } else {
            System.out.println("rook blocked");
        }

        // Everything else is invalid
        return false;
    }

    public String toString() {
        return "R";
    }
}

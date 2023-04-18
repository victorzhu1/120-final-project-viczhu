package org.cis1200.chess;

public class Pawn implements Piece{

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    private boolean firstMove;

    // Constructor for a Pawn piece
    public Pawn(int y, int x, String color) {
        this.y = y;
        this.x = x;
        this.color = color;
        firstMove = true;
    }

    @Override
    public void moveTo(int endY, int endX) {
        this.y = endY;
        this.x = endX;
    }

    @Override
    public String getType() {
        return "Pawn";
    }

    @Override
    public String getColor() {
        return color;
    }

    public boolean getFirstMoveStatus() {
        return firstMove;
    }

    public void setFirstMoveStatus(boolean status) {
        firstMove = status;
    }

    @Override
    public int[] getPosition() {
        return new int[] {y, x};
    }


    @Override
    public boolean isValidMove(int startY, int startX, int endY, int endX, Chess board) {
        Piece target = board.getCell(endY, endX);
        int dy = endY - startY;
        int dx = Math.abs(endX - startX);

        // Must move vertically
        if (dy == 0) {
            return false;
        }

        // If white pawn:
        if (color.equals("White")) {
            // Must move upward
            if (dy > 0) {
                return false;
            }

            // Can move two squares first move
            if (firstMove) {
                if (dy == -1 || dy == -2) {
                    if (dx == 0) {
                        if (target == null) {
                            return true;
                        }
                    }
                    if (dy == -1 && target != null) {
                        if (!target.getColor().equals(color)) {
                            if (dx == 1) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                // Normal move, dx must move up either 1 or 0
                if (dy == -1) {
                    if (target != null) {
                        if (!target.getColor().equals(color)) {
                            if (dx == 1) {
                                return true;
                            }
                        }
                    }
                    if (dx == 0) {
                        if (target == null) {
                            return true;
                        }
                    }
                }
            }
        }

        // If black pawn
        if (color.equals("Black")) {
            // Must move downward
            if (dy < 0) {
                return false;
            }
            if (firstMove) {
                if (dy == 1 || dy == 2) {
                    if (dx == 0) {
                        if (target == null) {
                            return true;
                        }
                    }
                    if (dy == 1 && target != null) {
                        if (!target.getColor().equals(color)) {
                            if (dx == 1) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                // Normal move, dx must move down either 1 or 0
                if (dy == 1) {
                    if (target != null) {
                        if (!target.getColor().equals(color)) {
                            if (dx == 1) {
                                return true;
                            }
                        }
                    }
                    if (dx == 0) {
                        if (target == null) {
                            return true;
                        }
                    }
                }
            }
        }

        // Everything else is invalid
        return false;
    }

    public String toString() {
        return "P";
    }
}

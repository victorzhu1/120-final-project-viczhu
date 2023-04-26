package org.cis1200.chess;

public class Pawn implements Piece {

    // Current position of piece
    private int x;
    private int y;

    // Color of piece
    private String color;

    private boolean firstMove;

    private boolean justMovedTwoSquares;

    // Constructor for a Pawn piece
    public Pawn(int y, int x, String color) {
        this.y = y;
        this.x = x;
        this.color = color;
        firstMove = true;
        justMovedTwoSquares = false;
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

    public boolean getJustMovedTwoStatus() {
        return justMovedTwoSquares;
    }

    public void setJustMovedTwoStatus(boolean status) {
        justMovedTwoSquares = status;
    }

    @Override
    public int[] getPosition() {
        return new int[] { y, x };
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
                    if (board.getCell(startY - 1, startX) != null && dx == 0) {
                        return false;
                    }
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

                // En passant capture
                if (startY == 3 && endY == 2 && Math.abs(dx) == 1) {
                    boolean onLeft = false;
                    Piece left = null;
                    if (startX > 0) {
                        left = board.getCell(startY, startX - 1);
                    }
                    Piece right = null;
                    if (startX < 7) {
                        right = board.getCell(startY, startX + 1);
                    }
                    Piece enemyPawn = null;
                    if (left instanceof Pawn && !left.getColor().equals(color) &&
                            ((Pawn) left).getJustMovedTwoStatus()) {
                        onLeft = true;
                        enemyPawn = left;
                    } else if (right instanceof Pawn && !right.getColor().equals(color) &&
                            ((Pawn) right).getJustMovedTwoStatus()) {
                        enemyPawn = right;
                    }
                    if (enemyPawn != null) {
                        if (((Pawn) enemyPawn).getJustMovedTwoStatus()) {

                            // Return false for other direction of the en passant, unless there is
                            // capture
                            if (!onLeft && (endX - startX) == -1) {
                                if (board.getCell(startY - 1, startX - 1) != null) {
                                    return true;
                                }
                                return false;
                            }
                            if (onLeft && (endX - startX) == 1) {
                                if (board.getCell(startY - 1, startX + 1) != null) {
                                    return true;
                                }
                                return false;
                            }

                            // Successful en passant
                            return true;
                        }
                    }
                }

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
                    if (board.getCell(startY + 1, startX) != null && dx == 0) {
                        return false;
                    }
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

                // En passant capture
                if (startY == 4 && endY == 5 && Math.abs(dx) == 1) {
                    boolean onLeft = false;
                    Piece left = null;
                    if (startX > 0) {
                        left = board.getCell(startY, startX - 1);
                    }
                    Piece right = null;
                    if (startX < 7) {
                        right = board.getCell(startY, startX + 1);
                    }
                    Piece enemyPawn = null;
                    if (left instanceof Pawn && !left.getColor().equals(color) &&
                            ((Pawn) left).getJustMovedTwoStatus()) {
                        onLeft = true;
                        enemyPawn = left;
                    } else if (right instanceof Pawn && !right.getColor().equals(color) &&
                            ((Pawn) right).getJustMovedTwoStatus()) {
                        enemyPawn = right;
                    }
                    if (enemyPawn != null) {
                        if (((Pawn) enemyPawn).getJustMovedTwoStatus()) {

                            // Return false for capturing the other direction of the en passant
                            if (!onLeft && (endX - startX) == -1) {
                                return false;
                            }
                            if (onLeft && (endX - startX) == 1) {
                                return false;
                            }

                            // Successful en passant
                            return true;
                        }
                    }
                }

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

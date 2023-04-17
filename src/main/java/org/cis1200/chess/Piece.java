package org.cis1200.chess;

public interface Piece {

    void moveTo(int endY, int endX);

    String getType();

    String getColor();

    int[] getPosition();

    boolean isValidMove(int startX, int startY, int endX, int endY, Chess board);

}

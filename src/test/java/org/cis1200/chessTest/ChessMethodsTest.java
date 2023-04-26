package org.cis1200.chessTest;

import org.cis1200.chess.Chess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

public class ChessMethodsTest {


    @Test
    public void testCountValidMoves() {
        Chess t = new Chess();
        int expected = 2;
        int actual = t.countPieceValidMoves(7, 1);
        assertEquals(expected, actual);
        t.move(7, 1, 5, 2);
        expected = 5;
        actual = t.countPieceValidMoves(5, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidCastle() {
        Chess t = new Chess();
        t.move(7, 6, 5, 5);
        t.move(6, 4, 5, 4);
        t.move(7, 5, 4, 2);
        boolean expected = true;
        boolean actual = t.move(7, 4, 7, 6);
        assertEquals(expected, actual);
    }

    @Test
    public void testLoseCastleRights() {
        Chess t = new Chess();
        t.move(7, 6, 5, 5);
        t.move(6, 4, 5, 4);
        t.move(7, 5, 4, 2);
        t.move(7, 4, 7, 5);
        t.move(7, 5, 7, 4);
        boolean expected = false;
        boolean actual = t.move(7, 4, 7, 6);
        assertEquals(expected, actual);
    }

    @Test
    public void testMoveWouldPutOwnKingInCheck() {
        Chess t = new Chess();
        t.move(1, 2, 3, 2);
        t.move(0, 3, 3, 0);
        boolean expected = false;
        boolean actual = t.move(6, 3, 5, 3);
        assertEquals(expected, actual);
    }

    @Test
    public void testKingInCheck() {
        Chess t = new Chess();
        t.move(1, 2, 3, 2);
        t.move(6, 3, 5, 3);
        t.move(0, 3, 3, 0);
        boolean expected = true;
        boolean actual = t.kingInCheck("White");
        assertEquals(expected, actual);
        assertEquals(false, t.kingInCheck("Black"));
    }

    @Test
    public void testKingInCheckmate() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        t.move(1, 4, 3, 4);
        t.move(7, 3, 3, 7);
        t.move(1, 0, 2, 0);
        t.move(7, 5, 4, 2);
        t.move(0, 6, 2, 5);
        t.move(3, 7, 1, 5);
        boolean expected = true;
        boolean actual = t.kingInCheckmate("Black");
        assertEquals(expected, actual);
        assertEquals(false, t.kingInCheckmate("White"));
    }

    @Test
    public void testCheckWinnerOngoing() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        t.move(1, 4, 3, 4);
        t.move(7, 3, 3, 7);
        t.move(1, 0, 2, 0);
        t.move(7, 5, 4, 2);
        int expected = 0;
        int actual = t.checkWinner();
        assertEquals(expected, actual);
    }
    @Test
    public void testCheckWinnerWhiteWon() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        t.move(1, 4, 3, 4);
        t.move(7, 3, 3, 7);
        t.move(1, 0, 2, 0);
        t.move(7, 5, 4, 2);
        t.move(0, 6, 2, 5);
        t.move(3, 7, 1, 5);
        int expected = 1;
        int actual = t.checkWinner();
        assertEquals(expected, actual);
    }


}

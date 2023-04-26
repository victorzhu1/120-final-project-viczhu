package org.cis1200.chessTest;

import org.cis1200.chess.Chess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

public class MoveValidityTest {

    // Test movement validity
    @Test
    public void testValidPawnMove() {
        Chess t = new Chess();
        boolean expected = true;
        boolean actual = t.move(6, 0, 5, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidPawnDoubleMove() {
        Chess t = new Chess();
        boolean expected = true;
        boolean actual = t.move(6, 0, 4, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidPawnMove() {
        Chess t = new Chess();
        boolean expected = false;
        boolean actual = t.move(6, 0, 6, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidKnightMove() {
        Chess t = new Chess();
        boolean expected = true;
        boolean actual = t.move(7, 1, 5, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidKnightMove() {
        Chess t = new Chess();
        boolean expected = false;
        boolean actual = t.move(7, 1, 7, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidBishopMove() {
        Chess t = new Chess();
        t.move(6, 3, 4, 3);
        boolean expected = true;
        boolean actual = t.move(7, 2, 3, 6);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidBishopMove() {
        Chess t = new Chess();
        t.move(6, 3, 4, 3);
        boolean expected = false;
        boolean actual = t.move(7, 2, 2, 6);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidBishopMoveThroughPiece() {
        Chess t = new Chess();
        t.move(6, 3, 4, 3);
        boolean expected = false;
        boolean actual = t.move(7, 2, 5, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidRookMove() {
        Chess t = new Chess();
        t.move(6, 0, 4, 0);
        boolean expected = true;
        boolean actual = t.move(7, 0, 5, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidRookMove() {
        Chess t = new Chess();
        t.move(6, 0, 4, 0);
        boolean expected = false;
        boolean actual = t.move(7, 0, 5, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidRookMoveThroughPiece() {
        Chess t = new Chess();
        t.move(6, 0, 4, 0);
        boolean expected = false;
        boolean actual = t.move(7, 0, 3, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidQueenMove() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        boolean expected = true;
        boolean actual = t.move(7, 3, 4, 6);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidQueenMove() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        boolean expected = false;
        boolean actual = t.move(7, 3, 4, 5);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidQueenMoveThroughPiece() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        boolean expected = false;
        boolean actual = t.move(7, 3, 3, 3);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidKingMove() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        boolean expected = true;
        boolean actual = t.move(7, 4, 6, 4);
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidKingMove() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        boolean expected = false;
        boolean actual = t.move(7, 4, 7, 5);
        assertEquals(expected, actual);
    }

    // Test capturing validities
    @Test
    public void testValidPawnCapture() {
        Chess t = new Chess();
        t.move(6, 0, 4, 0);
        t.move(1, 1, 3, 1);
        boolean expected = true;
        boolean actual = t.move(4, 0, 3, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidEnPassantPawnCapture() {
        Chess t = new Chess();
        t.move(6, 0, 4, 0);
        t.move(1, 2, 2, 2);
        t.move(4, 0, 5, 0);
        t.move(1, 1, 3, 1);
        t.move(5, 0, 4, 1);
        boolean expected = true;
        boolean actual = t.move(4, 0, 3, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidKnightCapture() {
        Chess t = new Chess();
        t.move(7, 1, 5, 2);
        t.move(1, 3, 3, 3);
        boolean expected = true;
        boolean actual = t.move(5, 2, 3, 3);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidBishopCapture() {
        Chess t = new Chess();
        t.move(6, 3, 4, 3);
        t.move(1, 6, 3, 6);
        boolean expected = true;
        boolean actual = t.move(7, 2, 3, 6);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidRookCapture() {
        Chess t = new Chess();
        t.move(6, 0, 4, 0);
        t.move(1, 1, 3, 1);
        t.move(6, 4, 5, 4);
        t.move(3, 1, 4, 0);
        boolean expected = true;
        boolean actual = t.move(7, 0, 4, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidQueenCapture() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        t.move(1, 7, 3, 7);
        boolean expected = true;
        boolean actual = t.move(7, 3, 3, 7);
        assertEquals(expected, actual);
    }

    @Test
    public void testValidKingCapture() {
        Chess t = new Chess();
        t.move(6, 4, 4, 4);
        t.move(1, 3, 3, 3);
        t.move(6, 0, 5, 0);
        t.move(0, 2, 4, 6);
        t.move(5, 0, 4, 0);
        t.move(4, 6, 6, 4);
        boolean expected = true;
        boolean actual = t.move(7, 4, 6, 4);
        assertEquals(expected, actual);
    }

}

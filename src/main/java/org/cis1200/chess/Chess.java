package org.cis1200.chess;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a model for a chess game.
 */
public class Chess {

    private Piece[][] board;

    private boolean whiteToMove;

    private boolean gameOver;

    private boolean inStalemate;

    private boolean cpuMode;

    // Keep track of castling rights
    private boolean whiteCanKingSideCastle;
    private boolean whiteCanQueenSideCastle;
    private boolean blackCanKingSideCastle;
    private boolean blackCanQueenSideCastle;

    /**
     * Constructor sets up game state.
     */
    public Chess() {
        reset();
    }

    /**
     * Makes a move for white or black. Returns whether the move was possible.
     */

    public boolean move(int startY, int startX, int endY, int endX) {

        // Get piece that's moving
        Piece piece = board[startY][startX];
        Piece target = board[endY][endX];

        // If move is a castling move
        if (isValidCastle(startY, startX, endY, endX)) {
            // Make king movement
            board[startY][startX] = null;
            board[endY][endX] = piece;
            piece.moveTo(endY, endX);
            ((King) piece).setCanCastle(false);

            // Make rook movement
            Piece rook;
            if (endY == 0 && endX == 2) {
                rook = board[0][0];
                board[0][0] = null;
                board[0][3] = rook;
                rook.moveTo(0, 3);
                blackCanQueenSideCastle = false;
            } else if (endY == 0 && endX == 6) {
                rook = board[0][7];
                board[0][7] = null;
                board[0][5] = rook;
                rook.moveTo(0, 5);
                blackCanKingSideCastle = false;
            } else if (endY == 7 && endX == 2) {
                rook = board[7][0];
                board[7][0] = null;
                board[7][3] = rook;
                rook.moveTo(7, 3);
                whiteCanQueenSideCastle = false;
            } else {
                rook = board[7][7];
                board[7][7] = null;
                board[7][5] = rook;
                rook.moveTo(7, 5);
                whiteCanKingSideCastle = false;
            }
            return true;
        }

        // Catch false non-castling moves
        if (piece == null) {
            return false;
        }
        if (!piece.isValidMove(startY, startX, endY, endX, this)) {
            return false;
        }
        if (moveWouldPutOwnKingInCheck(startY, startX, endY, endX)) {
            return false;
        }

        // Make movement
        board[startY][startX] = null;
        board[endY][endX] = piece;

        // If successful move was a pawn's
        if (piece instanceof Pawn) {

            Pawn thisPawn = (Pawn) piece;

            // If first move
            if (thisPawn.getFirstMoveStatus()) {

                // Moved two squares first
                if (Math.abs(endY - startY) == 2) {

                    thisPawn.setJustMovedTwoStatus(true);
                    piece.moveTo(endY, endX);

                    // First two square move should also reset other pawns
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {

                            // Don't reset this pawn
                            if (i == endY && j == endX) {
                                continue;
                            }
                            Piece anyPawn = this.getCell(i, j);
                            if (anyPawn instanceof Pawn && anyPawn != null) {
                                ((Pawn) anyPawn).setJustMovedTwoStatus(false);
                            }
                        }
                    }
                    thisPawn.setFirstMoveStatus(false);
                    return true;
                } else {

                    // Moved one square first
                    thisPawn.setFirstMoveStatus(false);
                }
            } else {

                // Pawn promotion in player vs player
                if ((!cpuMode && (endY == 0 || endY == 7)) || (cpuMode && endY == 0)) {
                    // Create a dialog box with options for piece promotion
                    String[] options = { "Queen", "Rook", "Bishop", "Knight" };
                    int choice = JOptionPane.showOptionDialog(
                            null, "Choose piece:", "Promote pawn",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                            null
                    );

                    // Create the new piece based on user's choice
                    Piece newPiece = null;
                    switch (choice) {
                        case 0: // Queen
                            newPiece = new Queen(endY, endX, piece.getColor());
                            break;
                        case 1: // Rook
                            newPiece = new Rook(endY, endX, piece.getColor());
                            break;
                        case 2: // Bishop
                            newPiece = new Bishop(endY, endX, piece.getColor());
                            break;
                        case 3: // Knight
                            newPiece = new Knight(endY, endX, piece.getColor());
                            break;
                    }
                    board[endY][endX] = newPiece;
                } else if (cpuMode && endY == 7) {

                    // Black promotes to queen
                    board[endY][endX] = new Queen(endY, endX, "Black");
                }

                // Deal with en passant
                if (target == null && Math.abs(endX - startX) == 1) {
                    // Check for adjacent pawn of opposite color
                    Piece left = null;
                    if (startX > 0) {
                        left = board[startY][startX - 1];
                    }
                    Piece right = null;
                    if (startX < 7) {
                        right = board[startY][startX + 1];
                    }
                    Piece enemyPawn = null;
                    if (left instanceof Pawn && !left.getColor().equals(thisPawn.getColor()) &&
                            ((Pawn) left).getJustMovedTwoStatus()) {
                        enemyPawn = left;
                    } else if (right instanceof Pawn
                            && !right.getColor().equals(thisPawn.getColor()) &&
                            ((Pawn) right).getJustMovedTwoStatus()) {
                        enemyPawn = right;
                    }
                    if (enemyPawn != null) {
                        if (((Pawn) enemyPawn).getJustMovedTwoStatus()) {
                            int x = enemyPawn.getPosition()[0];
                            int y = enemyPawn.getPosition()[1];
                            this.setCell(x, y, null);
                        }
                        piece.moveTo(endY, endX);
                        return true;
                    }
                }
            }

        }

        // Remove castling rights if King or Rook moves normally
        if (piece instanceof King) {
            ((King) piece).setCanCastle(false);
            if (whiteToMove) {
                whiteCanKingSideCastle = false;
                whiteCanQueenSideCastle = false;
            } else {
                blackCanKingSideCastle = false;
                blackCanQueenSideCastle = false;
            }
        }

        if (piece instanceof Rook) {
            Piece king = this.getCell(
                    findKingPosition(piece.getColor())[0], findKingPosition(piece.getColor())[1]
            );
            ((King) king).setCanCastle(false);
            // Black made first Rook move
            if (piece.getPosition()[0] == 0) {
                if (piece.getPosition()[1] == 0) {
                    blackCanQueenSideCastle = false;
                } else {
                    blackCanKingSideCastle = false;
                }
                // White made first Rook move
            } else {
                if (piece.getPosition()[1] == 0) {
                    whiteCanQueenSideCastle = false;
                } else {
                    whiteCanKingSideCastle = false;
                }
            }
        }

        // Valid generic move made, set all pawns' justMovedTwo to false
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece anyPawn = this.getCell(i, j);
                if (anyPawn instanceof Pawn && anyPawn != null) {
                    ((Pawn) anyPawn).setJustMovedTwoStatus(false);
                }
            }
        }

        // Track successful piece movement
        piece.moveTo(endY, endX);
        return true;
    }

    /**
     * Makes a random move for white or black.
     */
    public void randomMove(String color) {
        // Get list of all valid moves
        List<int[]> validMoves = generateMoves(color);

        if (validMoves.size() == 0 && kingInCheckmate("Black")) {
            gameOver = true;
            return;
        } else if (validMoves.size() == 0 && !kingInCheck("Black")) {
            inStalemate = true;
            return;
        }

        // Choose a random move
        int[] move = validMoves.get((int) (Math.random() * validMoves.size()));

        // Make move
        this.move(move[0], move[1], move[2], move[3]);
    }

    /**
     * Makes black's best move, given depth (higher depth, more calculated move)
     */
    public void makeBestMove(int depth) {

        int bestValue = Integer.MIN_VALUE;
        List<int[]> bestMoves = new ArrayList<>();
        List<int[]> validMoves = generateMoves("Black");
        for (int[] move : validMoves) {
            Piece piece = this.getCell(move[0], move[1]);
            Piece target = this.getCell(move[2], move[3]);

            // Move piece
            board[move[0]][move[1]] = null;
            board[move[2]][move[3]] = piece;

            // Find value
            int value = minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            // Move piece back
            board[move[0]][move[1]] = piece;
            board[move[2]][move[3]] = target;

            if (value > bestValue) {
                bestValue = value;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (value == bestValue) {
                bestMoves.add(move);
            }
        }

        // No bestMove, white won
        if (bestMoves.size() == 0 && kingInCheckmate("Black")) {
            gameOver = true;
        } else if (bestMoves.size() == 0 && !kingInCheck("Black")) {
            inStalemate = true;
        } else {
            // If multiple best moves, pick a random one
            int[] bestMove = bestMoves.get((int) (Math.random() * bestMoves.size()));

            // Make the best move
            this.move(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
        }
    }

    /**
     * Minimax method using recursive backtracking to find value of best move for
     * black
     */
    public int minimax(int depth, int alpha, int beta, boolean blackPlaying) {

        // Deal with winning scenarios, and 0 depth (just one evaluation) base cases
        int winner = checkWinner();
        if (winner != 0 || depth == 0) {
            if (winner == 2) {
                return Integer.MAX_VALUE - 1;
            }
            if (winner == 1) {
                return Integer.MIN_VALUE + 1;
            } else {
                return totalPointDiff();
            }
        }

        // Recursive method of finding either white or black's best total point
        // difference
        int bestValue;
        if (blackPlaying) {
            bestValue = Integer.MIN_VALUE;
            List<int[]> validMoves = generateMoves("Black");
            for (int[] move : validMoves) {
                Piece piece = this.getCell(move[0], move[1]);
                Piece target = this.getCell(move[2], move[3]);

                // Move piece
                board[move[0]][move[1]] = null;
                board[move[2]][move[3]] = piece;

                // Find value
                int value = minimax(depth - 1, alpha, beta, false);

                // Move piece back
                board[move[0]][move[1]] = piece;
                board[move[2]][move[3]] = target;

                bestValue = Math.max(bestValue, value);
                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestValue = Integer.MAX_VALUE;
            List<int[]> validMoves = generateMoves("White");
            for (int[] move : validMoves) {
                Piece piece = this.getCell(move[0], move[1]);
                Piece target = this.getCell(move[2], move[3]);

                // Move piece
                board[move[0]][move[1]] = null;
                board[move[2]][move[3]] = piece;

                // Find value
                int value = minimax(depth - 1, alpha, beta, true);

                // Move piece back
                board[move[0]][move[1]] = piece;
                board[move[2]][move[3]] = target;
                bestValue = Math.min(bestValue, value);
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestValue;
    }

    /**
     * Returns total point diff of black - white (playing as black)
     * Material points are multiplied by 100 to allow for small int bonuses
     */
    public int totalPointDiff() {
        int points = 0;
        int blackTotal = 0;
        int whiteTotal = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.getCell(i, j);
                int mobilityCount = countPieceValidMoves(i, j);
                if (piece != null) {
                    if (piece instanceof Pawn) {
                        points = 100;
                        points += mobilityCount;
                    }
                    if (piece instanceof Bishop) {
                        points = 300;
                        points += mobilityCount / 3;
                    }
                    if (piece instanceof Knight) {
                        points = 300;
                        points += mobilityCount / 3;
                    }
                    if (piece instanceof Rook) {
                        points = 500;
                        points += mobilityCount / 5;
                    }
                    if (piece instanceof Queen) {
                        points = 900;
                        points += mobilityCount / 9;
                    }
                    if (piece instanceof King) {
                        King king = (King) piece;
                        if (king.getCanCastle()) {
                            points += 100;
                        } else {
                            points -= 100;
                        }
                    }
                    if (piece.getColor().equals("White")) {
                        whiteTotal += points;
                    } else {
                        blackTotal += points;
                    }
                    points = 0;
                }
            }
        }

        return blackTotal - whiteTotal;
    }

    /**
     * Generates a list of valid moves (length four arrays, startY, startX, endY,
     * endX) for a given side.
     */
    public List<int[]> generateMoves(String color) {

        List<Piece> pieces = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.getCell(i, j);
                if (piece != null && piece.getColor().equals(color)) {
                    pieces.add(piece);
                }
            }
        }

        // Get list of all valid moves
        List<int[]> validMoves = new ArrayList<>();
        for (Piece piece : pieces) {
            int startY = piece.getPosition()[0];
            int startX = piece.getPosition()[1];
            for (int endY = 0; endY < 8; endY++) {
                for (int endX = 0; endX < 8; endX++) {
                    if (piece.isValidMove(startY, startX, endY, endX, this) &&
                            !moveWouldPutOwnKingInCheck(startY, startX, endY, endX)) {
                        validMoves.add(new int[] { startY, startX, endY, endX });
                        if (isValidCastle(startY, startX, endY, endX)) {
                        }
                    }
                }
            }
        }
        return validMoves;
    }

    public int countPieceValidMoves(int startY, int startX) {
        int count = 0;
        Piece piece = this.getCell(startY, startX);
        if (piece == null) {
            return 0;
        }
        for (int endY = 0; endY < 8; endY++) {
            for (int endX = 0; endX < 8; endX++) {
                if (piece.isValidMove(startY, startX, endY, endX, this) &&
                        !moveWouldPutOwnKingInCheck(startY, startX, endY, endX)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Checks if a move is a valid castling move.
     */
    public boolean isValidCastle(int startY, int startX, int endY, int endX) {
        String color = whiteToMove ? "White" : "Black";

        // Cannot castle when check
        if (kingInCheck(color)) {
            return false;
        }

        // White castle
        if (whiteToMove) {

            // Must stay on same rank
            if (endY != 7) {
                return false;
            }

            // Filter for correct King
            Piece king = board[startY][startX];
            if (!(king instanceof King && king.getColor().equals(color))) {
                return false;
            }

            if (endX == 2) {
                // Queen side castle
                if (!whiteCanQueenSideCastle) {
                    return false;
                }

                if (board[7][1] != null || board[7][2] != null || board[7][3] != null) {
                    return false;
                }
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        Piece piece = this.getCell(i, j);
                        if (piece != null && !piece.getColor().equals(color)) {
                            if (piece.isValidMove(i, j, 7, 2, this)) {
                                return false;
                            }
                            if (piece.isValidMove(i, j, 7, 3, this)) {
                                return false;
                            }
                        }
                    }
                }
                // King side castle
            } else if (endX == 6) {
                if (!whiteCanKingSideCastle) {
                    return false;
                }
                if (board[7][5] != null || board[7][6] != null) {
                    return false;
                }
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        Piece piece = this.getCell(i, j);
                        if (piece != null && !piece.getColor().equals(color)) {
                            if (piece.isValidMove(i, j, 7, 5, this)) {
                                return false;
                            }
                            if (piece.isValidMove(i, j, 7, 6, this)) {
                                return false;
                            }
                        }
                    }
                }
            } else {
                return false;
            }
            // Black castle
        } else {

            // Must stay on same rank
            if (endY != 0) {
                return false;
            }

            // Filter for correct King
            Piece king = board[startY][startX];
            if (!(king instanceof King && king.getColor().equals(color))) {
                return false;
            }

            // Queen side castle
            if (endX == 2) {
                if (!blackCanQueenSideCastle) {
                    return false;
                }
                if (board[0][1] != null || board[0][2] != null || board[0][3] != null) {
                    return false;
                }
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        Piece piece = this.getCell(i, j);
                        if (piece != null && !piece.getColor().equals(color)) {
                            if (piece.isValidMove(i, j, 0, 2, this)) {
                                return false;
                            }
                            if (piece.isValidMove(i, j, 0, 3, this)) {
                                return false;
                            }
                        }
                    }
                }
                // King side castle
            } else if (endX == 6) {
                if (!blackCanKingSideCastle) {
                    return false;
                }
                if (board[0][5] != null || board[0][6] != null) {
                    return false;
                }
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        Piece piece = this.getCell(i, j);
                        if (piece != null && !piece.getColor().equals(color)) {
                            if (piece.isValidMove(i, j, 0, 5, this)) {
                                return false;
                            }
                            if (piece.isValidMove(i, j, 0, 6, this)) {
                                return false;
                            }
                        }
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public int[] findKingPosition(String color) {
        // Find king of this color
        int kingRow = 0;
        int kingCol = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.getCell(i, j);
                if (piece instanceof King && piece.getColor().equals(color)) {
                    kingRow = i;
                    kingCol = j;
                }
            }
        }
        return new int[] { kingRow, kingCol };
    }

    /**
     * Checks if a move would put own King in check
     */
    public boolean moveWouldPutOwnKingInCheck(int startY, int startX, int endY, int endX) {
        Piece piece = board[startY][startX];
        Piece target = board[endY][endX];

        // Move piece
        board[startY][startX] = null;
        board[endY][endX] = piece;

        // Find if own king would be in check
        boolean result;
        if (piece == null) {
            result = false;
        } else {
            result = kingInCheck(piece.getColor());
        }

        // Move piece back
        board[startY][startX] = piece;
        board[endY][endX] = target;
        return result;
    }

    /**
     * Returns if a king is currently in check.
     */
    public boolean kingInCheck(String color) {

        // Find king of this color
        int kingRow = findKingPosition(color)[0];
        int kingCol = findKingPosition(color)[1];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.getCell(i, j);
                if (piece != null && !piece.getColor().equals(color)) {
                    if (piece.isValidMove(i, j, kingRow, kingCol, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns if a king is currently in checkmate.
     */
    public boolean kingInCheckmate(String color) {
        if (!kingInCheck(color)) {
            return false;
        }

        // See if any of own pieces can get king out of check
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.getCell(i, j);
                if (piece != null && piece.getColor().equals(color)) {
                    // Try all possible moves for piece
                    for (int iEnd = 0; iEnd < 8; iEnd++) {
                        for (int jEnd = 0; jEnd < 8; jEnd++) {
                            if (piece.isValidMove(i, j, iEnd, jEnd, this)) {
                                if (!moveWouldPutOwnKingInCheck(i, j, iEnd, jEnd)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns if a color is currently in checkmate.
     */
    public boolean inStalemate(String color) {
        // If king in check, not stalemate
        if (kingInCheck(color)) {
            return false;
        }

        // See if any of own pieces can move at all
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.getCell(i, j);
                if (piece != null && piece.getColor().equals(color)) {
                    // Try all possible moves for piece
                    for (int iEnd = 0; iEnd < 8; iEnd++) {
                        for (int jEnd = 0; jEnd < 8; jEnd++) {
                            if (piece.isValidMove(i, j, iEnd, jEnd, this)) {
                                if (!moveWouldPutOwnKingInCheck(i, j, iEnd, jEnd)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * checkWinner checks whether the game has reached a win condition.
     * checkWinner only looks for horizontal wins.
     *
     * @return 0 if nobody has won yet, 1 if white has won, and 2 if black
     *         has won, 3 if the game hits stalemate
     */
    public int checkWinner() {
        // Check for checkmate
        if (kingInCheckmate("White")) {
            return 2; // Black wins
        }
        if (kingInCheckmate("Black")) {
            return 1; // White wins
        }

        // Check for stalemate
        if ((inStalemate("White") && getCurrentPlayer())
                || (inStalemate("Black") && !getCurrentPlayer())) {
            return 3; // Stalemate
        }

        // Otherwise, game is not over yet
        return 0;
    }

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        // Create new board
        board = new Piece[8][8];
        whiteToMove = true;
        gameOver = false;
        inStalemate = false;
        cpuMode = false;
        whiteCanKingSideCastle = true;
        whiteCanQueenSideCastle = true;
        blackCanKingSideCastle = true;
        blackCanQueenSideCastle = true;

        // Place pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new org.cis1200.chess.Pawn(1, i, "Black");
            board[6][i] = new org.cis1200.chess.Pawn(6, i, "White");
        }

        // Place rooks
        board[0][0] = new org.cis1200.chess.Rook(0, 0, "Black");
        board[0][7] = new org.cis1200.chess.Rook(0, 7, "Black");
        board[7][0] = new org.cis1200.chess.Rook(7, 0, "White");
        board[7][7] = new org.cis1200.chess.Rook(7, 7, "White");

        // Place knights
        board[0][1] = new org.cis1200.chess.Knight(0, 1, "Black");
        board[0][6] = new org.cis1200.chess.Knight(0, 6, "Black");
        board[7][1] = new org.cis1200.chess.Knight(7, 1, "White");
        board[7][6] = new org.cis1200.chess.Knight(7, 6, "White");

        // Place bishops
        board[0][2] = new org.cis1200.chess.Bishop(0, 2, "Black");
        board[0][5] = new org.cis1200.chess.Bishop(0, 5, "Black");
        board[7][2] = new org.cis1200.chess.Bishop(7, 2, "White");
        board[7][5] = new org.cis1200.chess.Bishop(7, 5, "White");

        // Place queens
        board[0][3] = new org.cis1200.chess.Queen(0, 3, "Black");
        board[7][3] = new org.cis1200.chess.Queen(7, 3, "White");

        // Place kings
        board[0][4] = new org.cis1200.chess.King(0, 4, "Black");
        board[7][4] = new org.cis1200.chess.King(7, 4, "White");
    }

    /**
     * Returns true if it is white's turn, false if it is black's turn.
     */
    public boolean getCurrentPlayer() {
        return whiteToMove;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     */
    public Piece getCell(int y, int x) {
        return board[y][x];
    }

    /**
     * Sets coordinates as a certain piece.
     */
    public void setCell(int y, int x, Piece p) {
        board[y][x] = p;
    }

    /**
     * Returns the current state of the board.
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Switches the current side playing.
     */
    public void switchPlayer() {
        whiteToMove = !whiteToMove;
    }

    /**
     * Getter for status of game.
     */
    public boolean getGameOver() {
        return gameOver;
    }

    /**
     * Getter for stalemate status.
     */
    public boolean getInStalemate() {
        return inStalemate;
    }

    /**
     * Lets GameBoard set the state of this chess model as player vs. cpu.
     */
    public void setCpuMode(boolean b) {
        cpuMode = b;
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null) {
                    System.out.print(" ");
                } else {
                    System.out.print(board[i][j].toString());
                }

                if (j < 7) {
                    System.out.print(" | ");
                }
            }
            if (i < 7) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * Main method to run program's model, independent of controller and view.
     */
    public static void main(String[] args) {
        Chess t = new Chess();

        t.move(6, 3, 4, 3);

        t.printGameState();
    }
}

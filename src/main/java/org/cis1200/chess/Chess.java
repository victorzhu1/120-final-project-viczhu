package org.cis1200.chess;

/**
 * This class is a model for chess.
 * 
 * This game adheres to a Model-View-Controller design framework.
 * This framework is very effective for turn-based games. We
 * STRONGLY recommend you review these lecture slides, starting at
 * slide 8, for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec36.pdf
 * 
 * This model is completely independent of the view and controller.
 * This is in keeping with the concept of modularity! We can play
 * the whole game from start to finish without ever drawing anything
 * on a screen or instantiating a Java Swing object.
 * 
 * Run this file to see the main method play a game of TicTacToe,
 * visualized with Strings printed to the console.
 */
public class Chess {

    private Piece[][] board;

    private boolean whiteToMove;

    private boolean gameOver;

    /**
     * Constructor sets up game state.
     */
    public Chess() {
        reset();
        whiteToMove = true;
    }

    /**
     * Makes a move for white or black. Returns whether the move was possible.
     */

    public boolean move(int startY, int startX, int endY, int endX) {

        Piece piece = board[startY][startX];
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

        // If successful move was a pawn's first
        if (piece instanceof Pawn) {
            Pawn thisPawn = (Pawn) piece;
            if (thisPawn.getFirstMoveStatus()) {
                thisPawn.setFirstMoveStatus(false);
            }
        }

        // Track successful piece movement
        piece.moveTo(endY, endX);
        return true;
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
     * checkWinner checks whether the game has reached a win condition.
     * checkWinner only looks for horizontal wins.
     *
     * @return 0 if nobody has won yet, 1 if white has won, and 2 if black
     *         has won, 3 if the game hits stalemate
     */
    public int checkWinner() {
        // Check for checkmate
        if (kingInCheckmate("White")) {
            gameOver = true;
            return 2; // Black wins
        }
        if (kingInCheckmate("Black")) {
            gameOver = true;
            return 1; // White wins
        }

        // Check for stalemate
        if (inStalemate("White") || inStalemate("Black")) {
            gameOver = true;
            return 3; // Stalemate
        }

        // Otherwise, game is not over yet
        return 0;
    }

    /**
     * Returns if a king is currently in check.
     */
    public boolean kingInCheck(String color) {

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
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        // Create new board
        board = new Piece[8][8];
        gameOver = false;

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
     * getCurrentPlayer is a getter for the player
     * whose turn it is in the game.
     * 
     * @return true if it's Player 1's turn,
     *         false if it's Player 2's turn.
     */
    public boolean getCurrentPlayer() {
        return whiteToMove;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param x column to retrieve
     * @param y row to retrieve
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board. 0 = empty, 1 = Player 1, 2 = Player 2
     */
    public Piece getCell(int y, int x) {
        return board[y][x];
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void switchPlayer() {
        whiteToMove = !whiteToMove;
    }

    public void setPlayer(boolean b) {
        whiteToMove = b;
    }


    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     *
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     *
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        Chess t = new Chess();

        t.move(6, 3, 4, 3);

        t.printGameState();
    }
}

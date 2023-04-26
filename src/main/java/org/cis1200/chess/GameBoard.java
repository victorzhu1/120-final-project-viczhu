package org.cis1200.chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class instantiates the chess board.
 */
public class GameBoard extends JPanel {

    private Chess chessModel; // model for the game
    private JLabel status; // current status text

    private boolean gameOver;

    private int depth;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Piece selectedPiece = null;
    private boolean moveClick = false;

    private int gameMode;

    // Game constants
    public static final int BOARD_WIDTH = 720;
    public static final int BOARD_HEIGHT = 720;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        chessModel = new Chess(); // initializes model for the game
        status = statusInit; // initializes the status JLabel
        gameOver = false;

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {

                if (gameOver) {
                    return;
                }

                Point p = e.getPoint();

                int row = p.y / 90;
                int col = p.x / 90;

                if (!moveClick) {
                    selectedPiece = chessModel.getCell(row, col);
                    if (selectedPiece != null && (selectedPiece.getColor().equals("White")
                            ? true
                            : false) == chessModel.getCurrentPlayer()) {
                        selectedRow = row;
                        selectedCol = col;
                        moveClick = true;
                    }
                } else {
                    Piece replacement = chessModel.getCell(row, col);
                    if (replacement != null
                            && replacement.getColor().equals(selectedPiece.getColor())) {
                        selectedPiece = replacement;
                        selectedRow = row;
                        selectedCol = col;
                        updateStatus(); // updates the status JLabel
                        repaint(); // repaints the game board
                        return;
                    }
                    boolean validMove = chessModel.move(
                            selectedPiece.getPosition()[0],
                            selectedPiece.getPosition()[1], row, col
                    );
                    if (validMove) {
                        chessModel.switchPlayer();
                        selectedPiece = null;
                        moveClick = false;
                        selectedRow = -1;
                        selectedCol = -1;
                    } else {
                        System.out.println("Invalid Move");
                    }
                }
                updateStatus(); // updates the status JLabel
                repaint(); // repaints the game board
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {

        // Display instructions
        instructions();

        // Prompt for game mode, restart everything
        gameMode = promptForGameMode();
        chessModel.reset();
        gameOver = false;
        if (gameMode == 1) {
            chessModel.setCpuMode(true);
            status.setText("White to make first move!" + " Difficulty: " + (depth - 1));
        } else {
            status.setText("White to make first move!");
        }

        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();

        // Reset selections
        selectedPiece = null;
        moveClick = false;
        selectedRow = -1;
        selectedCol = -1;
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (chessModel.getCurrentPlayer()) {
            if (gameMode == 0) {
                status.setText("White to move.");
            } else {
                status.setText("White to move." + " Difficulty: " + (depth - 1));
            }
        } else if (gameMode == 0) {
            status.setText("Black to move.");
            checkResult();
        } else if (gameMode == 1 && depth == 1) {
            status.setText("Black to move." + " Difficulty: " + (depth - 1));
            chessModel.randomMove("Black");
            chessModel.switchPlayer();
            status.setText("White to move." + " Difficulty: " + (depth - 1));
            checkResult();
        } else {
            status.setText("Black making move..." + " Difficulty: " + (depth - 1));
            chessModel.makeBestMove(depth);
            chessModel.switchPlayer();
            status.setText("White to move." + " Difficulty: " + (depth - 1));
            checkResult();
        }
    }

    /**
     * Draws the game board.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the checkerboard
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color lightSquareColor = new Color(240, 209, 166, 100);
                Color darkSquareColor = new Color(176, 117, 35, 100);
                Color color = (row + col) % 2 == 0 ? lightSquareColor : darkSquareColor;
                g.setColor(color);
                g.fillRect(col * 90, row * 90, 90, 90);
            }
        }

        // Highlights valid moves for selected piece
        if (selectedPiece != null && (selectedPiece.getColor().equals("White")
                ? true
                : false) == chessModel.getCurrentPlayer()) {
            int currRow = selectedPiece.getPosition()[0];
            int currCol = selectedPiece.getPosition()[1];
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (currRow == row && currCol == col) {
                        continue;
                    }
                    if ((selectedPiece.isValidMove(currRow, currCol, row, col, chessModel) &&
                            !chessModel.moveWouldPutOwnKingInCheck(currRow, currCol, row, col)) ||
                            chessModel.isValidCastle(currRow, currCol, row, col)) {
                        g.setColor(new Color(192, 168, 135, 200));
                        if (chessModel.getCell(row, col) == null) {
                            // Draw a semi-transparent circle on top of valid move locations
                            g.fillOval(col * 90 + 33, row * 90 + 33, 25, 25);
                        } else if (chessModel.getCell(row, col) != null) {
                            // Draw viewable shape (4 triangles in corner) for valid moves
                            // containing a piece
                            int[] xTopLeft = { col * 90, col * 90 + 20, col * 90 };
                            int[] yTopLeft = { row * 90, row * 90, row * 90 + 20 };
                            g.fillPolygon(xTopLeft, yTopLeft, 3);

                            int[] xTopRight = { (col + 1) * 90, (col + 1) * 90 - 20,
                                (col + 1) * 90 };
                            int[] yTopRight = { row * 90, row * 90, row * 90 + 20 };
                            g.fillPolygon(xTopRight, yTopRight, 3);

                            int[] xBottomLeft = { col * 90, col * 90 + 20, col * 90 };
                            int[] yBottomLeft = { (row + 1) * 90, (row + 1) * 90,
                                (row + 1) * 90 - 20 };
                            g.fillPolygon(xBottomLeft, yBottomLeft, 3);

                            int[] xBottomRight = { (col + 1) * 90, (col + 1) * 90 - 20,
                                (col + 1) * 90 };
                            int[] yBottomRight = { (row + 1) * 90, (row + 1) * 90,
                                (row + 1) * 90 - 20 };
                            g.fillPolygon(xBottomRight, yBottomRight, 3);
                        }
                    }
                }
            }
        }

        // Draws selected square
        if (selectedRow != -1 && selectedCol != -1) {
            Color highlight = new Color(245, 169, 163, 110);
            g.setColor(highlight);
            g.fillRect(selectedCol * 90, selectedRow * 90, 90, 90);
        }

        // Highlight a king if in check
        String color = chessModel.getCurrentPlayer() ? "White" : "Black";
        int kingRow = 0;
        int kingCol = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = chessModel.getBoard()[i][j];
                if (piece instanceof King && piece.getColor().equals(color)) {
                    kingRow = i;
                    kingCol = j;
                }
            }
        }

        // Check if the king is in check
        boolean inCheck = chessModel.kingInCheck(color);
        boolean inCheckmate = chessModel.kingInCheckmate(color);

        // If the king is in check or checkmate, draw a red oval behind it
        if (inCheck || inCheckmate) {
            g.setColor(new Color(255, 0, 0, 75)); // red color with 50% opacity
            g.fillOval(9 + kingCol * 90, 10 + kingRow * 90, 70, 70);
        }

        // Draws pieces
        BufferedImage pieceImage;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = chessModel.getCell(i, j);
                if (piece != null) {
                    String filename = "files/" + piece.getType().toLowerCase()
                            + piece.getColor().substring(0, 1)
                            + ".png";
                    try {
                        pieceImage = ImageIO.read(new File(filename));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    g.drawImage(pieceImage, 14 + 90 * j, 14 + 90 * i, null);
                }
            }
        }

        // Draws board grid
        g.setColor(Color.BLACK);
        for (int i = 0; i < 9; i++) {
            g.drawLine(i * 90, 0, i * 90, 720);
            g.drawLine(0, i * 90, 720, i * 90);
        }

    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /**
     * Displays instructions (and choice to skip).
     */
    public void instructions() {
        String[] options = { "Skip instructions", "View instructions" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose to:", "Instructions",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, null
        );
        String[] nextAndSkipButton = { "Skip", "Next" };
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "You will play the chess match as white \n" +
                            "and your goal is to checkmate Black's king.",
                    "Instructions (1)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Each side has 8 pawns, 2 knights, 2 bishops, \n" +
                            "2 rooks, 1 queen, and 1 king. To win, \n" +
                            "white or black must use their pieces to put the \n" +
                            "other king in checkmate, which is being (1) in check, \n" +
                            "meaning it is currently being attacked by an enemy \n" +
                            "piece, and (2) in a position where that side has no \n" +
                            "legal moves to get out of check. \n",
                    "Instructions (2)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Pawns move either one or two squares forward \n" +
                            "on their first move. After, they can only move one \n" +
                            "square forward on moves, but can capture one square \n" +
                            "forward + one square horizontally (diagonally). \n" +
                            "Pawns have a special move, 'en passant', where they \n" +
                            "can capture an enemy pawn that has just moved two squares \n" +
                            "forward and landed directly left or right of the \n" +
                            "capturing pawn, which will capture and land diagonally. \n",
                    "Instructions (3)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Knights move in an L shape, meaning one square up/down, \n" +
                            "and two squares left/right (or vice versa). They can \n" +
                            "jump over pieces. \n",
                    "Instructions (4)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Bishops move diagonally, as far as they want. \n" +
                            "They can not move over pieces. \n",
                    "Instructions (5)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Rooks move vertially and horizontally, as \n" +
                            "far as they want. They can not move over pieces. \n",
                    "Instructions (6)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Queens can move vertially, horizontally, or \n" +
                            "diagonally, as far as they want. They can not \n" +
                            "move over pieces.",
                    "Instructions (7)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Kings can move one square left, right, up, down, \n" +
                            "or diagonally. They can not move into a check. \n" +
                            "The king can also castle, a move where it \n" +
                            "almost 'swaps' places with one of the 2 rooks \n" +
                            "next to it. This is only available if neither the \n" +
                            "king nor the rook being castled with have moved yet \n" +
                            "and the king does not land on or move through check. \n",
                    "Instructions (8)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Kings can not be captured. This means that \n" +
                            "they can not move into a check, and its other pieces \n" +
                            "can not make moves that would leave the king in check. \n \n" +
                            "So again, to win, you must put the opponent king in check, \n" +
                            "and also in a position where it cannot escape that check! \n",
                    "Instructions (9)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextAndSkipButton, null
            );
        }
        String[] nextButton = { "Next" };
        if (choice == 1) {
            choice = JOptionPane.showOptionDialog(
                    this,
                    "Instructions over! Press next to select game mode. \n",
                    "Instructions (10)",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, nextButton, null
            );
        }
    }

    /**
     * Asks at start of game which mode to play.
     */
    public int promptForGameMode() {
        String[] options = { "Player vs. Player", "Player vs. Computer" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose game mode:", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, null
        );

        // Player vs computer selected, pick depth
        if (choice == 1) {
            String[] depthOptions = { "Level 2", "Level 1", "Level '0'" };
            int depthChoice = JOptionPane.showOptionDialog(
                    this,
                    "Higher difficulty takes longer to make moves. \n Level '0' makes random moves!",
                    "Computer Difficulty",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, depthOptions, null
            );
            depth = 3 - depthChoice;

        }
        return choice;
    }

    public void checkResult() {
        int winner = chessModel.checkWinner();

        if (winner == 1 || chessModel.getGameOver()) {
            status.setText("White wins!!!");
            gameOver = true;
            JOptionPane.showMessageDialog(null, "White wins!!!");
        } else if (winner == 2) {
            status.setText("Black wins!!!");
            gameOver = true;
            JOptionPane.showMessageDialog(null, "Black wins!!!");
        } else if (winner == 3 || chessModel.getInStalemate()) {
            status.setText("Stalemate!!!");
            gameOver = true;
            JOptionPane.showMessageDialog(null, "Stalemate!!!");
        }
    }
}

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

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Piece selectedPiece = null;
    private boolean moveClick = false;

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


        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {

                Point p = e.getPoint();

                int row = p.y / 90;
                int col = p.x / 90;

                if (!moveClick) {
                    selectedPiece = chessModel.getCell(row, col);
                    if (selectedPiece != null && (selectedPiece.getColor().equals("White")
                            ? true : false) == chessModel.getCurrentPlayer()) {
                        selectedRow = row;
                        selectedCol = col;
                        moveClick = true;
                    }
                } else {
                    Piece replacement = chessModel.getCell(row, col);
                    if (replacement != null && replacement.getColor().equals(selectedPiece.getColor())) {
                        selectedPiece = replacement;
                        selectedRow = row;
                        selectedCol = col;
                        updateStatus(); // updates the status JLabel
                        repaint(); // repaints the game board
                        return;
                    }
                    boolean validMove = chessModel.move(selectedPiece.getPosition()[0],
                            selectedPiece.getPosition()[1], row, col);
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
        chessModel.reset();
        status.setText("White to make first move");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();

        selectedPiece = null;
        moveClick = false;
        selectedRow = -1;
        selectedRow = -1;

        chessModel.setPlayer(true);
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (chessModel.getCurrentPlayer()) {
            status.setText("White to move");
        } else {
            status.setText("Black to move");
        }

        int winner = chessModel.checkWinner();

        if (winner == 1) {
            status.setText("White wins!!!");
        } else if (winner == 2) {
            status.setText("Black wins!!!");
        } else if (winner == 3) {
            status.setText("Stalemate!!!");
        }
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
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
                ? true : false) == chessModel.getCurrentPlayer()) {
            int currRow = selectedPiece.getPosition()[0];
            int currCol = selectedPiece.getPosition()[1];
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (currRow == row && currCol == col) {
                        continue;
                    }
                    if (selectedPiece.isValidMove(currRow, currCol, row, col, chessModel) &&
                    !chessModel.moveWouldPutOwnKingInCheck(currRow, currCol, row, col)) {
                        // Draw a semi-transparent circle on top of valid move locations
                        g.setColor(new Color(192, 168, 135, 200));
                        g.fillOval(col * 90 + 33, row * 90 + 33, 25, 25);
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

        // If the king is in check, draw a red oval behind it
        if (inCheck) {
            g.setColor(new Color(255, 0, 0, 75)); // red color with 50% opacity
            g.fillOval(9 + kingCol * 90, 10 + kingRow * 90, 70, 70);
        }

        // Draws pieces
        BufferedImage pieceImage;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = chessModel.getCell(i, j);
                if (piece != null) {
                    String filename = "files/" + piece.getType().toLowerCase() + piece.getColor().substring(0, 1)
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
}

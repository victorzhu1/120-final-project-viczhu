package org.cis1200;

import javax.swing.*;

public class Game {
    /**
     * Main method run to start and run the game.
     */
    public static void main(String[] args) {
        Runnable game = new org.cis1200.chess.RunChess();

        SwingUtilities.invokeLater(game);
    }
}

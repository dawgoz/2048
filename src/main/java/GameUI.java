package main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

class GameUI {
    private Game game = new Game();
    private boolean isGamePaused = false;
    private boolean is2048Reached = false;
    private boolean isGameLostAfter2048 = false;
    private static final int squareSizeScale = 6;
    private static final Color backgroundColor = new Color(187, 173, 160);
    private static final Color emptySquareColor = new Color(205, 193, 181);
    private static final Color twoFourFontColor = new Color(117, 107, 97);
    private static final Color pauseLabelColor = new Color(255, 0, 0);
    private static final Color fontColor = new Color(244, 230, 219);
    private static final Font font = new Font("Clear Sans", Font.BOLD, 17);
    private final int squareSize;
    private final JFrame frame;
    private final JLabel score;
    private final JLabel highScore;
    private final JLabel time;
    private final JLabel winState;
    private final JLabel pauseState;
    private final Timer timer;
    private final int delayTime = 1000;
    private int secondsElapsed;
    private Grid grid;
    private final int secInHour = 3600;
    private final int secInMin = 60;
    private final int row = 4;
    private final int col = 4;

    private GameUI() {
        final int minWindowHeight = 700;
        final int minWindowWidth = 500;
        final int hGapTop = 10;
        final int vGapTop = 10;
        final int hGapWin = 5;
        final int vGapWin = 5;
        squareSize = (minWindowHeight / squareSizeScale);

        frame = new JFrame();
        frame.setMinimumSize(new Dimension(minWindowWidth, minWindowHeight));
        frame.pack();
        frame.setTitle("2048");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.setBackground(backgroundColor);

        JPanel topLeftPanel = new JPanelCustom();
        topLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, hGapTop, vGapTop));
        topLeftPanel.setBorder(new CompoundBorder(topLeftPanel.getBorder(),
                new EmptyBorder(10, 15, -15, 0)));

        JPanel topRightPanel = new JPanelCustom();
        topRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, hGapTop, vGapTop));
        topRightPanel.setBorder(new CompoundBorder(topRightPanel.getBorder(),
                new EmptyBorder(10, 0, -200, 15)));

        JPanel winPanel = new JPanelCustom();
        winPanel.setLayout(new FlowLayout(FlowLayout.CENTER, hGapWin, vGapWin));
        winPanel.setBorder(new CompoundBorder(winPanel.getBorder(),
                new EmptyBorder(0, 0, -15, 0)));

        JPanel scoreTimePausePanel = new JPanelCustom();
        scoreTimePausePanel.setLayout(new GridLayout(4, 1));

        JPanel instructionPanel = new JPanelCustom();
        instructionPanel.setLayout(new GridLayout(3, 1));

        JPanel mainPanel = new JPanelCustom();
        mainPanel.setLayout(new GridLayout());

        score = new JLabelCustom("Score: 0 ");
        highScore = new JLabelCustom(null);
        updateHighScoreText();
        time = new JLabelCustom("Time: 00:00 ");
        pauseState = new JLabelCustom(" ");
        JLabel instructions = new JLabelCustom("<html>INSTRUCTIONS<br>P - pause game<br>ESC - new game<br>MOVE - WASD of arrow keys<html>");
        winState = new JLabelCustom(null);
        clearGameStatusText();
        winState.setFont(font.deriveFont(20f));
        winState.setBorder(new EmptyBorder(0, 0, 25, 0));

        scoreTimePausePanel.add(score);
        scoreTimePausePanel.add(highScore);
        scoreTimePausePanel.add(time);
        scoreTimePausePanel.add(pauseState);
        topLeftPanel.add(scoreTimePausePanel);

        instructionPanel.add(instructions);
        topRightPanel.add(instructionPanel);

        mainPanel.add(topLeftPanel);
        mainPanel.add(topRightPanel);
        winPanel.add(winState);

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(winPanel, BorderLayout.SOUTH);
        frame.add(grid = new Grid());
        secondsElapsed = 0;
        timer = new Timer(delayTime, e -> {
            secondsElapsed++;
            time.setText(String.format("Time: %02d:%02d ", (secondsElapsed % secInHour) / secInMin, (secondsElapsed % secInMin)));
        });
        timer.start();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_P -> togglePause();
                    case KeyEvent.VK_ESCAPE -> resetGame();
                }
                if (!game.isGameLost() && !isGamePaused && !isGameLostAfter2048) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP, KeyEvent.VK_W -> game.shiftTiles(Game.Direction.UP);
                        case KeyEvent.VK_DOWN, KeyEvent.VK_S -> game.shiftTiles(Game.Direction.DOWN);
                        case KeyEvent.VK_LEFT, KeyEvent.VK_A -> game.shiftTiles(Game.Direction.LEFT);
                        case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> game.shiftTiles(Game.Direction.RIGHT);
                    }
                    updateAfterMove();
                }
            }
        });
        frame.setVisible(true);
    }

    private void updateAfterMove() {
        grid.updateTiles();
        score.setText(String.format("Score: %d ", game.getScore()));
        updateHighScoreText();
        if (!is2048Reached) {
            checkGameStatus();
        } else {
            clearGameStatusText();
            timer.start();
            if (!isGameLostAfter2048) {
                checkGameStatusAfterWin();
            }
        }
    }

    private void resetGame() {
        if (!isGamePaused) {
            frame.remove(grid);
            is2048Reached = false;
            isGameLostAfter2048 = false;
            game = new Game();
            grid = new Grid();
            frame.add(grid);
            grid.refreshSquares();
            secondsElapsed = 0;
            score.setText("Score: 0 ");
            updateHighScoreText();
            clearGameStatusText();
            timer.stop();
            time.setText("Time: 00:00 ");
            timer.start();
        }
    }

    private void updateHighScoreText() {
        highScore.setText(String.format("High Score: %d", game.getHighScore()));
    }

    private void pauseGame() {
        pauseState.setText("GAME PAUSED");
        pauseState.setForeground(pauseLabelColor);
        timer.stop();
    }

    private void resumeGame() {
        pauseState.setText(" ");
        if (!game.isGameLost() && !is2048Reached) {
            timer.start();
        }
    }

    private void togglePause() {
        if (isGamePaused) {
            resumeGame();
        } else {
            pauseGame();
        }
        isGamePaused = !isGamePaused;
    }

    private void checkGameStatus() {
        if (game.isGameWon()) {
            setWinText();
            is2048Reached = true;
            timer.stop();
        } else if (game.isGameLost()) {
            setGameOverText();
            timer.stop();
        }
    }

    private void checkGameStatusAfterWin() {
        if (game.isGameLost()) {
            setGameOverText();
            timer.stop();
        }
    }

    private void setGameOverText() {
        winState.setText("<html><center>main.java.Game Over!<br>Press ESC to play again<center><html>");
    }

    private void setWinText() {
        winState.setText("<html><center>You won!<br>Press any key to continue playing<center><html>");
    }

    private void clearGameStatusText() {
        winState.setText("<html><center>&nbsp;<br>&nbsp;<center><html>");
    }

    private static class JLabelCustom extends JLabel {
        private JLabelCustom(String text) {
            super(text);
            this.setFocusable(false);
            this.setFont(font);
            this.setForeground(fontColor);
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }

    private static class JPanelCustom extends JPanel {
        private JPanelCustom() {
            super();
            this.setFocusable(false);
            this.setBackground(backgroundColor);
        }
    }

    private class Grid extends JPanel {
        private final Square[][] cells = new Square[row][col];

        private Grid() {
            this.setLayout(new GridBagLayout());
            this.setBackground(backgroundColor);
            GridBagConstraints gbc = new GridBagConstraints();
            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    gbc.gridx = c;
                    gbc.gridy = r;
                    Square square = new Square(r, c, null);
                    cells[r][c] = square;
                    square.setBorder(new LineBorder(backgroundColor, squareSize / 19));
                    add(square, gbc);
                }
            }
            updateTiles();
        }

        private void updateTiles() {
            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    if (game.getBoard()[r][c] != null) {
                        Color tileColor = game.getBoard()[r][c].getColor();
                        cells[r][c].setBackground(tileColor);
                        cells[r][c].setTileValue(game.getBoard()[r][c].toString());
                    } else {
                        cells[r][c].setBackground(emptySquareColor);
                    }
                    cells[r][c].repaint();
                    this.repaint();
                }
            }
        }

        private void refreshSquares() {
            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    cells[r][c].revalidate();
                    cells[r][c].repaint();
                }
            }
        }
    }

    public class Square extends JPanel {
        private final int row;
        private final int col;
        private String tileValue;

        private Square(int row, int col, String tileValue) {
            this.row = row;
            this.col = col;
            this.tileValue = tileValue;
        }

        private void setTileValue(String value) {
            this.tileValue = value;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (game.getBoard()[row][col] != null) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

                float fontScale = 0.42f;
                if (tileValue.length() > 3) {
                    float fontDecrease = 0.075f;
                    fontScale -= (tileValue.length() - 3) * fontDecrease;
                }
                g2.setFont(font.deriveFont(squareSize * fontScale));

                g2.setColor(twoFourFontColor);
                if (game.getBoard()[row][col].getPower() > 2) {
                    g2.setColor(fontColor);
                }

                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(tileValue, g2);
                int x = (this.getWidth() - (int) r.getWidth()) / 2;
                int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(tileValue, x, y);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(squareSize, squareSize);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::new);
    }
}
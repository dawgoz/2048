import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Arrays;


class Game {
    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final Tile[][] board;
    private final Tile[][] oldBoard;
    private final Random randomNumber;
    private int score;
    private int highScore;
    private static final String highScoreLocation = "../high_score.txt";

    Game() {
        board = new Tile[4][4];
        oldBoard = new Tile[4][4];
        score = 0;
        randomNumber = new Random();
        initializeBoard();

        if (!new File(highScoreLocation).exists()) {
            saveHighScore();
        }
        loadHighScore();
    }

    private void saveHighScore() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(highScoreLocation), StandardCharsets.UTF_8))) {
            this.highScore = score;
            writer.write(String.valueOf(this.highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHighScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(highScoreLocation));
            String num = reader.readLine();
            this.highScore = (num == null) ? 0 : Integer.parseInt(num);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeBoard() {
        int toGen = 2;
        while (toGen > 0) {
            int rand = randomNumber.nextInt(16);
            if (board[rand / 4][rand % 4] == null) {
                board[rand / 4][rand % 4] = new Tile(isFour());
                toGen--;
            }
        }
    }

    int getHighScore() {
        return this.highScore;
    }

    Tile[][] getBoard() {
        return board;
    }

    int getScore() {
        return score;
    }

    boolean isGameWon() {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (board[r][c] != null && board[r][c].getPower() == 11) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isGameLost() {
        int i, j;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (board[r][c] != null) {
                    i = r + 1;
                    j = c;
                    if (i <= 3) {
                        if (board[i][j] != null && board[r][c].getPower() == board[i][j].getPower()) {
                            return false;
                        }
                    }
                    i = r - 1;
                    if (i >= 0) {
                        if (board[i][j] != null && board[r][c].getPower() == board[i][j].getPower()) {
                            return false;
                        }
                    }
                    i = r;
                    j = c - 1;
                    if (j >= 0) {
                        if (board[i][j] != null && board[r][c].getPower() == board[i][j].getPower()) {
                            return false;
                        }
                    }
                    j = c + 1;
                    if (j <= 3) {
                        if (board[i][j] != null && board[r][c].getPower() == board[i][j].getPower()) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    void shiftTiles(Direction direction) {
        rotateBoard(direction);
        for (int i = 0; i < 4; i++) {
            board[i] = combineTiles(board[i]);
        }
        rotateBoard(direction);
        if (isBoardChanged()) {
            for (int i = 0; i < 4; i++) {
                oldBoard[i] = Arrays.copyOf(board[i], 4);
            }
            addNewTile();
        }
        if (highScore <= score) {
            saveHighScore();
        }
    }

    private boolean isBoardChanged() {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (board[r][c] != oldBoard[r][c]) {
                    return true;
                }
                if (board[r][c] != null && oldBoard[r][c] != null) {
                    if (board[r][c].getPower() != oldBoard[r][c].getPower()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void rotateBoard(Direction direction) {
        Tile temp;
        switch (direction) {
            case UP: // invert around (x, x) axis
                for (int r = 1; r < 4; r++) {
                    for (int c = 0; c < r; c++) {
                        temp = board[r][c];
                        board[r][c] = board[c][r];
                        board[c][r] = temp;
                    }
                }
                break;
            case DOWN: // invert around (4-x, x) axis
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < (3 - r); c++) {
                        temp = board[r][c];
                        board[r][c] = board[3 - c][3 - r];
                        board[3 - c][3 - r] = temp;
                    }
                }
                break;
            case RIGHT: // invert around y axis
                for (int r = 0; r < 4; r++) {
                    temp = board[r][0];
                    board[r][0] = board[r][3];
                    board[r][3] = temp;
                    temp = board[r][1];
                    board[r][1] = board[r][2];
                    board[r][2] = temp;
                }
                break;
            case LEFT:
                break;
        }
    }

    private void compressBoard(Tile[] sub) {
        for (int j = 0; j < 4; j++) {
            if (sub[j] == null) {
                for (int i = j + 1; i < 4; i++) {
                    if (sub[i] != null) {
                        sub[j] = sub[i];
                        sub[i] = null;
                        break;
                    }
                }
            }
        }
    }

    private Tile[] combineTiles(Tile[] sub) {
        compressBoard(sub);
        for (int i = 0; i < 3; i++) {
            if (sub[i] != null) {
                if (sub[i + 1] != null && sub[i + 1].getPower() == sub[i].getPower()) {
                    sub[i + 1] = null;
                    sub[i].increasePow();
                    score += sub[i].getValue();
                }
            }
        }
        compressBoard(sub);
        return sub;
    }

    private boolean isFour() {
        return randomNumber.nextInt(10) == 5; //10% chance
    }

    private void addNewTile() {
        int emptyTilesCount = 0;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (board[r][c] == null) {
                    emptyTilesCount++;
                }
            }
        }
        if (emptyTilesCount == 0) {
            return;
        }
        int rand = randomNumber.nextInt(emptyTilesCount);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (board[r][c] == null && rand > 0) {
                    rand--;
                }
                if (board[r][c] == null && rand == 0) {
                    board[r][c] = new Tile(isFour());
                    return;
                }
            }
        }
    }
}
package main.java;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.opentest4j.TestAbortedException;

import java.awt.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @BeforeAll
    public static void init() {
        System.out.println("<< Running before all! >>");
        final String highScoreLocation = "./highScore.txt";
        boolean success = new File(highScoreLocation).delete();
        if (success) {
            System.out.println("File deleted!");
        } else {
            System.out.println("File not deleted!");
        }
    }

    @Test
    //@Disabled("")
    @DisplayName("Should return 0 as game score")
    void scoreOnGameInit() {
        System.out.println("<< scoreOnGameInit test >>");
        Game game = new Game();
        assertEquals(0, game.getScore());
    }

    @Test
    @DisplayName("Should create a new high score text file")
    void highScoreFileCreation() {
        System.out.println("<< highScoreFileCreation test >>");
        final String highScoreLocation = "./highScore.txt";
        boolean fileExists = new File(highScoreLocation).exists();
        Game game = new Game();
        Assumptions.assumeFalse(fileExists);
        game.saveHighScore();
        System.out.println("File created!");
        fileExists = new File(highScoreLocation).exists();
        assertTrue(fileExists);
    }

    @Test
    @DisplayName("Should load high score from existing text file")
    void highScoreLoadingFromFile() {
        System.out.println("<< highScoreLoadingFromFile test >>");
        final String highScoreLocation = "./highScore.txt";
        boolean fileExists = new File(highScoreLocation).exists();
        Game game = new Game();
        Assumptions.assumeTrue(fileExists);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(highScoreLocation));
            int result = Integer.parseInt(reader.readLine());
            game.loadHighScore();
            System.out.println("High score = " + result);
            assertEquals(result, game.getHighScore());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Should return correct HEX color code of tile")
    void checkTileColor() {
        System.out.println("<< checkTileColor test >>");
        Tile tile = new Tile(false);
        for (int i = 0; i < 3; i++) {
            tile.increasePow();
        }
        String result = Integer.toHexString(tile.getColor().getRGB()).toUpperCase(Locale.ROOT);
        assertEquals(result, "FFF8935F");
    }

    @Test
    @DisplayName("Should return correct value after power increase")
    void checkTileValue() {
        System.out.println("<< checkTileValue test >>");
        Tile tile = new Tile(false);
        for (int i = 0; i < 4; i++) {
            tile.increasePow();
        }
        assertEquals(tile.getValue(), 32);
    }
}
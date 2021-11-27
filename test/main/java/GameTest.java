package main.java;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.opentest4j.TestAbortedException;

import java.awt.*;
import java.io.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testScoreOnGameInit() {
        Game game = new Game();
        assertEquals(0, game.getScore());
    }

    @Test
    void testHighScoreFileCreation(){
        final String highScoreLocation = "./highScore.txt";
        Game game= new Game();
        File myFile = new File(highScoreLocation);
        myFile.delete();
        boolean fileExists = new File(highScoreLocation).exists();
        if (!fileExists){
            game.saveHighScore();
            System.out.println("File created");
            fileExists = new File(highScoreLocation).exists();
            assertTrue(fileExists);
        }
        else {
            throw new TestInstantiationException("Aborted");
        }

    }

    @Test
    void testLoadHighScoreFromFile() {
        final String highScoreLocation = "./highScore.txt";
        boolean fileExists = new File(highScoreLocation).exists();
        Game game= new Game();
        if(fileExists){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(highScoreLocation));
                int result = Integer.parseInt(reader.readLine());
                game.loadHighScore();
                assertEquals(result, game.getHighScore());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testTileColorGetter(){
        Tile tile = new Tile(false);
        for (int i=0; i<3; i++){
            tile.increasePow();
        }
    String result = Integer.toHexString(tile.getColor().getRGB()).toUpperCase(Locale.ROOT);
        assertEquals(result, "FFF8935F");
    }

    @Test
    void testTileValueGetter(){
        Tile tile = new Tile(false);
        for (int i=0; i<4; i++){
            tile.increasePow();
        }
        assertEquals(tile.getValue(), 32);
    }
}
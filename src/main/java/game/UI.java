package game;

import bagel.Font;
import bagel.Image;
import bagel.util.Colour;

import java.util.Properties;

public class UI {

    private final int textsize;
    private final Font textfont;
    private final Colour textcolour;

    private String waveText;
    private double waveX;
    private double waveY;

    private String scoreText;
    private double scoreX;
    private double scoreY;

    private Image playerLifeImage;
    private double playerLivesStartPosition;
    private double playerLivesStartX;
    private double playerLivesStartY;
    private int playerLivesGap;

    public UI(Properties gameProps) {

        String[] colour = gameProps.getProperty("text.colour").split(",");
        double r = Double.parseDouble(colour[0]);
        double g = Double.parseDouble(colour[1]);
        double b = Double.parseDouble(colour[2]);
        textcolour = new Colour(r,g,b);

        textsize = Integer.parseInt(gameProps.getProperty("text.size"));
        textfont = new Font(gameProps.getProperty("text.font"), textsize);

        waveText = gameProps.getProperty("wave.text");
        String[] wavePos = gameProps.getProperty("wave.pos").split(",");
        waveX = Double.parseDouble(wavePos[0]);
        waveY = Double.parseDouble(wavePos[1]);

        scoreText = gameProps.getProperty("score.text");
        String[] scorePos = gameProps.getProperty("score.pos").split(",");
        scoreX = Double.parseDouble(scorePos[0]);
        scoreY = Double.parseDouble(scorePos[1]);

        playerLifeImage = new Image(gameProps.getProperty("playerLives.image"));
        String[] playerLivesStartPosition = gameProps.getProperty("playerLives.startPosition").split(",");
        playerLivesStartX = Double.parseDouble(playerLivesStartPosition[0]);
        playerLivesStartY = Double.parseDouble(playerLivesStartPosition[1]);
        playerLivesGap = Integer.parseInt(gameProps.getProperty("playerLives.gap"));
    }

    public void draw(int lives, int wave, int score) {
        // draw live;
        for (int i = 0; i < lives; i++){
            playerLifeImage.draw(playerLivesStartX + i * playerLivesGap, playerLivesStartY);
        }
        // draw wave;
        textfont.drawString(String.format("%s %d", waveText, wave), waveX, waveY);
        // draw score;
        textfont.drawString(String.format("%s %d", scoreText, score), scoreX, scoreY);

    }
}

package game;

import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;

import java.util.ArrayList;
import java.util.Properties;

public class UI {

    private final int textsize;
    private final Font textfont;
    private final Colour textcolour;

    private final String waveText;
    private final double waveX;
    private final double waveY;

    private final String scoreText;
    private final double scoreX;
    private final double scoreY;

    private final Image playerLifeImage;
    private final double playerLivesStartX;
    private final double playerLivesStartY;
    private final int playerLivesGap;

    private final String pausedTitleText;
    private final int pausedTitleSize;
    private final Font pausedTitleFont;
    private final int pausedTitlePosY;
    private final ArrayList<String> controlListTextSplit;
    private final int controlsListStartPosY;
    private final int controlsListRowGap;
    private final String timescaleText;
    private final double timeScalePosX;
    private final double timeScalePosY;

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

        //pause mode:
        pausedTitleText = gameProps.getProperty("pausedTitle.text");
        pausedTitleSize = Integer.parseInt(gameProps.getProperty("pausedTitle.size"));
        pausedTitleFont = new Font(gameProps.getProperty("text.font"), pausedTitleSize);
        pausedTitlePosY = Integer.parseInt(gameProps.getProperty("pausedTitle.posY"));
        controlListTextSplit = new ArrayList<>();
        String[] controls = gameProps.getProperty("controlsList.text").split("[,]");
        for (String control: controls){
            controlListTextSplit.add(control.trim());
        }
        controlsListStartPosY = Integer.parseInt(gameProps.getProperty("controlsList.startPosY"));
        controlsListRowGap = Integer.parseInt(gameProps.getProperty("controlsList.rowGap"));
        timescaleText = gameProps.getProperty("timescale.text");
        String[] timeScalePos = gameProps.getProperty("timescale.pos").split(",");
        timeScalePosX = Double.parseDouble(timeScalePos[0]);
        timeScalePosY = Double.parseDouble(timeScalePos[1]);
    }


    public void draw(int lives, int wave, int score) {
        // draw live;
        for (int i = 0; i < lives; i++){
            playerLifeImage.draw(playerLivesStartX + i * playerLivesGap, playerLivesStartY);
        }
        // draw wave;
        textfont.drawString(String.format("%s %d", waveText, wave), waveX, waveY, new DrawOptions().setBlendColour(textcolour));
        // draw score;
        textfont.drawString(String.format("%s %d", scoreText, score), scoreX, scoreY, new DrawOptions().setBlendColour(textcolour));

    }

    public void draw_pause(double timeScale){
        DrawOptions options = new DrawOptions().setBlendColour(textcolour);

        //draw title
        drawCentreText(pausedTitleText,pausedTitleFont,pausedTitlePosY,options);

        //draw controls list
        for (int i = 0; i < controlListTextSplit.size(); i++) {
            double y = controlsListStartPosY + i * controlsListRowGap;
            drawCentreText(controlListTextSplit.get(i), textfont, y, options);
        }

        //draw timescale
        String timeScaleStr = String.format("%s %s", timescaleText, timeScale );
        textfont.drawString(timeScaleStr, timeScalePosX, timeScalePosY, options);
    }

    private void drawCentreText(String text, Font font, double y, DrawOptions options){
        double x = (ShadowAliens.screenWidth/2 - font.getWidth(text)/2);
        font.drawString(text, x, y, options);
    }
}

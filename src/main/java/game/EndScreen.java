package game;

import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.DrawOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import bagel.Keys;

public class EndScreen extends Screen {

    private Player player;
    private boolean isWin;
    private boolean restartRequested = false;
    private Font resultFont;
    private Font textFont;
    private String resultText;
    private double resultPosY;
    private ArrayList<String> instructionsList;
    private double instructionsStartPosY;
    private double instructionsRowGap;
    private DrawOptions textColour;

    public EndScreen(Properties gameProps, boolean isWin) {
        super(gameProps);
        this.isWin = isWin;

        // load player (can move left/right, cannot shoot)
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double playerX = ShadowAliens.getScreenWidth() / 2;
        double playerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int playerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initialLives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        int hitInvincibilityTime = Integer.parseInt(gameProps.getProperty("player.hitInvincibilityTime"));
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime);

        // load text settings
        String fontPath = gameProps.getProperty("text.font");
        int defaultSize = Integer.parseInt(gameProps.getProperty("text.size"));

        textFont = new Font(fontPath, defaultSize);

        String[] colours = gameProps.getProperty("text.colour").split(",");
        textColour = new DrawOptions().setBlendColour(
                Double.parseDouble(colours[0]),
                Double.parseDouble(colours[1]),
                Double.parseDouble(colours[2]), 1);

        if (isWin) {
            int resultSize = Integer.parseInt(gameProps.getProperty("end.win.size"));
            resultFont = new Font(fontPath, resultSize);
            resultText = gameProps.getProperty("end.win.text");
            resultPosY = Double.parseDouble(gameProps.getProperty("end.win.posY"));
        } else {
            int resultSize = Integer.parseInt(gameProps.getProperty("end.lose.size"));
            resultFont = new Font(fontPath, resultSize);
            resultText = gameProps.getProperty("end.lose.text");
            resultPosY = Double.parseDouble(gameProps.getProperty("end.lose.posY"));
        }

        instructionsStartPosY = Double.parseDouble(gameProps.getProperty("end.instructionsList.startPosY"));
        instructionsRowGap = Double.parseDouble(gameProps.getProperty("end.instructionsList.rowGap"));
        instructionsList = new ArrayList<>(Arrays.asList(
                gameProps.getProperty("end.instructionsList.text").split(",")));
    }

    @Override
    public void update(Input input) {
        player.movement(input, 1.0);
        if (input.wasPressed(Keys.SPACE)) {
            restartRequested = true;
        }
        draw();
    }

    public boolean shouldRestart() {
        return restartRequested;
    }

    @Override
    public void draw() {
        // draw player
        player.draw();

        // draw win/lose text (centred)
        double resultX = ShadowAliens.getScreenWidth() / 2 - resultFont.getWidth(resultText) / 2;
        resultFont.drawString(resultText, resultX, resultPosY, textColour);

        // draw instructions list (centred)
        for (int i = 0; i < instructionsList.size(); i++) {
            String line = instructionsList.get(i);
            double x = ShadowAliens.getScreenWidth() / 2 - textFont.getWidth(line) / 2;
            double y = instructionsStartPosY + i * instructionsRowGap;
            textFont.drawString(line, x, y, textColour);
        }
    }
}


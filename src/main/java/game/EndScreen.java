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

    // Player shown on the end screen
    private Player player;

    // True when the player presses SPACE to restart
    private boolean restartRequested = false;

    // Fonts used for result text and instruction text
    private Font resultFont;
    private Font textFont;

    // Win or lose message
    private String resultText;
    private double resultPosY;

    // Restart instruction lines
    private ArrayList<String> instructionsList;
    private double instructionsStartPosY;
    private double instructionsRowGap;

    // Text colour used for all end screen text
    private DrawOptions textColour;

    public EndScreen(Properties gameProps, boolean isWin) {
        super(gameProps);

        // Load player settings
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double playerX = ShadowAliens.getScreenWidth() / 2;
        double playerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int playerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initialLives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        int hitInvincibilityTime = Integer.parseInt(gameProps.getProperty("player.hitInvincibilityTime"));
        Image projectileImage = new Image(gameProps.getProperty("projectile.image"));
        double projectileSpeed = Double.parseDouble(gameProps.getProperty("projectile.movementSpeed"));
        Image invincibilityImage = new Image(gameProps.getProperty("invincibility.image"));

        // Create player for display and movement on the end screen
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime, projectileImage, projectileSpeed, invincibilityImage);

        // Load text font settings
        String fontPath = gameProps.getProperty("text.font");
        int defaultSize = Integer.parseInt(gameProps.getProperty("text.size"));

        textFont = new Font(fontPath, defaultSize);

        // Load text colour
        String[] colours = gameProps.getProperty("text.colour").split(",");
        textColour = new DrawOptions().setBlendColour(
                Double.parseDouble(colours[0]),
                Double.parseDouble(colours[1]),
                Double.parseDouble(colours[2]), 1);

        // Load win or lose result text
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

        // Load instruction text settings
        instructionsStartPosY = Double.parseDouble(gameProps.getProperty("end.instructionsList.startPosY"));
        instructionsRowGap = Double.parseDouble(gameProps.getProperty("end.instructionsList.rowGap"));
        instructionsList = new ArrayList<>(Arrays.asList(
                gameProps.getProperty("end.instructionsList.text").split(",")));
    }

    @Override
    public void update(Input input) {
        // Allow player to move left and right on the end screen
        player.movement(input, 1.0);

        // Request restart when SPACE is pressed
        if (input.wasPressed(Keys.SPACE)) {
            restartRequested = true;
        }

        // Draw the end screen
        draw();
    }

    public boolean shouldRestart() {
        // Return whether the game should restart
        return restartRequested;
    }

    @Override
    public void draw() {
        // Draw player
        player.draw();

        // Draw win or lose text in the centre
        double resultX = ShadowAliens.getScreenWidth() / 2 - resultFont.getWidth(resultText) / 2;
        resultFont.drawString(resultText, resultX, resultPosY, textColour);

        // Draw instruction lines in the centre
        for (int i = 0; i < instructionsList.size(); i++) {
            String line = instructionsList.get(i);
            double x = ShadowAliens.getScreenWidth() / 2 - textFont.getWidth(line) / 2;
            double y = instructionsStartPosY + i * instructionsRowGap;
            textFont.drawString(line, x, y, textColour);
        }
    }
}
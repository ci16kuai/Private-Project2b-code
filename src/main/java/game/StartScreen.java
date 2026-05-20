package game;

import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.DrawOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class StartScreen extends Screen {

    private Player player;
    private Font titleFont;
    private Font textFont;
    private String titleText;
    private double titlePosY;
    private ArrayList<String> instructionsList;
    private double instructionsStartPosY;
    private double instructionsRowGap;
    private DrawOptions textColour;

    public StartScreen(Properties gameProps) {
        super(gameProps);

        // load player (can move left/right, cannot shoot)
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double playerX = ShadowAliens.getScreenWidth() / 2;
        double playerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int playerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initialLives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        int hitInvincibilityTime = Integer.parseInt(gameProps.getProperty("player.hitInvincibilityTime"));
        Image projectileImage = new Image(gameProps.getProperty("projectile.image"));
        double projectileSpeed = Double.parseDouble(gameProps.getProperty("projectile.movementSpeed"));
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime, projectileImage, projectileSpeed);

        // load text settings
        String fontPath = gameProps.getProperty("text.font");
        int defaultSize = Integer.parseInt(gameProps.getProperty("text.size"));
        int titleSize = Integer.parseInt(gameProps.getProperty("start.title.size"));

        titleFont = new Font(fontPath, titleSize);
        textFont = new Font(fontPath, defaultSize);

        titleText = gameProps.getProperty("start.title.text");
        titlePosY = Double.parseDouble(gameProps.getProperty("start.title.posY"));

        instructionsStartPosY = Double.parseDouble(gameProps.getProperty("start.instructionsList.startPosY"));
        instructionsRowGap = Double.parseDouble(gameProps.getProperty("start.instructionsList.rowGap"));
        instructionsList = new ArrayList<>(Arrays.asList(
                gameProps.getProperty("start.instructionsList.text").split(",")));

        // text colour
        String[] colours = gameProps.getProperty("text.colour").split(",");
        textColour = new DrawOptions().setBlendColour(
                Double.parseDouble(colours[0]),
                Double.parseDouble(colours[1]),
                Double.parseDouble(colours[2]), 1);
    }

    @Override
    public void update(Input input) {
        // player can move left/right but NOT shoot (use moveOnly)
        player.movement(input, 1.0);
        draw();
    }

    @Override
    public void draw() {
        // draw player
        player.draw();

        // draw title (centred)
        double titleX = ShadowAliens.getScreenWidth() / 2 - titleFont.getWidth(titleText) / 2;
        titleFont.drawString(titleText, titleX, titlePosY, textColour);

        // draw instructions list (centred)
        for (int i = 0; i < instructionsList.size(); i++) {
            String line = instructionsList.get(i);
            double x = ShadowAliens.getScreenWidth() / 2 - textFont.getWidth(line) / 2;
            double y = instructionsStartPosY + i * instructionsRowGap;
            textFont.drawString(line, x, y, textColour);
        }
    }
}

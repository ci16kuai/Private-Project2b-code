package game;

import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.Window;

import java.util.Properties;


/**
 * Main game class that manages initialising the screens and game objects
 */
public class ShadowAliens extends AbstractGame {
    private static Properties gameProps;
    private static double screenWidth;
    private static double screenHeight;
    private static UI ui;


    public BattleScreen battleScreen;
    public PauseScreen pauseScreen;
    public Screen currentScreen;
    private StartScreen startScreen;
    private EndScreen endScreen;

    public ShadowAliens(Properties gameProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                "Shadow Aliens");

        ShadowAliens.gameProps = gameProps;
        screenWidth = Integer.parseInt(gameProps.getProperty("window.width"));
        screenHeight = Integer.parseInt(gameProps.getProperty("window.height"));

        String[] backgroundColors = gameProps.getProperty("background.colour").split(",");
        double r = Double.parseDouble(backgroundColors[0]);
        double g = Double.parseDouble(backgroundColors[1]);
        double b = Double.parseDouble(backgroundColors[2]);
        Window.setClearColour(r, g, b);

        // UI is instantiated once here and shared via static getter
        ui = new UI(gameProps);

        battleScreen = new BattleScreen(gameProps);
        pauseScreen = new PauseScreen(gameProps, battleScreen);
        startScreen = new StartScreen(gameProps);
        currentScreen = startScreen;
    }

    /**
     * Run and render the next frame.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        currentScreen.update(input);

        // Change current screen to endScreen
        if (currentScreen instanceof BattleScreen) {
            if (battleScreen.isGameOver()) {
                endScreen = new EndScreen(gameProps, false);
                currentScreen = endScreen;
                return;
            }

            if (battleScreen.isGameWon()) {
                endScreen = new EndScreen(gameProps, true);
                currentScreen = endScreen;
                return;
            }
        }

        switchMode(input);

        // I: Invincible
        if (input.wasPressed(Keys.I)) {
            battleScreen.switchInv();
        }

        // R: reset
        if (input.wasPressed(Keys.R)) {
            resetGame();
        }

        // G: speed up
        if (input.wasPressed(Keys.G)) {
            battleScreen.speedUp();
        }

        // F: speed down
        if (input.wasPressed(Keys.F)) {
            battleScreen.speedDown();
        }

        // N: Skip current wave
        if (input.wasPressed(Keys.N)) {
            if (currentScreen instanceof BattleScreen || currentScreen instanceof PauseScreen) {
                battleScreen.skipWave();
            }
        }
    }

    public static void main(String[] args) {
        Properties props = IOUtils.readPropertiesFile(System.getProperty("gameData", "gameData.properties"));
        ShadowAliens game = new ShadowAliens(props);
        game.run();
    }

    public void switchMode(Input input) {
        // Start → Battle
        if (input.wasPressed(Keys.SPACE) && currentScreen instanceof StartScreen) {
            currentScreen = battleScreen;
        }

        // Battle ↔ Pause
        if (input.wasPressed(Keys.ESCAPE)) {
            if (currentScreen instanceof BattleScreen) {
                currentScreen = pauseScreen;
            } else if (currentScreen instanceof PauseScreen) {
                currentScreen = battleScreen;
            }
        }

        // End → new Battle
        if (currentScreen instanceof EndScreen && ((EndScreen) currentScreen).shouldRestart()) {
            battleScreen = new BattleScreen(gameProps);
            pauseScreen = new PauseScreen(gameProps, battleScreen);
            currentScreen = battleScreen;
        }
    }

    private void resetGame() {
        battleScreen = new BattleScreen(gameProps);
        pauseScreen = new PauseScreen(gameProps, battleScreen);
        startScreen = new StartScreen(gameProps);
        currentScreen = startScreen;
    }

    public static double getScreenWidth() {
        return screenWidth;
    }

    public static double getScreenHeight() {
        return screenHeight;
    }

    public static UI getUI() {
        return ui;
    }
}

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
    public static double screenWidth;
    public static double screenHeight;

    public BattleScreen battleScreen;
    public PauseScreen pauseScreen;
    public Screen currentScreen;
    private UI ui;

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
        Window.setClearColour(r,g,b);

        battleScreen = new BattleScreen(gameProps);
        pauseScreen = new PauseScreen(gameProps, battleScreen);
        currentScreen = battleScreen;
        ui = new UI(gameProps);

    }

    /**
     * Run and render the next frame.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        currentScreen.update(input);
        switch_mode(input);
        //if the game is paused, draw pause UI
        if (currentScreen instanceof PauseScreen){
            ui.draw_pause(battleScreen.calTimeScale());
        }

        //I: Dev mode
        if (input.wasPressed(Keys.I)){
            battleScreen.switchDev();
        }

        //R: reset
        if (input.wasPressed(Keys.R)){
            resetGame();
        }

        //G: speedUp
        if (input.wasPressed(Keys.G)){
            battleScreen.speedup();
        }

        //F: speedDown
        if (input.wasPressed(Keys.F)){
            battleScreen.speedDown();
        }
    }


    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile(System.getProperty("gameData","gameData.properties"));
        ShadowAliens game = new ShadowAliens(gameProps);
        game.run();
    }

    public void switch_mode(Input input){

        //switch Screen if  ESC is pressed;
        if (input.wasPressed(Keys.ESCAPE)){
            if (currentScreen instanceof BattleScreen){
                currentScreen = pauseScreen;
            }else if (currentScreen instanceof PauseScreen){
                currentScreen = battleScreen;
            }
        }
    }

    private void resetGame(){
        battleScreen = new BattleScreen(gameProps);
        pauseScreen = new PauseScreen(gameProps, battleScreen);
        currentScreen = battleScreen;
    }
}


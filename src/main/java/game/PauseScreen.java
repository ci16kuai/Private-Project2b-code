package game;

import bagel.Input;

import java.util.Properties;

public class PauseScreen extends Screen {

    private BattleScreen battleScreen;

    public PauseScreen(Properties gameProps, BattleScreen battleScreen) {
        super(gameProps);
        this.battleScreen = battleScreen;
    }

    @Override
    public void update(Input input) {
        draw();
    }

    @Override
    public void draw() {
        // the pause screen shows the battle scene frozen underneath the pause overlay
        battleScreen.draw();
    }
}

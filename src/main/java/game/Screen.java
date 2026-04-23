package game;

import bagel.Input;

import java.util.Properties;

public abstract class Screen {

    protected Properties gameProps;

    public Screen(Properties gameProps) {
        this.gameProps = gameProps;
    }

    public abstract void update(Input input);
    public abstract void draw();
}

package game;

import bagel.Image;

public class EnginePowerup extends Powerup {

    private int speedMultiplier;

    public EnginePowerup(double x, double y, Image image, double speed, int duration) {
        super(x, y, image, speed, duration);
    }

    @Override
    public void apply(Player player) {
        player.activatePowerup("engine", duration);
    }
}

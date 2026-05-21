package game;

import bagel.Image;

public class CooldownPowerup extends Powerup {

    public CooldownPowerup(double x, double y, Image image, double speed, int duration, int arrivalTime) {
        super(x, y, image, speed, duration, arrivalTime);
    }

    @Override
    public void apply(Player player) {
        player.activatePowerup("cooldown", duration);
    }
}

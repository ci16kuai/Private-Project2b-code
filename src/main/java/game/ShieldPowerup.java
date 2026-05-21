package game;

import bagel.Image;

public class ShieldPowerup extends Powerup {

    public ShieldPowerup(double x, double y, Image image, double speed, int duration, int arrivalTime) {
        super(x, y, image, speed, duration, arrivalTime);
    }

    public void apply(Player player) {
        player.activatePowerup("shield", duration);
    }
}

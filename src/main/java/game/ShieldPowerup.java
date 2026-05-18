package game;

import bagel.Image;

public class ShieldPowerup extends Powerup {

    public ShieldPowerup(double x, double y, Image image, double speed, int duration) {
        super(x, y, image, speed, duration);
    }

    public void apply(Player player) {
        player.activatePowerup("shield", duration);
    }
}

package game;

import bagel.Image;

public class CooldownPowerup extends Powerup {

    private int reducedCooldown;

    public CooldownPowerup(double x, double y, Image image, double speed, int duration) {
        super(x, y, image, speed, duration);
    }

    @Override
    public void apply(Player player) {
        player.activatePowerup("cooldown", duration);
    }
}

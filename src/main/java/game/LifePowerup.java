package game;

import bagel.Image;

public class LifePowerup extends Powerup {

    private int livesAdded;

    public LifePowerup(double x, double y, Image image, double speed, int duration, int arrivalTime) {
        super(x, y, image, speed, duration, arrivalTime);
    }

    @Override
    public void apply(Player player) {
        player.gainLife();
    }
}

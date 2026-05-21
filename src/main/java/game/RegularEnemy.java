package game;

import bagel.DrawOptions;
import bagel.Image;

public class RegularEnemy extends Enemy {

    public RegularEnemy(double x, double y, Image image, int speed, int arrivalTime) {
        super(x, y, image, speed, arrivalTime);
    }

    @Override
    public void update(double frameCount, double timeScale) {
        // if the enemy has not arrived yet, return
        if (frameCount < arrivalTime) {
            return;
        }
        if (!hasArrived(frameCount)) {
            return;
        }
        y += speed * timeScale;
        // if enemy is outside screen, deactivate
        if (y >= ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }
}

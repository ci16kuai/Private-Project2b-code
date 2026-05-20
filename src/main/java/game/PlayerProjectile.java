package game;

import bagel.Image;

public class PlayerProjectile extends Projectile {

    public PlayerProjectile(double x, double y, Image image, double speed) {
        super(x, y, image, speed);
    }

    @Override
    public void update(double timeScale) {
        y -= speed * timeScale;
        // if projectile goes off the top of screen, deactivate
        if (y < 0 - image.getHeight() / 2) {
            deactive();
        }
    }
}

package game;

import bagel.Image;

public class EnemyProjectile extends Projectile {

    public EnemyProjectile(double x, double y, Image image, double speed) {
        super(x, y, image, speed);
    }

    @Override
    public void update(double timeScale) {
        y += speed * timeScale;
        // if projectile goes off the bottom of screen, deactivate
        if (y > ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }
}

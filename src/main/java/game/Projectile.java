package game;

import bagel.Image;

public class Projectile extends GameObject {

    private final double speed;

    public Projectile(double x, double y, Image image, double speed) {
        super(x, y, image);
        this.speed = speed;
    }

    public void update(double timeScale) {
        y -= speed * timeScale;

        // if projectile is outside screen, deactivate
        if (y < 0 - image.getHeight() / 2) {
            deactive();
        }
    }
}

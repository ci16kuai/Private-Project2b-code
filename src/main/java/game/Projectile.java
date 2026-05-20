package game;

import bagel.Image;

public abstract class Projectile extends GameObject {

    protected double speed;

    public Projectile(double x, double y, Image image, double speed) {
        super(x, y, image);
        this.speed = speed;
    }

    public abstract void update(double timeScale);
}

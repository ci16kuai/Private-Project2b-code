package game;

import bagel.Image;

public abstract class Powerup extends GameObject {

    protected double speed;
    protected int duration;

    public Powerup(double x, double y, Image image, double speed, int duration) {
        super(x, y, image);
        this.speed = speed;
        this.duration = duration;
    }

    public void update(double timeScale) {
        y += speed * timeScale;

    }

    public abstract void apply(Player player);
}

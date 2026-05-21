package game;

import bagel.Image;

public abstract class Powerup extends GameObject implements Moveable {

    protected double speed;
    protected int duration;

    public Powerup(double x, double y, Image image, double speed, int duration) {
        super(x, y, image);
        this.speed = speed;
        this.duration = duration;
    }

    @Override
    public void move(double timeScale) {
        y += speed * timeScale;
    }

    public void update(double timeScale) {
        move(timeScale);

        if (y >= ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }

    public abstract void apply(Player player);
}

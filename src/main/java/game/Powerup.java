package game;

import bagel.Image;

public abstract class Powerup extends GameObject implements Moveable {

    protected double speed;
    protected int duration;
    protected int arrivalTime;

    public Powerup(double x, double y, Image image, double speed, int duration, int arrivalTime) {
        super(x, y, image);
        this.speed = speed;
        this.duration = duration;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public void move(double timeScale) {
        y += speed * timeScale;
    }

    public void update(double frameCount, double timeScale) {
        if (!hasArrived(frameCount)) {
            return;
        }

        move(timeScale);

        if (y >= ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }

    public boolean hasArrived(double frameCount) {
        return frameCount >= arrivalTime;
    }

    public abstract void apply(Player player);
}

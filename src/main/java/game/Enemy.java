package game;

import bagel.DrawOptions;
import bagel.Image;

public abstract class Enemy extends GameObject {

    public int speed;
    protected int arrivalTime;

    public Enemy(double x, double y, Image image, int speed, int arrivalTime) {
        super(x, y, image);
        this.speed = speed;
        this.arrivalTime = arrivalTime;
    }

    public abstract void update(double frameCount, double timeScale);

    @Override
    public void draw() {
        // rotate 180 degrees so the enemy ship faces downward
        image.draw(x, y, new DrawOptions().setRotation(Math.PI/2));
    }
}

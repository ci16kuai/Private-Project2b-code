package game;

import bagel.DrawOptions;
import bagel.Image;

public class Enemy extends GameObject {

    public int speed;
    protected int arrivalTime;

    public Enemy(double x, double y, Image image, int speed, int arrivalTime) {
        super(x, y, image);
        this.speed = speed;
        this.arrivalTime = arrivalTime;
    }

    public void update(double frameCount, double timeScale) {
        // if the enemy has not arrived yet, return
        if (frameCount < arrivalTime) {
            return;
        } else {
            // apply timeScale so game speed changes affect enemy movement
            y += speed * timeScale;
        }

        // if enemy is outside screen, deactivate
        if (y >= ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }

    @Override
    public void draw() {
        // rotate 180 degrees so the enemy ship faces downward
        image.draw(x, y, new DrawOptions().setRotation(Math.PI));
    }
}

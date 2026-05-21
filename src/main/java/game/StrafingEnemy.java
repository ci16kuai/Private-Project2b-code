package game;

import bagel.DrawOptions;
import bagel.Image;

public class StrafingEnemy extends Enemy {

    private int horizontalDirection; // 1 = right, -1 = left

    public StrafingEnemy(double x, double y, Image image, int speed, int arrivalTime) {
        super(x, y, image, speed, arrivalTime);
        // start moving toward nearest edge
        if (x < ShadowAliens.getScreenWidth() / 2) {
            horizontalDirection = -1; // closer to left edge, move left
        } else {
            horizontalDirection = 1;  // closer to right edge, move right
        }
    }

    @Override
    public void update(double frameCount, double timeScale) {
        if (frameCount < arrivalTime) {
            return;
        }
        // move down
        y += speed * timeScale;

        // move horizontally
        x += speed * horizontalDirection * timeScale;

        // bounce off edges (no pixel off screen)
        if (x <= image.getWidth() / 2) {
            x = image.getWidth() / 2;
            horizontalDirection = 1;
        } else if (x >= ShadowAliens.getScreenWidth() - image.getWidth() / 2) {
            x = ShadowAliens.getScreenWidth() - image.getWidth() / 2;
            horizontalDirection = -1;
        }

        if (y >= ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }

    @Override
    public void draw() {
        image.draw(x, y, new DrawOptions().setRotation(Math.PI/2));
    }
}

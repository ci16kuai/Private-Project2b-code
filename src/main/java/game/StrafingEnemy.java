package game;

import bagel.Image;

public class StrafingEnemy extends Enemy {

    // Direction of horizontal movement: 1 = right, -1 = left
    private int horizontalDirection;

    public StrafingEnemy(double x, double y, Image image, int speed, int arrivalTime) {
        super(x, y, image, speed, arrivalTime);

        // Start moving toward the nearest side of the screen.
        if (x < ShadowAliens.getScreenWidth() / 2) {
            horizontalDirection = -1;
        } else {
            horizontalDirection = 1;
        }
    }

    @Override
    public void update(double frameCount, double timeScale) {
        if (!hasArrived(frameCount)) {
            return;
        }

        y += speed * timeScale;
        x += speed * horizontalDirection * timeScale;

        // Reverse direction when reaching a screen edge.
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
}
package game;

import bagel.DrawOptions;
import bagel.Image;

public class ShootingEnemy extends Enemy implements Shootable {

    private int firingRate;
    private int cooldownLeft;
    private Image projectileImage;
    private double projectileSpeed;

    public ShootingEnemy(double x, double y, Image image, int speed, int arrivalTime,
                         int firingRate, Image projectileImage, double projectileSpeed) {
        super(x, y, image, speed, arrivalTime);
        this.firingRate = firingRate;
        this.cooldownLeft = firingRate;
        this.projectileImage = projectileImage;
        this.projectileSpeed = projectileSpeed;
    }

    @Override
    public void update(double frameCount, double timeScale) {
        if (frameCount < arrivalTime) {
            return;
        }
        if (!hasArrived(frameCount)) {
            return;
        }
        y += speed * timeScale;
        updateCooldown();
        // if enemy is outside screen, deactivate
        if (y >= ShadowAliens.getScreenHeight() + image.getHeight() / 2) {
            deactive();
        }
    }

    @Override
    public void updateCooldown() {
        if (cooldownLeft > 0) {
            cooldownLeft--;
        }
    }

    @Override
    public boolean canShoot() {
        return cooldownLeft == 0;
    }

    @Override
    public EnemyProjectile shoot() {
        cooldownLeft = firingRate;
        return new EnemyProjectile(x, y + image.getHeight() / 2, projectileImage, projectileSpeed);
    }

}

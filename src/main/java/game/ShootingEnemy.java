package game;

import bagel.DrawOptions;
import bagel.Image;

public class ShootingEnemy extends Enemy implements Shootable {

    private int firingRate;
    private int cooldownLeft;

    public ShootingEnemy(double x, double y, Image image, int speed, int arrivalTime, int firingRate) {
        super(x, y, image, speed, arrivalTime);
        this.firingRate = firingRate;
        this.cooldownLeft = firingRate; // first shot at arrivalTime + firingRate
    }

    @Override
    public void update(double frameCount, double timeScale) {
        if (frameCount < arrivalTime) {
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
    public Projectile shoot() {
        cooldownLeft = firingRate;
        // EnemyProjectile 创建后替换这里的 null
        return null;
    }

    @Override
    public void draw() {
        image.draw(x, y, new DrawOptions().setRotation(Math.PI));
    }
}

package game;

import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Player extends GameObject implements Shootable {

    private int baseSpeed;
    private int currentSpeed;
    private int initialLives;
    private int lives;

    private int baseShootCooldown;
    private int currentShootCooldown;
    private int coolDownLeft = 0;

    private int powerupDurationLeft = 0;
    private String activePowerup = "";
    private boolean devInvincible = false;
    private int hitInvincibilityTime;
    private int hitInvincibilityLeft = 0;

    private Image projectileImage;
    private double projectileSpeed;

    private Image invincibilityImage;

    public Player(double x, double y, Image image, int speed, int lives, int shootCooldown, int hitInvincibilityTime,
                  Image projectileImage, double projectileSpeed, Image invincibilityImage) {
        super(x, y, image);
        this.baseSpeed = speed;
        this.currentSpeed = speed;
        this.lives = lives;
        this.initialLives = lives;
        this.baseShootCooldown = shootCooldown;
        this.currentShootCooldown = shootCooldown;
        this.hitInvincibilityTime = hitInvincibilityTime;
        this.projectileImage = projectileImage;
        this.projectileSpeed = projectileSpeed;
        this.invincibilityImage = invincibilityImage;
    }

    public PlayerProjectile update(Input input, double timeScale) {
        movement(input, timeScale);
        updateCooldown();
        updatePowerup(timeScale);
        return tryShoot(input);
    }

    public void movement(Input input, double timeScale) {
        if ((input.wasPressed(Keys.A) || input.isDown(Keys.A))) {
            x -= currentSpeed * timeScale;
            if (x < image.getWidth() / 2) {
                x = image.getWidth() / 2;
            }
        }
        if ((input.wasPressed(Keys.D) || input.isDown(Keys.D))) {
            x += currentSpeed * timeScale;
            if (x >= ShadowAliens.getScreenWidth() - image.getWidth() / 2 - 1) {
                x = ShadowAliens.getScreenWidth() - image.getWidth() / 2 - 1;
            }
        }
    }

    public void updateCooldown() {
        if (coolDownLeft > 0) {
            coolDownLeft--;
        }
    }

    public PlayerProjectile tryShoot(Input input) {
        if (input.wasPressed(Keys.SPACE) && canShoot()) {
            return shoot();
        }
        return null;
    }

    @Override
    public PlayerProjectile shoot() {
        coolDownLeft = currentShootCooldown;
        return new PlayerProjectile(x, y - image.getHeight() / 2, projectileImage, projectileSpeed);
    }

    public void activatePowerup(String type, int duration) {
        this.activePowerup = type;
        this.powerupDurationLeft = duration;
        if (type.equals("engine")) {
            currentSpeed = baseSpeed * 2;
        } else if (type.equals("cooldown")) {
            currentShootCooldown = Math.max(1, baseShootCooldown / 3);
        }
    }

    public void updatePowerup(double timeScale) {
        if (powerupDurationLeft > 0) {
            powerupDurationLeft -= timeScale;
            if (powerupDurationLeft <= 0) {
                // 恢复 powerup 效果
                if (activePowerup.equals("engine")) {
                    currentSpeed = baseSpeed;
                } else if (activePowerup.equals("cooldown")) {
                    currentShootCooldown = baseShootCooldown;
                }
                activePowerup = "";
            }
        }
        if (hitInvincibilityLeft > 0) {
            hitInvincibilityLeft -= timeScale;
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (isInvincible()) {
            invincibilityImage.draw(x, y);
        }
    }

    public boolean isInvincible() {
        return (powerupDurationLeft > 0 && activePowerup.equals("shield"))
                || devInvincible
                || hitInvincibilityLeft > 0;
    }

    public void loseLife() {
        lives -= 1;
        hitInvincibilityLeft = hitInvincibilityTime;
    }

    public void gainLife() {
        if (lives < initialLives) {
            lives++;
        }
    }

    public int getLives() {
        return lives;
    }

    public void setDevInvincible(boolean value) {
        devInvincible = value;
    }

    public boolean canShoot() {
        return coolDownLeft == 0;
    }
}

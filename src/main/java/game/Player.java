package game;

import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Player extends GameObject {

    private int speed;
    private int lives;
    private int shootCooldown;
    private int coolDownLeft = 0;
    private boolean canShoot = false;

    public Player(double x, double y, Image image, int speed, int lives, int shootCooldown) {
        super(x, y, image);
        this.speed = speed;
        this.lives = lives;
        this.shootCooldown = shootCooldown;
    }

    public boolean update(Input input, double timeScale) {
        movement(input, timeScale);
        updateCooldown();
        return shoot(input);
    }

    public void movement(Input input, double timeScale) {
        // move left if press or hold A
        if ((input.wasPressed(Keys.A) || input.isDown(Keys.A))) {
            x -= speed * timeScale;
            if (x < 0 + image.getWidth() / 2) {
                x = 0 + image.getWidth() / 2;
            }
        }

        // move right if press or hold D
        if ((input.wasPressed(Keys.D) || input.isDown(Keys.D))) {
            x += speed * timeScale;
            if (x >= ShadowAliens.getScreenWidth() - image.getWidth() / 2 - 1) {
                x = ShadowAliens.getScreenWidth() - (image.getWidth() / 2) - 1;
            }
        }
    }

    public void updateCooldown() {
        if (coolDownLeft > 0) {
            coolDownLeft--;
        }
        canShoot = (coolDownLeft == 0);
    }

    public boolean shoot(Input input) {
        if (input.wasPressed(Keys.SPACE) && canShoot) {
            canShoot = false;
            coolDownLeft = shootCooldown;
            return true;
        }
        return false;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives -= 1;
    }
}

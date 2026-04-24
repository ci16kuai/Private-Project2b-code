package game;

import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Player extends Object{

    public int speed;
    public int lives;
    public int shootCooldown;
    public int coolDownLeft = 0;
    public boolean canShoot = false;

    public Player(double x, double y, Image image, int speed, int lives, int  shootCooldown) {
        super(x, y, image);
        this.speed = speed;
        this.lives = lives;
        this.shootCooldown = shootCooldown;
    }

    public boolean update(Input input){
        // press move button:
        movement(input);

        // press space button:
        updateCooldown();
        return shoot(input);
    }

    public void movement(Input input){
        if ((input.wasPressed(Keys.A) || input.isDown(Keys.A))){
            x  -= speed;
            if (x < 0 + image.getWidth()/2){
                x = 0  + image.getWidth()/2;
            }
        }
        if ((input.wasPressed(Keys.D) || input.isDown(Keys.D))){
            x += speed;
            if (x >= ShadowAliens.screenWidth - image.getWidth()/2 - 1) {
                x = ShadowAliens.screenWidth - (image.getWidth() / 2) - 1;
            }
        }
    }

    public void updateCooldown(){
            if (coolDownLeft > 0){
                coolDownLeft--;
            }
            canShoot = (coolDownLeft == 0);
    }

    public boolean shoot(Input input){
        // if pressed SPACE:
        if (input.wasPressed(Keys.SPACE) && canShoot){
            //reset cooldown and cannot shoot;
            canShoot = false;
            coolDownLeft  = shootCooldown;
            return true;
        }
        return false;
    }

    // get the current player lives
    public int getLives() {
        return lives;
    }

}

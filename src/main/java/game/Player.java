package game;

import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Player extends Object{

    public int speed;
//    public int lives;
//    public int shootCooldown;
//    public int cooldownTime;

    public Player(double x, double y, Image image, int speed) {
        super(x, y, image);
        this.speed = speed;
//        this.lives = lives;
//        this.shootCooldown = shootCooldown;
//        this.cooldownTime = cooldownTime;
    }

    public void update(Input input){
        movement(input);
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
}

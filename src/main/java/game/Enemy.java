package game;

import bagel.Image;
import bagel.Input;

public class Enemy extends Object{

    private int speed;
    private int arrivalTime;

    public Enemy(double x, double y, Image image, int speed, int arrivalTime) {
        super(x, y, image);
        this.speed = speed;
        this.arrivalTime = arrivalTime;
    }

    public void update(int frameCount){
        // if the enemy is not arrived, return
        if (frameCount < arrivalTime){
            return;
        } else{  //if  arrived
            y += speed;
        }

        // if enemy is outside screen:
        if (y >= ShadowAliens.screenHeight + image.getHeight()/2){
            active = false;
        }
    }

}

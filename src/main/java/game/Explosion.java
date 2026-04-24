package game;

import bagel.Image;

public class Explosion extends Object{

    private int explosionDuration;
    private int timer;

    public Explosion(double x, double y, Image explosionImage, int explosionDuration) {
        super(x, y, explosionImage);
        this.explosionDuration = explosionDuration;
        timer = explosionDuration;
    }

    public void update(){
        timer --;
        if (timer <= 0){
            deactive();
        }
    }
}

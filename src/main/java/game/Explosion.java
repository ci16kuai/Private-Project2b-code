package game;

import bagel.Image;

public class Explosion extends Object{

    private int explosionDuration;
    private double timer;

    public Explosion(double x, double y, Image explosionImage, int explosionDuration) {
        super(x, y, explosionImage);
        this.explosionDuration = explosionDuration;
        timer = explosionDuration;
    }

    public void update(double timeScale){

        timer = timer - (1 * timeScale);
        if (timer <= 0){
            deactive();
        }
    }
}

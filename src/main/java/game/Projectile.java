package game;

import bagel.Image;

public class Projectile extends Object{

    private final double speed;

    public Projectile(double x, double y, Image image, double speed) {
        super(x, y, image);
        this.speed = speed;
    }

    public void update(double timesScale){
        y -= speed * timesScale;

        // if projectile is outside screen:
        if(y < 0 - image.getHeight()/2){
            deactive();
        }
    }
}

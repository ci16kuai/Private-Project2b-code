package game;

import bagel.Image;

public abstract class Object {

    public Image image;
    public double x;
    public double y;
    protected boolean active;

    public Object(double x, double y, Image image) {
        this.active = true;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void draw(){
        image.draw(x,y);
    }

    public boolean isActive() {
        return active;
    }

    public void deactive(){
        active = false;
    }
}

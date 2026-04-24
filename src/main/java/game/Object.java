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

    // identify whether one object collides with one other *AI generated
    public boolean collidesWith(Object other){
        double dx = Math.abs(this.x - other.x);
        double dy = Math.abs(this.y - other.y);

        double allowedX = (this.image.getWidth() + other.image.getWidth()) / 2;
        double allowedY = (this.image.getHeight() + other.image.getHeight()) / 2;

        return dx < allowedX && dy < allowedY;
    }
}

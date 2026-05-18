package game;

import bagel.Image;

public abstract class GameObject {

    protected Image image;
    protected double x;
    protected double y;
    private boolean active;

    public GameObject(double x, double y, Image image) {
        this.active = true;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public void draw() {
        image.draw(x, y);
    }

    public boolean isActive() {
        return active;
    }

    public void deactive() {
        active = false;
    }

    // identify whether one object collides with another (AI generated)
    public boolean collidesWith(GameObject other) {
        double dx = Math.abs(this.x - other.x);
        double dy = Math.abs(this.y - other.y);

        double allowedX = (this.image.getWidth() + other.image.getWidth()) / 2;
        double allowedY = (this.image.getHeight() + other.image.getHeight()) / 2;

        return dx < allowedX && dy < allowedY;
    }
}

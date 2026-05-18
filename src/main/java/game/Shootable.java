package game;

public interface Shootable {

    public void updateCooldown();
    public boolean canShoot();
    public Projectile shoot();

}
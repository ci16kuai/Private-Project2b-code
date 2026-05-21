package game;

import bagel.Image;
import java.util.ArrayList;
import java.util.Properties;

public class Wave {

    private Properties gameProps;
    private int waveNumber;
    private ArrayList<Enemy> enemies;
    private ArrayList<Powerup> powerups;
    private double frameCount;

    public Wave(Properties gameProps, int waveNumber) {
        this.gameProps = gameProps;
        this.waveNumber = waveNumber;
        this.enemies = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.frameCount = 0;
        loadEnemies();
        loadPowerups();
    }

    private void loadEnemies() {
        int firingRate = Integer.parseInt(gameProps.getProperty("enemy.shooting.firingRate"));
        Image enemyProjectileImage = new Image(gameProps.getProperty("enemyProjectile.image"));
        double enemyProjectileSpeed = Double.parseDouble(gameProps.getProperty("enemyProjectile.movementSpeed"));

        int i = 0;
        while (gameProps.getProperty(String.format("wave.%d.enemy.%d.type", waveNumber, i)) != null) {
            String type = gameProps.getProperty(String.format("wave.%d.enemy.%d.type", waveNumber, i));
            int arrivalTime = Integer.parseInt(gameProps.getProperty(String.format("wave.%d.enemy.%d.arrivalTime", waveNumber, i)));
            int speed = Integer.parseInt(gameProps.getProperty(String.format("wave.%d.enemy.%d.movementSpeed", waveNumber, i)));
            double posX = Double.parseDouble(gameProps.getProperty(String.format("wave.%d.enemy.%d.posX", waveNumber, i)));
            Image image = new Image(gameProps.getProperty("enemy." + type + ".image"));
            double startY = 0 - image.getHeight() / 2;

            Enemy enemy = switch (type) {
                case "regular"  -> new RegularEnemy(posX, startY, image, speed, arrivalTime);
                case "strafing" -> new StrafingEnemy(posX, startY, image, speed, arrivalTime);
                case "shooting" -> new ShootingEnemy(posX, startY, image, speed, arrivalTime,
                        firingRate, enemyProjectileImage, enemyProjectileSpeed);
                default -> null;
            };

            if (enemy != null) {
                enemies.add(enemy);
            }
            i++;
        }
    }

    private void loadPowerups() {
        int j = 0;
        while (gameProps.getProperty(String.format("wave.%d.powerup.%d.type", waveNumber, j)) != null) {
            String type = gameProps.getProperty(String.format("wave.%d.powerup.%d.type", waveNumber, j));
            double posX = Double.parseDouble(gameProps.getProperty(String.format("wave.%d.powerup.%d.posX", waveNumber, j)));
            Image image = new Image(gameProps.getProperty("powerup." + type + ".image"));
            double speed = Double.parseDouble(gameProps.getProperty("powerup." + type + ".movementSpeed"));
            String durationStr = gameProps.getProperty("powerup." + type + ".duration");
            int duration = (durationStr != null) ? Integer.parseInt(durationStr) : 0;
            int arrivalTime = Integer.parseInt(gameProps.getProperty(
                    String.format("wave.%d.powerup.%d.arrivalTime", waveNumber, j)));

            Powerup p = switch (type) {
                case "shield"   -> new ShieldPowerup(posX, 0, image, speed, duration, arrivalTime);
                case "life"     -> new LifePowerup(posX, 0, image, speed, duration, arrivalTime);
                case "cooldown" -> new CooldownPowerup(posX, 0, image, speed, duration, arrivalTime);
                case "engine"   -> new EnginePowerup(posX, 0, image, speed, duration, arrivalTime);
                default -> null;
            };

            if (p != null) {
                powerups.add(p);
            }
            j++;
        }
    }

    public void update(double timeScale) {
        frameCount+=timeScale;

        for (Enemy enemy : enemies) {
            enemy.update(frameCount, timeScale);
        }

        for (Powerup powerup : powerups) {
            powerup.update(frameCount, timeScale);
        }
    }

    public void draw() {
        for (Enemy enemy : enemies) {
            if (enemy.hasArrived(frameCount)) {
                enemy.draw();
            }
        }
        for (Powerup powerup : powerups) {
            if (powerup.hasArrived(frameCount)) {
                powerup.draw();
            }
        }
    }

    public void deleteInactiveEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            if (!enemies.get(i).isActive()) {
                enemies.remove(i);
                i--;
            }
        }
        for (int i = 0; i < powerups.size(); i++) {
            if (!powerups.get(i).isActive()) {
                powerups.remove(i);
                i--;
            }
        }
    }

    public void clearObjects() {
        enemies.clear();
        powerups.clear();
    }

    public boolean isCompleted() {
        return enemies.isEmpty() && powerups.isEmpty();
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Powerup> getPowerups() {
        return powerups;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public double getFrameCount() {
        return frameCount;
    }
}

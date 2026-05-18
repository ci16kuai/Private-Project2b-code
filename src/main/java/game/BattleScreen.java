package game;

import bagel.Image;
import bagel.Input;
import bagel.Window;

import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;
    public ArrayList<Enemy> enemies;
    public ArrayList<Projectile> projectiles;
    public ArrayList<Explosion> explosions;
    public ArrayList<Powerup> powerups;

    // UI
    private int lives;
    private int wave = 1;
    private int score = 0;
    private double frameCount;
    public double timeScale = 1.0;

    private boolean InvMode = false;
    private int speedLevel = 0;

    public BattleScreen(Properties gameProps) {
        super(gameProps);

        // initialize battle screen objects
        initialiseObjects();
    }

    @Override
    public void update(Input input) {
        // update player status
        double currentTimeScale = calTimeScale();
        if (player.update(input, currentTimeScale)) {  // returned true = player pressed SPACE
            Projectile projectile = new Projectile(
                    player.getX(),
                    player.getY(),
                    new Image(gameProps.getProperty("projectile.image")),
                    Double.parseDouble(gameProps.getProperty("projectile.movementSpeed")));
            projectiles.add(projectile);
        }
        // update enemy status (pass timeScale so speed changes affect enemies too)
        for (Enemy enemy : enemies) {
            enemy.update(frameCount, currentTimeScale);
        }

        // update projectiles
        for (Projectile projectile : projectiles) {
            projectile.update(currentTimeScale);
        }

        // update powerups
        for (Powerup powerup : powerups) {
            powerup.update(currentTimeScale);
        }

        // update explosions
        for (Explosion explosion : explosions) {
            explosion.update(currentTimeScale);
        }

        // check collision
        checkCollisions();

        // delete inactive objects
        deleteInactiveObjects();

        draw();
        frameCount += currentTimeScale;
    }


    @Override
    public void draw() {
        // draw player
        player.draw();

        // draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw();
        }

        // draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw();
        }

        // draw powerups
        for (Powerup powerup : powerups) {
            powerup.draw();
        }

        // draw explosions
        for (Explosion explosion : explosions) {
            explosion.draw();
        }

        // draw UI
        lives = player.getLives();
        ShadowAliens.getUI().draw(lives, wave, score);
    }

    public void initialiseObjects() {
        // initialize player
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double playerX = ShadowAliens.getScreenWidth() / 2;
        double playerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int playerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initialLives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        int hitInvincibilityTime = Integer.parseInt(gameProps.getProperty("player.hitInvincibilityTime"));
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime);

        // initialize projectile and explosions
        projectiles = new ArrayList<>();
        explosions = new ArrayList<>();

        // initialize enemy
        enemies = new ArrayList<>();
        Image enemyImage = new Image(gameProps.getProperty("enemy.image"));

        int i = 0;
        // read the enemy data until no enemies
        while ((gameProps.getProperty(String.format("enemy.%d.arrivalTime", i)) != null)) {
            int arrivalTime = Integer.parseInt(gameProps.getProperty(String.format("enemy.%d.arrivalTime", i)));
            int enemySpeed = Integer.parseInt(gameProps.getProperty(String.format("enemy.%d.movementSpeed", i)));
            double enemyX = Double.parseDouble(gameProps.getProperty(String.format("enemy.%d.posX", i)));

            Enemy enemy = new Enemy(enemyX, 0 - enemyImage.getHeight() / 2, enemyImage, enemySpeed, arrivalTime);
            enemies.add(enemy);
            i++;
        }

        int j = 0;
        while (gameProps.getProperty(String.format("wave.1.powerup.%d.type", j)) != null) {
            String type = gameProps.getProperty(String.format("wave.1.powerup.%d.type", j));
            int arrivalTime = Integer.parseInt(gameProps.getProperty(String.format("wave.1.powerup.%d.arrivalTime", j)));
            double posX = Double.parseDouble(gameProps.getProperty(String.format("wave.1.powerup.%d.posX", j)));

            Image image = new Image(gameProps.getProperty("powerup." + type + ".image"));
            double speed = Double.parseDouble(gameProps.getProperty("powerup." + type + ".movementSpeed"));
            String durationStr = gameProps.getProperty("powerup." + type + ".duration");
            int duration = (durationStr != null) ? Integer.parseInt(durationStr) : 0;

            Powerup p = switch (type) {
                case "shield" -> new ShieldPowerup(posX, 0, image, speed, duration);
                case "life" -> new LifePowerup(posX, 0, image, speed, duration);
                case "cooldown" -> new CooldownPowerup(posX, 0, image, speed, duration);
                case "engine" -> new EnginePowerup(posX, 0, image, speed, duration);
                default -> null;
            };

            if (p != null) {
                powerups.add(p);
            }
            j++;
        }
    }

    public void checkCollisions() {
        // check if player collides with enemies
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && (frameCount >= enemy.arrivalTime)) {
                if (enemy.collidesWith(player)) {
                    enemy.deactive();
                    if (!player.isInvincible()) {
                        player.loseLife();
                        score = Math.max(0, score - Integer.parseInt(gameProps.getProperty("score.gotHit")));
                    }
                    if (player.getLives() == 0) {
                        Window.close();
                    }
                }
            }
        }
        // check if enemies collide with projectiles
        for (Enemy enemy : enemies) {
            for (Projectile projectile : projectiles) {
                if (enemy.isActive() && (frameCount >= enemy.arrivalTime)) {
                    if (enemy.collidesWith(projectile)) {
                        enemy.deactive();
                        projectile.deactive();
                        score += 1;
                        Image explosionImage = new Image(gameProps.getProperty("explosion.image"));
                        int explosionDuration = Integer.parseInt(gameProps.getProperty("explosion.duration"));
                        Explosion explosion = new Explosion(enemy.getX(), enemy.getY(), explosionImage, explosionDuration);
                        explosions.add(explosion);
                    }
                }
            }
        }
        for (Powerup powerup : powerups) {
            if (powerup.isActive() && player.collidesWith(powerup)) {
                powerup.apply(player);
                powerup.deactive();
                score += Integer.parseInt(gameProps.getProperty("score.gotPowerup"));
            }
        }
    }

    public void deleteInactiveObjects() {
        for (int i = 0; i < enemies.size(); i++) {
            if (!enemies.get(i).isActive()) {
                enemies.remove(i);
                i--;
            }
        }
        for (int i = 0; i < projectiles.size(); i++) {
            if (!projectiles.get(i).isActive()) {
                projectiles.remove(i);
                i--;
            }
        }
        for (int i = 0; i < explosions.size(); i++) {
            if (!explosions.get(i).isActive()) {
                explosions.remove(i);
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

    public void speedUp() {
        speedLevel++;
    }

    public void speedDown() {
        speedLevel--;
    }

    // calculate the timeScale based on speedLevel
    public double calTimeScale() {
        if (speedLevel > 0) {
            return speedLevel + 1;
        }
        if (speedLevel < 0) {
            return 1.0 / (1 - speedLevel);
        }
        return 1.0;
    }

    public void switchInv() {
        InvMode = !InvMode;
        player.setDevInvincible(InvMode);
    }
}
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

    // UI
    private int lives;
    private int wave = 1;
    private int score = 0;
    private double frameCount;
    public double timeScale = 1.0;

    private boolean devMode = false;
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

        // draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw();
        }

        // draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw();
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
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown);

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
    }

    public void checkCollisions() {
        // check if player collides with enemies
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && (frameCount >= enemy.arrivalTime)) {
                if (enemy.collidesWith(player)) {
                    enemy.deactive();
                    if (!devMode) {
                        player.loseLife();
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

    public void switchDev() {
        devMode = !devMode;
    }
}

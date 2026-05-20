package game;

import bagel.Image;
import bagel.Input;
import bagel.Window;

import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;
    public ArrayList<Enemy> enemies;
    public ArrayList<PlayerProjectile> projectiles;
    public ArrayList<EnemyProjectile> enemyProjectiles;
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
        PlayerProjectile p = player.update(input, currentTimeScale);
        if (p != null) {
            projectiles.add(p);
        }

        // update enemy status (pass timeScale so speed changes affect enemies too)
        for (Enemy enemy : enemies) {
            enemy.update(frameCount, currentTimeScale);
            if (enemy instanceof ShootingEnemy se && se.canShoot()) {
                EnemyProjectile ep = se.shoot();
                if (ep != null) {
                    enemyProjectiles.add(ep);
                }
            }
        }

        // update player projectiles
        for (PlayerProjectile projectile : projectiles) {
            projectile.update(currentTimeScale);
        }
        // update enemy projectiles
        for (EnemyProjectile ep : enemyProjectiles) {
            ep.update(currentTimeScale);
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
        for (PlayerProjectile projectile : projectiles) {
            projectile.draw();
        }
        for (EnemyProjectile ep : enemyProjectiles) {
            ep.draw();
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
        // initialize lists
        projectiles = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        explosions = new ArrayList<>();   // 加在这里

        // initialize player
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double playerX = ShadowAliens.getScreenWidth() / 2;
        double playerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int playerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initialLives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        int hitInvincibilityTime = Integer.parseInt(gameProps.getProperty("player.hitInvincibilityTime"));
        Image projectileImage = new Image(gameProps.getProperty("projectile.image"));
        double projectileSpeed = Double.parseDouble(gameProps.getProperty("projectile.movementSpeed"));
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime, projectileImage, projectileSpeed );

        // initialize enemy
        enemies = new ArrayList<>();
        Image enemyProjectileImage = new Image(gameProps.getProperty("enemyProjectile.image"));
        double enemyProjectileSpeed = Double.parseDouble(gameProps.getProperty("enemyProjectile.movementSpeed"));
        int firingRate = Integer.parseInt(gameProps.getProperty("enemy.shooting.firingRate"));
        int i = 0;

        while (gameProps.getProperty(String.format("wave.1.enemy.%d.type", i)) != null) {
            String type = gameProps.getProperty(String.format("wave.1.enemy.%d.type", i));
            int arrivalTime = Integer.parseInt(gameProps.getProperty(String.format("wave.1.enemy.%d.arrivalTime", i)));
            int enemySpeed = Integer.parseInt(gameProps.getProperty(String.format("wave.1.enemy.%d.movementSpeed", i)));
            double enemyX = Double.parseDouble(gameProps.getProperty(String.format("wave.1.enemy.%d.posX", i)));
            Image enemyImage = new Image(gameProps.getProperty("enemy." + type + ".image"));
            double startY = 0 - enemyImage.getHeight() / 2;

            Enemy enemy = switch (type) {
                case "regular"  -> new RegularEnemy(enemyX, startY, enemyImage, enemySpeed, arrivalTime);
                case "strafing" -> new StrafingEnemy(enemyX, startY, enemyImage, enemySpeed, arrivalTime);
                case "shooting" -> new ShootingEnemy(enemyX, startY, enemyImage, enemySpeed, arrivalTime, firingRate, enemyProjectileImage, enemyProjectileSpeed);
                default -> null;
            };

            if (enemy != null) {
                enemies.add(enemy);
            }
            i++;
        }

        //initialize powerups
        powerups = new ArrayList<>();
        int j = 0;
        while (gameProps.getProperty(String.format("wave.1.powerup.%d.type", j)) != null) {
            String type = gameProps.getProperty(String.format("wave.1.powerup.%d.type", j));
            int arrivalTime = Integer.parseInt(gameProps.getProperty(String.format("wave.1.powerup.%d.arrivalTime", j)));
            double posX = Double.parseDouble(gameProps.getProperty(String.format("wave.1.powerup.%d.posX", j)));

            Image image = new Image(gameProps.getProperty("powerup." + type + ".image"));
            double speed = Double.parseDouble(gameProps.getProperty("powerup." + type + ".movementSpeed"));
            String durationStr = gameProps.getProperty("powerup." + type + ".duration");
            int duration = (durationStr != null) ? Integer.parseInt(durationStr) : 0;

            Powerup p;
            if (type.equals("shield")) {
                p = new ShieldPowerup(posX, 0, image, speed, duration);
            } else if (type.equals("life")) {
                p = new LifePowerup(posX, 0, image, speed, duration);
            } else if (type.equals("cooldown")) {
                p = new CooldownPowerup(posX, 0, image, speed, duration);
            } else if (type.equals("engine")) {
                p = new EnginePowerup(posX, 0, image, speed, duration);
            } else {
                p = null;
            }

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
                    // 加大爆炸
                    Image largeExplosionImage = new Image(gameProps.getProperty("explosion.large.image"));
                    int largeDuration = Integer.parseInt(gameProps.getProperty("explosion.large.duration"));
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), largeExplosionImage, largeDuration));
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
            for (PlayerProjectile projectile : projectiles) {
                if (enemy.isActive() && (frameCount >= enemy.arrivalTime)) {
                    if (enemy.collidesWith(projectile)) {
                        enemy.deactive();
                        projectile.deactive();
                        //Add points based on the type of enemy:
                        if (enemy instanceof RegularEnemy) {
                            score += Integer.parseInt(gameProps.getProperty("score.destroyedEnemy.regular"));
                        } else if (enemy instanceof StrafingEnemy) {
                            score += Integer.parseInt(gameProps.getProperty("score.destroyedEnemy.strafing"));
                        } else if (enemy instanceof ShootingEnemy) {
                            score += Integer.parseInt(gameProps.getProperty("score.destroyedEnemy.shooting"));
                        }
                        Image explosionImage = new Image(gameProps.getProperty("explosion.large.image"));
                        int explosionDuration = Integer.parseInt(gameProps.getProperty("explosion.large.duration"));
                        Explosion explosion = new Explosion(enemy.getX(), enemy.getY(), explosionImage, explosionDuration);
                        explosions.add(explosion);
                    }
                }
            }
        }

        // 敌方子弹击中玩家
        for (EnemyProjectile ep : enemyProjectiles) {
            if (ep.isActive() && player.collidesWith(ep)) {
                ep.deactive();
                Image smallExplosionImage = new Image(gameProps.getProperty("explosion.small.image"));
                int smallDuration = Integer.parseInt(gameProps.getProperty("explosion.small.duration"));
                explosions.add(new Explosion(ep.getX(), ep.getY(), smallExplosionImage, smallDuration));
                if (!player.isInvincible()) {
                    player.loseLife();
                    score = Math.max(0, score - Integer.parseInt(gameProps.getProperty("score.gotHit")));
                }
                if (player.getLives() == 0) {
                    Window.close();
                }
            }
        }

// 玩家子弹击中敌方子弹
        for (PlayerProjectile pp : projectiles) {
            for (EnemyProjectile ep : enemyProjectiles) {
                if (pp.isActive() && ep.isActive() && pp.collidesWith(ep)) {
                    pp.deactive();
                    ep.deactive();
                    score += Integer.parseInt(gameProps.getProperty("score.hitProjectile"));
                    Image smallExplosionImage = new Image(gameProps.getProperty("explosion.small.image"));
                    int smallDuration = Integer.parseInt(gameProps.getProperty("explosion.small.duration"));
                    explosions.add(new Explosion(ep.getX(), ep.getY(), smallExplosionImage, smallDuration));
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
        for (int i = 0; i < enemyProjectiles.size(); i++) {
            if (!enemyProjectiles.get(i).isActive()) {
                enemyProjectiles.remove(i);
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
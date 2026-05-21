package game;

import bagel.Image;
import bagel.Input;

import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;
    private ArrayList<PlayerProjectile> projectiles;
    private ArrayList<EnemyProjectile> enemyProjectiles;
    private ArrayList<Explosion> explosions;
    private ArrayList<Wave> waves;
    private int currentWaveIndex = 0;

    // UI
    private int lives;
    private int score = 0;
    private boolean InvMode = false;
    private int speedLevel = 0;

    private boolean gameWon = false;

    public BattleScreen(Properties gameProps) {
        super(gameProps);

        // initialize battle screen objects
        initialiseObjects();
    }

    @Override
    public void update(Input input) {
        double currentTimeScale = calTimeScale();
        // update player status
        PlayerProjectile p = player.update(input, currentTimeScale);
        if (p != null) {
            projectiles.add(p);
        }

        // update current wave (enemies + powerups)
        Wave currentWave = waves.get(currentWaveIndex);
        currentWave.update(currentTimeScale);

        // collect enemy projectiles from ShootingEnemies
        for (Enemy enemy : currentWave.getEnemies()) {
            if (enemy instanceof ShootingEnemy) {
                ShootingEnemy se = (ShootingEnemy) enemy;
                if (se.canShoot()) {
                    EnemyProjectile ep = se.shoot();
                    if (ep != null) {
                        enemyProjectiles.add(ep);
                    }
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

        // update explosions
        for (Explosion explosion : explosions) {
            explosion.update(currentTimeScale);
        }

        // check collisions
        checkCollisions();

        // delete inactive objects
        deleteInactiveObjects();
        currentWave.deleteInactiveEnemies();

        // check wave completion
        if (getCurrentWave().isCompleted() && enemyProjectiles.isEmpty()) {
            if (currentWaveIndex < waves.size() - 1) {
                advanceWave();
            } else {
                score += Integer.parseInt(gameProps.getProperty("score.waveCompleted"));
                gameWon = true;
            }
        }

        draw();
    }


    @Override
    public void draw() {
        player.draw();

        getCurrentWave().draw();

        for (PlayerProjectile projectile : projectiles) {
            projectile.draw();
        }

        for (EnemyProjectile ep : enemyProjectiles) {
            ep.draw();
        }

        for (Explosion explosion : explosions) {
            explosion.draw();
        }

        lives = player.getLives();
        ShadowAliens.getUI().draw(lives, getCurrentWave().getWaveNumber(), score);
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
        Image invincibilityImage = new Image(gameProps.getProperty("invincibility.image"));
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime, projectileImage, projectileSpeed, invincibilityImage);

        //initialize powerups and enemies along waves
        waves = new ArrayList<>();
        int w = 1;
        while (gameProps.getProperty(String.format("wave.%d.enemy.0.type", w)) != null
                || gameProps.getProperty(String.format("wave.%d.powerup.0.type", w)) != null) {
            waves.add(new Wave(gameProps, w));
            w++;
        }

    }

    public void checkCollisions() {
        // check if player collides with enemies
        for (Enemy enemy : getCurrentWave().getEnemies()) {
            if (enemy.isActive() && enemy.hasArrived(getCurrentWave().getFrameCount())) {
                if (enemy.collidesWith(player)) {
                    enemy.deactive();
                    // large Explosion
                    Image largeExplosionImage = new Image(gameProps.getProperty("explosion.large.image"));
                    int largeDuration = Integer.parseInt(gameProps.getProperty("explosion.large.duration"));
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), largeExplosionImage, largeDuration));
                    if (!player.isInvincible()) {
                        player.loseLife();
                        score = Math.max(0, score - Integer.parseInt(gameProps.getProperty("score.gotHit")));
                    }
                }
            }
        }
        // check if enemies collide with projectiles
        for (Enemy enemy : getCurrentWave().getEnemies()) {
            for (PlayerProjectile projectile : projectiles) {
                if (enemy.isActive() && enemy.hasArrived(getCurrentWave().getFrameCount())) {
                    if (enemy.collidesWith(projectile)) {
                        enemy.deactive();
                        projectile.deactive();
                        //Add points based on the type of enemy:
                        switch (enemy) {
                            case RegularEnemy regularEnemy ->
                                    score += Integer.parseInt(gameProps.getProperty("score.destroyedEnemy.regular"));
                            case StrafingEnemy strafingEnemy ->
                                    score += Integer.parseInt(gameProps.getProperty("score.destroyedEnemy.strafing"));
                            case ShootingEnemy shootingEnemy ->
                                    score += Integer.parseInt(gameProps.getProperty("score.destroyedEnemy.shooting"));
                            default -> {
                            }
                        }
                        Image explosionImage = new Image(gameProps.getProperty("explosion.large.image"));
                        int explosionDuration = Integer.parseInt(gameProps.getProperty("explosion.large.duration"));
                        Explosion explosion = new Explosion(enemy.getX(), enemy.getY(), explosionImage, explosionDuration);
                        explosions.add(explosion);
                    }
                }
            }
        }

        // check if enemyProjectile hit player
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
            }
        }

        // check if enemyProjectile hit playerProjectile
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

        for (Powerup powerup : getCurrentWave().getPowerups()) {
            if (powerup.isActive() && player.collidesWith(powerup)) {
                powerup.apply(player);
                powerup.deactive();
                score += Integer.parseInt(gameProps.getProperty("score.gotPowerup"));
            }
        }
    }

    public void deleteInactiveObjects() {
        //the deletion for enemy and powerups are handled in wave class

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

    public void advanceWave() {
        score += Integer.parseInt(gameProps.getProperty("score.waveCompleted"));
        currentWaveIndex++;
        projectiles.clear();
        enemyProjectiles.clear();
    }

    private Wave getCurrentWave() {
        return waves.get(currentWaveIndex);
    }

    public void skipWave() {
        Wave currentWave = waves.get(currentWaveIndex);
        // clear all gameObjects, no explosion and no points of Objects rewarded
        currentWave.clearObjects();
        enemyProjectiles.clear();
        projectiles.clear();

        // wave points rewarded
        score += Integer.parseInt(gameProps.getProperty("score.waveCompleted"));

        // identify if it's last wave
        if (currentWaveIndex < waves.size() - 1) {
            currentWaveIndex++;
        } else {
            gameWon = true;
        }
    }

    public boolean isGameOver() {
        return player.getLives() <= 0;
    }

    public boolean isGameWon() {
        return gameWon;
    }
}
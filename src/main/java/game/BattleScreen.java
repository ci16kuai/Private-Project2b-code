package game;

import bagel.Image;
import bagel.Input;

import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;

    // Lists for objects that exist during battle
    private ArrayList<PlayerProjectile> projectiles;
    private ArrayList<EnemyProjectile> enemyProjectiles;
    private ArrayList<Explosion> explosions;

    // Stores all waves and tracks the current wave
    private ArrayList<Wave> waves;
    private int currentWaveIndex = 0;

    // UI values
    private int lives;
    private int score = 0;

    // Developer invincibility mode
    private boolean InvMode = false;

    // Controls the game speed level
    private int speedLevel = 0;

    // True when the player has completed all waves
    private boolean gameWon = false;

    public BattleScreen(Properties gameProps) {
        super(gameProps);

        // Create all battle objects
        initialiseObjects();
    }

    @Override
    public void update(Input input) {
        double currentTimeScale = calTimeScale();

        // Update player and create a projectile if the player shoots
        PlayerProjectile p = player.update(input, currentTimeScale);
        if (p != null) {
            projectiles.add(p);
        }

        // Update enemies and powerups in the current wave
        Wave currentWave = waves.get(currentWaveIndex);
        currentWave.update(currentTimeScale);

        // Let shooting enemies create projectiles when ready
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

        // Update player projectiles
        for (PlayerProjectile projectile : projectiles) {
            projectile.update(currentTimeScale);
        }

        // Update enemy projectiles
        for (EnemyProjectile ep : enemyProjectiles) {
            ep.update(currentTimeScale);
        }

        // Update explosions
        for (Explosion explosion : explosions) {
            explosion.update(currentTimeScale);
        }

        // Check all collisions
        checkCollisions();

        // Remove inactive objects
        deleteInactiveObjects();
        currentWave.deleteInactiveEnemies();

        // Move to the next wave or win the game if all waves are complete
        if (getCurrentWave().isCompleted() && enemyProjectiles.isEmpty()) {
            if (currentWaveIndex < waves.size() - 1) {
                advanceWave();
            } else {
                score += Integer.parseInt(gameProps.getProperty("score.waveCompleted"));
                gameWon = true;
            }
        }

        // Draw all battle objects
        draw();
    }

    @Override
    public void draw() {
        // Draw player
        player.draw();

        // Draw current wave enemies and powerups
        getCurrentWave().draw();

        // Draw player projectiles
        for (PlayerProjectile projectile : projectiles) {
            projectile.draw();
        }

        // Draw enemy projectiles
        for (EnemyProjectile ep : enemyProjectiles) {
            ep.draw();
        }

        // Draw explosions
        for (Explosion explosion : explosions) {
            explosion.draw();
        }

        // Draw UI information
        lives = player.getLives();
        ShadowAliens.getUI().draw(lives, getCurrentWave().getWaveNumber(), score);
    }

    public void initialiseObjects() {
        // Create empty object lists
        projectiles = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        explosions = new ArrayList<>();   // 加在这里

        // Load player settings from game properties
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

        // Create player
        player = new Player(playerX, playerY, playerImage, playerSpeed, initialLives, shootCooldown, hitInvincibilityTime, projectileImage, projectileSpeed, invincibilityImage);

        // Load all waves from game  properties
        waves = new ArrayList<>();
        int w = 1;
        while (gameProps.getProperty(String.format("wave.%d.enemy.0.type", w)) != null
                || gameProps.getProperty(String.format("wave.%d.powerup.0.type", w)) != null) {
            waves.add(new Wave(gameProps, w));
            w++;
        }
    }

    public void checkCollisions() {
        // Check collision between player and enemies
        for (Enemy enemy : getCurrentWave().getEnemies()) {
            if (enemy.isActive() && enemy.hasArrived(getCurrentWave().getFrameCount())) {
                if (enemy.collidesWith(player)) {
                    enemy.deactive();

                    // Create a large explosion at the enemy position
                    Image largeExplosionImage = new Image(gameProps.getProperty("explosion.large.image"));
                    int largeDuration = Integer.parseInt(gameProps.getProperty("explosion.large.duration"));
                    explosions.add(new Explosion(enemy.getX(), enemy.getY(), largeExplosionImage, largeDuration));

                    // Player loses one life if not invincible
                    if (!player.isInvincible()) {
                        player.loseLife();
                        score = Math.max(0, score - Integer.parseInt(gameProps.getProperty("score.gotHit")));
                    }
                }
            }
        }

        // Check collision between player projectiles and enemies
        for (Enemy enemy : getCurrentWave().getEnemies()) {
            for (PlayerProjectile projectile : projectiles) {
                if (enemy.isActive() && enemy.hasArrived(getCurrentWave().getFrameCount())) {
                    if (enemy.collidesWith(projectile)) {
                        enemy.deactive();
                        projectile.deactive();

                        // Add score based on enemy type
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

                        // Create a large explosion at the enemy position
                        Image explosionImage = new Image(gameProps.getProperty("explosion.large.image"));
                        int explosionDuration = Integer.parseInt(gameProps.getProperty("explosion.large.duration"));
                        Explosion explosion = new Explosion(enemy.getX(), enemy.getY(), explosionImage, explosionDuration);
                        explosions.add(explosion);
                    }
                }
            }
        }

        // Check collision between enemy projectiles and player
        for (EnemyProjectile ep : enemyProjectiles) {
            if (ep.isActive() && player.collidesWith(ep)) {
                ep.deactive();

                // Create a small explosion at the projectile position
                Image smallExplosionImage = new Image(gameProps.getProperty("explosion.small.image"));
                int smallDuration = Integer.parseInt(gameProps.getProperty("explosion.small.duration"));
                explosions.add(new Explosion(ep.getX(), ep.getY(), smallExplosionImage, smallDuration));

                // Player loses one life if not invincible
                if (!player.isInvincible()) {
                    player.loseLife();
                    score = Math.max(0, score - Integer.parseInt(gameProps.getProperty("score.gotHit")));
                }
            }
        }

        // Check collision between player projectiles and enemy projectiles
        for (PlayerProjectile pp : projectiles) {
            for (EnemyProjectile ep : enemyProjectiles) {
                if (pp.isActive() && ep.isActive() && pp.collidesWith(ep)) {
                    pp.deactive();
                    ep.deactive();

                    // Add score for destroying an enemy projectile
                    score += Integer.parseInt(gameProps.getProperty("score.hitProjectile"));

                    // Create a small explosion at the collision position
                    Image smallExplosionImage = new Image(gameProps.getProperty("explosion.small.image"));
                    int smallDuration = Integer.parseInt(gameProps.getProperty("explosion.small.duration"));
                    explosions.add(new Explosion(ep.getX(), ep.getY(), smallExplosionImage, smallDuration));
                }
            }
        }

        // Check collision between player and powerups
        for (Powerup powerup : getCurrentWave().getPowerups()) {
            if (powerup.isActive() && player.collidesWith(powerup)) {
                powerup.apply(player);
                powerup.deactive();
                score += Integer.parseInt(gameProps.getProperty("score.gotPowerup"));
            }
        }
    }

    public void deleteInactiveObjects() {
        // Enemy and powerup deletion is handled in the Wave class

        // Remove inactive enemy projectiles
        for (int i = 0; i < enemyProjectiles.size(); i++) {
            if (!enemyProjectiles.get(i).isActive()) {
                enemyProjectiles.remove(i);
                i--;
            }
        }

        // Remove inactive player projectiles
        for (int i = 0; i < projectiles.size(); i++) {
            if (!projectiles.get(i).isActive()) {
                projectiles.remove(i);
                i--;
            }
        }

        // Remove inactive explosions
        for (int i = 0; i < explosions.size(); i++) {
            if (!explosions.get(i).isActive()) {
                explosions.remove(i);
                i--;
            }
        }
    }

    public void speedUp() {
        // Increase game speed level
        speedLevel++;
    }

    public void speedDown() {
        // Decrease game speed level
        speedLevel--;
    }

    // Calculate the time scale based on the current speed level
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
        // Toggle developer invincibility mode
        InvMode = !InvMode;
        player.setDevInvincible(InvMode);
    }

    public void advanceWave() {
        // Add wave completion score and move to the next wave
        score += Integer.parseInt(gameProps.getProperty("score.waveCompleted"));
        currentWaveIndex++;

        // Clear projectiles when entering a new wave
        projectiles.clear();
        enemyProjectiles.clear();
    }

    private Wave getCurrentWave() {
        // Return the wave currently being played
        return waves.get(currentWaveIndex);
    }

    public void skipWave() {
        Wave currentWave = waves.get(currentWaveIndex);

        // Clear all current wave objects without giving object scores
        currentWave.clearObjects();
        enemyProjectiles.clear();
        projectiles.clear();

        // Add wave completion score
        score += Integer.parseInt(gameProps.getProperty("score.waveCompleted"));

        // Move to the next wave, or win if this is the final wave
        if (currentWaveIndex < waves.size() - 1) {
            currentWaveIndex++;
        } else {
            gameWon = true;
        }
    }

    public boolean isGameOver() {
        // Game is over when the player has no lives left
        return player.getLives() <= 0;
    }

    public boolean isGameWon() {
        // Game is won after all waves are completed
        return gameWon;
    }
}
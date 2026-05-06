package game;

import bagel.Image;
import bagel.Input;
import bagel.Window;

import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Explosion> explosions;

    // UI
    private UI ui;
    private int lives;
    private int wave = 1;
    private int score = 0;
    private int frameCount;
    protected double timeScale = 1;

    private boolean DevMode = false; //Dev mode status
    private int speedLevel = 0;

    public BattleScreen(Properties gameProps) {
        super(gameProps);

        // initialize battle screen objects
        initialise_objects();

        // initialize information UI
        ui =  new UI(gameProps);
    }

    @Override
    public void update(Input input) {
        // update player status
        double currentTimeScale = calTimeScale();
        if (player.update(input, currentTimeScale)){  //returned 1 = player pressed SPACE
            Projectile projectile =  new Projectile(
                    player.x,
                    player.y,
                    new Image(gameProps.getProperty("projectile.image")),
                    Double.parseDouble(gameProps.getProperty("projectile.movementSpeed")));
            projectiles.add(projectile);
        }
        // update enemy status
        for (Enemy enemy : enemies) {
            enemy.update(frameCount);
        }
        // update projectiles
        for (Projectile projectile : projectiles){
            projectile.update(currentTimeScale);
        }

        //update explosions
        for (Explosion explosion: explosions){
            explosion.update(currentTimeScale);
        }

        // check collision
        checkCollisions();

        // delete inactive objects
        deleteInactiveObjects();

        draw();
        frameCount ++;
    }

    @Override
    public void draw() {
        //draw player
        player.draw();

        //*draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw();
        }

        //*draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw();
        }

        //*draw explosions
        for (Explosion explosion : explosions) {
            explosion.draw();
        }

        //*draw UI
        lives = player.getLives();
        ui.draw(lives, wave, score);
    }

    public void initialise_objects(){
        //*initialize player
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double PlayerX = ShadowAliens.screenWidth/2;
        double PlayerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int PlayerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initial_lives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        player = new Player(PlayerX, PlayerY, playerImage, PlayerSpeed, initial_lives, shootCooldown);

        //*initialize projectile and explosions
        projectiles = new ArrayList<>();
        explosions = new ArrayList<>();

        //*initialize enemy
         enemies = new ArrayList<>();
         Image enemyImage = new Image(gameProps.getProperty("enemy.image"));

         int i = 0;
         //read the enemy data until no enemies
         while ((gameProps.getProperty(String.format("enemy.%d.arrivalTime", i))!=null)) {
             int arrivalTime = Integer.parseInt(gameProps.getProperty(String.format("enemy.%d.arrivalTime", i)));
             int enemySpeed = Integer.parseInt(gameProps.getProperty(String.format("enemy.%d.movementSpeed", i)));
             double EnemyX = Double.parseDouble(gameProps.getProperty(String.format("enemy.%d.posX", i)));

             Enemy enemy = new Enemy(EnemyX, 0 - enemyImage.getHeight()/2, enemyImage, enemySpeed, arrivalTime);
             enemies.add(enemy); // add it to arraylist
             i++;
         }
    }


    public void checkCollisions(){
        // check if player collide with enemies
        for (Enemy enemy: enemies){
           if(enemy.isActive() && (frameCount >= enemy.arrivalTime)) {
                if (enemy.collidesWith(player)){ // if collides
                    // enemy inactive
                    enemy.deactive();
                    //if not in Dev mode, player lose 1 live
                    if (!DevMode){
                        player.lives -= 1;
                    }

                    if (player.lives == 0) {
                        Window.close();
                    }
                }
            }
        }
        // check if enemies collide with projectiles
        for (Enemy enemy: enemies){
            for (Projectile projectile: projectiles){
                if(enemy.isActive() && (frameCount >= enemy.arrivalTime)) {
                    if (enemy.collidesWith(projectile)){ // if collides
                        // enemy inactive
                        enemy.deactive();
                        // projectile inactive
                        projectile.deactive();
                        // score add
                        score += 1;
                        // explosion occurs
                        Image explosionImage =  new Image(gameProps.getProperty("explosion.image"));
                        int explosionDuration =  Integer.parseInt(gameProps.getProperty("explosion.duration"));
                        Explosion explosion = new Explosion(enemy.x, enemy.y, explosionImage, explosionDuration);
                        explosions.add(explosion); // add explosion to explosions array list
                    }
                }
            }
        }
    }

    public void deleteInactiveObjects(){

        // within enemies size, remove inactive objects
        for (int i = 0; i < enemies.size(); i++){
            if (!enemies.get(i).active){
                enemies.remove(i);
                i--;
            }
        }

        // within projectiles size, remove inactive objects
        for (int i = 0; i < projectiles.size(); i++){
            if (!projectiles.get(i).active){
                projectiles.remove(i);
                i--;
            }
        }

        // remove explosions
        for (int i = 0; i < explosions.size(); i++){
            if (!explosions.get(i).active){
                explosions.remove(i);
                i--;
            }
        }
    }

    public void speedup(){
        speedLevel ++;
    }

    public void speedDown(){
        speedLevel --;
    }

    //calculate the timeScale based on speedLevel
    public double calTimeScale(){
        if (speedLevel > 0){
            return speedLevel+1;
        }
        if (speedLevel < 0){
            return 1.0 / (1-speedLevel);
        }
        return 1.0;
    }

    public void switchDev(){
        DevMode = !DevMode;
    }
}

package game;

import bagel.Image;
import bagel.Input;

import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;
    public ArrayList<Enemy> enemies;
    public ArrayList<Projectile> projectiles;
//    public ArrayList<Explosion>;

    // UI
    private UI ui;
    private int lives;
    private int wave;
    private int score;

    private int frameCount;

    public BattleScreen(Properties gameProps) {
        super(gameProps);

        // initialize battle screen objects
        initialise_objects();

        // initialize information UI
        ui =  new UI(gameProps);

        // initialize game
    }

    @Override
    public void update(Input input) {
        // update player status
        if (player.update(input)){
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
            projectile.update();
        }

        // delete inactive objects
        deleteInactiveObjects();

        draw();
        frameCount ++;
    }

    @Override
    public void draw() {
        //draw player
        player.draw();

        //draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw();
        }

        //draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw();
        }

        //draw UI
        lives = player.getLives();
        ui.draw(lives, wave, score);
    }

    public void initialise_objects(){
        //initialize player
        Image playerImage = new Image(gameProps.getProperty("player.image"));
        double PlayerX = ShadowAliens.screenWidth/2;
        double PlayerY = Double.parseDouble(gameProps.getProperty("player.posY"));
        int PlayerSpeed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int initial_lives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        int shootCooldown = Integer.parseInt(gameProps.getProperty("player.shootCooldown"));
        player = new Player(PlayerX, PlayerY, playerImage, PlayerSpeed, initial_lives, shootCooldown);

        //initialize projectile
        projectiles = new ArrayList<Projectile>();

        //initialize enemy
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

    public void deleteInactiveObjects(){

        for (int i = 0; i < enemies.size(); i++){
            if (!enemies.get(i).active){
                enemies.remove(i);
                i--;
            }
        }

        for (int i = 0; i < projectiles.size(); i++){
            if (!projectiles.get(i).active){
                projectiles.remove(i);
                i--;
            }
        }
    }
}

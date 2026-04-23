package game;

import bagel.Image;
import bagel.Input;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;

public class BattleScreen extends Screen {

    private Player player;
//    public ArrayList<Enemy>;
//    public ArrayList<Projectile>;
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

    }

    @Override
    public void update(Input input) {
        player.update(input);
        draw();
    }

    @Override
    public void draw() {
        //draw player
        player.draw();

        //draw UI
        lives = player.getLives();
        ui.draw(lives, wave, score);
    }

    public void initialise_objects(){
        //initialize player
        Image image = new Image(gameProps.getProperty("player.image"));
        double x = ShadowAliens.screenWidth/2;
        double y = Double.parseDouble(gameProps.getProperty("player.posY"));
        int speed = Integer.parseInt(gameProps.getProperty("player.speed"));
        int lives = Integer.parseInt(gameProps.getProperty("player.initialLives"));
        player = new Player(x,y,image,speed,lives);

        //initialize enemy

        //initialize projectile
    }
}

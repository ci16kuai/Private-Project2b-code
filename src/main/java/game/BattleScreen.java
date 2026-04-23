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

    public BattleScreen(Properties gameProps) {
        super(gameProps);
        initialise_objects();
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
    }

    public void initialise_objects(){
        //initialize player
        Image image = new Image(gameProps.getProperty("player.image"));
        double x = ShadowAliens.screenWidth/2;
        double y = Double.parseDouble(gameProps.getProperty("player.posY"));
        int speed = Integer.parseInt(gameProps.getProperty("player.speed"));
        player = new Player(x,y,image,speed);

        //initialize enemy

        //initialize projectile
    }
}

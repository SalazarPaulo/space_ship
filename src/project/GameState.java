package src.project;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import src.graphics.Animation;
import src.graphics.Assets;
import src.math.Vector2D;
import src.gameObjects.Player;
import src.gameObjects.Constants;
import src.gameObjects.MovingObject;
import src.gameObjects.*;

public class GameState {

    private Player player;
    private ArrayList<MovingObject> movingObjects = new ArrayList<MovingObject>();
    private int meteors;
    private ArrayList<Animation> explosions = new ArrayList<Animation>();
    int i;

    public GameState () {
        player = new Player(new Vector2D(Constants.WIDTH/2 - Assets.player.getWidth()/2,
				Constants.HEIGHT/2 - Assets.player.getHeight()/2), new Vector2D(),
				Constants.PLAYER_MAX_VEL, Assets.player, this);
        movingObjects.add(player);
        meteors = 1;
        startWave();
    }
    public void divideMeteor (Meteor meteor) {
        Size size = meteor.getSize();
        BufferedImage[] textures = size.textures;
        Size newSize = null;
        switch(size) {
            case BIG:
                newSize = Size.MED;
                break;
            case MED:
                newSize = Size.SMALL;
                break;
            case SMALL:
                newSize = Size.TINY;
                break;
            default:
                return;
        }
        for ( i = 0; i < size.quantity; i++ ) {
            movingObjects.add(new Meteor (
                meteor.getPosition(),
                new Vector2D(0, 1).setDirection(Math.random()*Math.PI*2), // Direccion aleatoria
                Constants.METEOR_VEL*Math.random() + 1,
                textures[(int)Math.random()*textures.length],
                this, // gameState
                newSize
            ));
        }

    }
    private void startWave () {

        double x, y;

        for ( i = 0; i < meteors; i++ ) {

            x = i % 2 == 0 ? Math.random()*Constants.WIDTH: 0;
            y = i % 2 == 0 ? 0: Math.random()*Constants.HEIGHT;

            BufferedImage texture = Assets.bigs[(int)Math.random()*Assets.bigs.length];

            movingObjects.add(new Meteor (
                new Vector2D(x, y),
                new Vector2D(0, 1).setDirection(Math.random()*Math.PI*2), // Direccion aleatoria
                Constants.METEOR_VEL*Math.random() + 1,
                texture,
                this, // gameState
                Size.BIG
            ));
        }
        meteors++;
        spawnUfo();
    }
    public void playExplosion (Vector2D position) {
        explosions.add(new Animation (
            Assets.exp,
            50,
            position.subtract(new Vector2D(Assets.exp[0].getWidth()/2, Assets.exp[0].getHeight()/2)) // Explosion en todo el centro del objeto
        ));  
    }
    private void spawnUfo () {
        
        int rand = (int) (Math.random() * 2);
        ////---- Posicion Inicial ----////
        double x = rand == 0 ? (Math.random()*Constants.WIDTH) : 0;
        double y = rand == 0 ? 0 : (Math.random()*Constants.HEIGHT);

        ArrayList<Vector2D> path = new ArrayList<Vector2D>(); // Representa el camino
        double posX, posY; // Posiciones de los nodos que forman el camino

        ////-- Valor al azar lado izquiedo superior (cuadrante II) ----////
        posX = Math.random()*Constants.WIDTH/2; 
        posY = Math.random()*Constants.HEIGHT/2;
        path.add(new Vector2D(posX, posY)); // Agregando las posiciones al camino

        ////-- Sector derecho superior (cuadrante I) ----////
        posX = Math.random()*(Constants.WIDTH/2) + Constants.WIDTH/2; 
        posY = Math.random()*Constants.HEIGHT/2;
        path.add(new Vector2D(posX, posY));
        
        ////-- Sector Izquierdo Inferior (cuadrante III) ----////
        posX = Math.random()*Constants.WIDTH/2; 
        posY = Math.random()*(Constants.HEIGHT/2) + Constants.HEIGHT/2;
        path.add(new Vector2D(posX, posY));
        
        ////-- Sector Izquierdo derecho (cuadrante IV) ----////
        posX = Math.random()*(Constants.WIDTH/2) + Constants.WIDTH/2; 
        posY = Math.random()*(Constants.HEIGHT/2) + Constants.HEIGHT/2;
        path.add(new Vector2D(posX, posY));
        
        movingObjects.add( new Ufo(
            new Vector2D(x, y), // Position
            new Vector2D(), // Velocidad puede ser vector 0, no hay problema ya que hay aceleracion <--- 
            Constants.UFO_MAX_VEL, // Velocidad maxima
            Assets.ufo, // imagen
            path, // camino
            this // estado de juego
            ));
        }
        public void update () {
            for (i = 0; i < movingObjects.size(); i++ ) 
                movingObjects.get(i).update();
            for (i = 0; i < explosions.size(); i++ ) {
                Animation anim = explosions.get(i);
                anim.update();
                if ( !(anim.isRunning()) ) 
                    explosions.remove(i);
            }
            for (i = 0; i < movingObjects.size(); i++ ) 
                if ( movingObjects.get(i) instanceof Meteor ) // si este objeto movible es una instancia de la clase meteoro
                    return;
            startWave();
        }
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            for (i = 0; i < movingObjects.size(); i++ )
                movingObjects.get(i).draw(g);

            for (i = 0; i < explosions.size(); i++ ) {
                Animation anim = explosions.get(i);
                g2d.drawImage(anim.getCurrentFrame(), (int) anim.getPosition().getX(), (int) anim.getPosition().getY(), null);
            }
        }
    public ArrayList<MovingObject> getMovingObjects() { return movingObjects;}
    public Player getPlayer() { return player; }
}

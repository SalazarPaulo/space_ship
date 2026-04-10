package src.gameObjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;

import src.graphics.Assets;
import src.math.Vector2D;
import src.project.GameState;

public class Ufo extends MovingObject {
    /*El target.subtract(this.getCenter()).getMagnitude() es para hallar la distancia (un valor positivo), el  target.subtract(this.getCenter()) es para hallar un vector que vaya desde this.getCenter() hasta target. Ese vector se puede usar para cálcular la fuerza para mover el UFO hacia los puntos objetivos. */

    private ArrayList<Vector2D> path; 
    private Vector2D currentNode;
    private int index;
    private boolean following;
    private Chronometer fireRate;
    int i;

    public Ufo (Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, 
                ArrayList<Vector2D> path, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        this.path = path;
        index = 0;
        following = true;
        fireRate = new Chronometer();
        fireRate.run(Constants.UFO_FIRE_RATE);
    }
    private Vector2D pathFollowing () {
        currentNode = path.get(index);
        double distanceToNode = currentNode.subtract(getCenter()).getMagnitud();

        if ( distanceToNode < Constants.NODE_RADIUS ) {
            index++;
            if ( index >= path.size() ) 
                following = false;
        }
        return seekForce(currentNode);
    }
    private Vector2D seekForce (Vector2D target) {
        Vector2D desiredVelocity = target.subtract(getCenter());
        desiredVelocity = desiredVelocity.normalize().scale(maxVel);
        return desiredVelocity.subtract(velocity);
    }
    @Override
    public void update() {

        Vector2D pathFollowing;

        if ( following )
            pathFollowing = pathFollowing();
        else
            pathFollowing = new Vector2D();

        pathFollowing = pathFollowing.scale(1/Constants.UFO_MASS); // Aceleracion
        velocity = velocity.add(pathFollowing); // Añadiendo la aceleracion a la velocidad
        velocity = velocity.limit(maxVel); // Para que no excesa la velocidad maxima
        position = position.add(velocity); // Añadiendo la velocidad a la posicion
        if ( position.getX() > Constants.WIDTH || position.getY() > Constants.HEIGHT 
        || position.getX() < 0 || position.getY() < 0 ) 
            Destroy();

        //-- Shoot --//
        if ( !fireRate.isRunning() ) {
            Vector2D toPlayer = gameState.getPlayer().getCenter().subtract(getCenter());

            toPlayer = toPlayer.normalize(); // Normalizando el vecto toPlayer, porque estamos trabajando con angulos
            double currentAngle = toPlayer.getAngle();
            double newAngle = Math.random()*(Math.PI) - (Math.PI/2) + currentAngle; // Para obtener un angulo entre 0 y 180 grados con respecto al jugador
            toPlayer = toPlayer.setDirection(newAngle); // Modificando el vector toPlayer

            //-- Laser --//
            Laser laser = new Laser (
                getCenter().add(toPlayer.scale(width)), // Posicion Inicial parecido al jugador
                toPlayer, // Velocidad el vector toPlayer
                Constants.LASER_VEL, // Constante de la velocidad
                newAngle + Math.PI/2, // Angulo
                Assets.redLaser,
                gameState
            );
            gameState.getMovingObjects().add(0, laser);
            fireRate.run(Constants.UFO_FIRE_RATE); // Comienze a disparar 

        }

        
        angle += 0.05; // Rotacion

        collideWith(); 
        fireRate.update();
    }

    @Override
    public void draw(Graphics g) {
        
        Graphics2D g2d = (Graphics2D)g;
        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width/2, height/2);
        g2d.drawImage(texture, at, null);
        ////---- Dibujo de un rectangulo que representa cada nodo del camino ----////
        g.setColor(Color.RED);
        for ( i = 0; i < path.size(); i++ )
        g.drawRect ((int) path.get(i).getX(), (int) path.get(i).getY(), 5, 5);// Para poder ver a donde va el UFO
    }   
}

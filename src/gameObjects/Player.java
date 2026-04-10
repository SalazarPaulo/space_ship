package src.gameObjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

import src.graphics.Assets;
import src.input.KeyBoard;
import src.math.Vector2D;
import src.project.GameState;

public class Player extends MovingObject {

    private Vector2D heading;
    private Vector2D acceleration;
    private boolean accelerating = false;
    private Chronometer fireRate;

    public Player (Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        this.gameState = gameState;
        heading = new Vector2D(0, 1);
        acceleration = new Vector2D();
        fireRate = new Chronometer();

    }
    @Override
    public void update () {

        if ( KeyBoard.SHOOT &&  !fireRate.isRunning() ) {
            gameState.getMovingObjects().add(0, new Laser (
                getCenter().add(heading.scale(width)),
                heading,
                Constants.LASER_VEL,
                angle,
                Assets.redLaser,
                gameState
            ));
            fireRate.run(Constants.FIRERATE); //-- Arrancar          
        }
        if ( KeyBoard.RIGHT )
            angle += Constants.DELTAANGLE;
        if ( KeyBoard.LEFT )
            angle -= Constants.DELTAANGLE;
        
        if ( KeyBoard.UP ) {
            acceleration = heading.scale(Constants.ACC);
            accelerating = true;
        } else {
            if ( velocity.getMagnitud() != 0 )
                acceleration = (velocity.scale(-1).normalize()).scale(Constants.ACC/2);
            accelerating = false;   
        }

        velocity = velocity.add(acceleration);

        velocity = velocity.limit(maxVel);

        heading = heading.setDirection(angle - Math.PI/2);

        position = position.add(velocity);

        ///--- LIMITANDO PARA QUE LA NAVE NO SE SALGA DE LA VENTANA ---///
        if ( position.getX() > Constants.WIDTH)
            position.setX(0);
        if ( position.getY() > Constants.HEIGHT)
            position.setY(0);

        if ( position.getX() < 0 )
            position.setX(Constants.WIDTH);
        if ( position.getY() < 0 )
            position.setY(Constants.HEIGHT);

        fireRate.update(); //-- Actualizacion de disparo(ventana para cuando termina su recorrido)
        collideWith(); //-- Para el cuando choque
    }

    @Override
    public void draw (Graphics g) { 
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform at1 = AffineTransform.getTranslateInstance(position.getX() + width/2 + 5, position.getY() + height/2 + 10);
        AffineTransform at2 = AffineTransform.getTranslateInstance(position.getX() + 5, position.getY() + height/2 + 10);
        at1.rotate(angle, -5, -10);
        at2.rotate(angle, width/2 - 5, -10);
        if ( accelerating ) {
            g2d.drawImage(Assets.speed, at1, null);
            g2d.drawImage(Assets.speed, at2, null);
        }
        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width/2, height/2);
        g2d.drawImage (texture, at, null);
    }

}

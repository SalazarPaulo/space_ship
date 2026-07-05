package src.gameObjects;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import src.graphics.Assets;
import src.graphics.Sound;
import src.math.Vector2D;
import src.project.GameState;

public abstract class MovingObject extends GameObject {
    protected Vector2D velocity;
    protected AffineTransform at;
    protected double angle;
    protected double maxVel;
    protected int width;
    protected int height;
    protected GameState gameState;

    private final Sound explosion;
    private boolean dead;

    protected MovingObject(Vector2D position, Vector2D velocity, double maxVel,
                           BufferedImage texture, GameState gameState) {
        super(new Vector2D(position), texture);
        this.velocity = new Vector2D(velocity);
        this.maxVel = maxVel;
        this.gameState = gameState;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.explosion = new Sound(Assets.explosion);
    }

    /** Usa una copia para que dividir meteoritos no modifique el iterador actual. */
    protected final void collideWith() {
        ArrayList<MovingObject> snapshot = new ArrayList<>(gameState.getMovingObjects());
        for (MovingObject other : snapshot) {
            if (other == this || other.isDead() || isDead()) continue;
            double distance = other.getCenter().subtract(getCenter()).getMagnitude();
            if (distance < other.width / 2.0 + width / 2.0) {
                gameState.resolveCollision(this, other);
            }
        }
    }

    public void destroy() {
        if (dead) return;
        dead = true;
        if (!(this instanceof Laser) && !(this instanceof PowerUp)) explosion.play();
    }

    public boolean isDead() { return dead; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Vector2D getCenter() {
        return new Vector2D(position.getX() + width / 2.0, position.getY() + height / 2.0);
    }
}

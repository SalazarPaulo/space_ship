package src.gameObjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import src.math.Vector2D;
import src.project.GameState;

public class Laser extends MovingObject {
    private final boolean playerOwned;

    public Laser(Vector2D position, Vector2D direction, double maxVel, double angle,
                 BufferedImage texture, GameState gameState, boolean playerOwned) {
        super(position, direction.normalize().scale(maxVel), maxVel, texture, gameState);
        this.angle = angle;
        this.playerOwned = playerOwned;
    }

    @Override
    public void update(float dt) {
        position = position.add(velocity);
        if (position.getX() < -width || position.getX() > Constants.WIDTH + width
                || position.getY() < -height || position.getY() > Constants.HEIGHT + height) {
            destroy();
            return;
        }
        collideWith();
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        AffineTransform transform = AffineTransform.getTranslateInstance(position.getX() - width / 2.0, position.getY());
        transform.rotate(angle, width / 2.0, 0);
        g2d.drawImage(texture, transform, null);
    }

    public boolean isPlayerOwned() { return playerOwned; }
}

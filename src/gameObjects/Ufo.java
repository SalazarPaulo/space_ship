package src.gameObjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import src.graphics.Assets;
import src.graphics.Sound;
import src.math.Vector2D;
import src.project.GameState;

public class Ufo extends MovingObject {
    private final ArrayList<Vector2D> path;
    private int index;
    private long fireRate;
    private final Sound shoot;

    public Ufo(Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture,
               ArrayList<Vector2D> path, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        this.path = path;
        this.shoot = new Sound(Assets.ufoShoot);
    }

    @Override
    public void update(float dt) {
        fireRate += dt;
        if (index < path.size()) {
            Vector2D target = path.get(index);
            Vector2D toTarget = target.subtract(getCenter());
            if (toTarget.getMagnitude() < Constants.NODE_RADIUS) {
                index++;
            } else {
                Vector2D desired = toTarget.normalize().scale(maxVel);
                velocity = velocity.add(desired.subtract(velocity).scale(1.0 / Constants.UFO_MASS)).limit(maxVel);
            }
        }
        position = position.add(velocity);
        if (position.getX() > Constants.WIDTH + width || position.getY() > Constants.HEIGHT + height
                || position.getX() < -width || position.getY() < -height) {
            destroy(false);
            return;
        }
        if (fireRate >= Constants.UFO_FIRE_RATE && !gameState.getPlayer().isSpawning()) {
            shoot();
            fireRate = 0;
        }
        angle += 0.05;
        collideWith();
    }

    private void shoot() {
        Vector2D direction = gameState.getPlayer().getCenter().subtract(getCenter()).normalize();
        double currentAngle = direction.getAngle() + (Math.random() * Constants.UFO_ANGLE_RANGE - Constants.UFO_ANGLE_RANGE / 2.0);
        direction = new Vector2D(0, 1).setDirection(currentAngle).normalize();
        gameState.addMovingObject(new Laser(
                getCenter().add(direction.scale(width)), direction, Constants.LASER_VEL,
                currentAngle + Math.PI / 2.0, Assets.redLaser, gameState, false));
        shoot.play();
    }

    @Override
    public void destroy() { destroy(true); }

    public void destroy(boolean rewardScore) {
        if (isDead()) return;
        if (rewardScore) gameState.addScore(Constants.UFO_SCORE, getCenter());
        gameState.playExplosion(getCenter());
        super.destroy();
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        AffineTransform transform = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        transform.rotate(angle, width / 2.0, height / 2.0);
        g2d.drawImage(texture, transform, null);
    }
}

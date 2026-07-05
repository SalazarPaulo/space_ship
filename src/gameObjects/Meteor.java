package src.gameObjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import src.math.Vector2D;
import src.project.GameState;

public class Meteor extends MovingObject {
    private final Size size;

    public Meteor(Vector2D position, Vector2D velocity, double maxVel,
                  BufferedImage texture, GameState gameState, Size size) {
        super(position, velocity.normalize().scale(maxVel), maxVel, texture, gameState);
        this.size = size;
    }

    @Override
    public void update(float dt) {
        Player player = gameState.getPlayer();
        if (player != null && player.isShieldOn()) {
            double distance = player.getCenter().subtract(getCenter()).getMagnitude();
            if (distance < Constants.SHIELD_DISTANCE / 2.0 + width / 2.0) {
                Vector2D flee = getCenter().subtract(player.getCenter()).normalize().scale(0.12);
                velocity = velocity.add(flee).limit(Constants.METEOR_MAX_VEL);
            }
        }
        position = position.add(velocity);
        if (position.getX() > Constants.WIDTH) position.setX(-width);
        if (position.getY() > Constants.HEIGHT) position.setY(-height);
        if (position.getX() < -width) position.setX(Constants.WIDTH);
        if (position.getY() < -height) position.setY(Constants.HEIGHT);
        angle += Constants.DELTAANGLE / 2.0;
    }

    @Override
    public void destroy() { destroy(true); }

    public void destroy(boolean rewardScore) {
        if (isDead()) return;
        if (rewardScore) gameState.addScore(Constants.METEOR_SCORE, getCenter());
        gameState.divideMeteor(this);
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

    public Size getSize() { return size; }
}

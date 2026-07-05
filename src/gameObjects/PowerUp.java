package src.gameObjects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import src.graphics.Assets;
import src.graphics.Sound;
import src.math.Vector2D;
import src.project.GameState;
import src.ui.Action;
import src.ui.UiRenderer;

/**
 * Power-up circular e icono interno dibujados con Java2D; no usa PNG de powers/.
 */
public class PowerUp extends MovingObject {
    private long duration;
    private final Action action;
    private final Sound pickup;
    private final PowerUpTypes type;

    public PowerUp(Vector2D position, PowerUpTypes type, Action action, GameState gameState) {
        super(position, new Vector2D(), 0,
                new BufferedImage(Constants.POWER_UP_SIZE, Constants.POWER_UP_SIZE, BufferedImage.TYPE_INT_ARGB),
                gameState);
        this.type = type;
        this.action = action;
        this.pickup = new Sound(Assets.powerUp);
    }

    @Override
    public void update(float dt) {
        angle += dt * 0.006;
        duration += dt;
        if (duration > Constants.POWER_UP_DURATION) {
            destroy();
            return;
        }
        collideWith();
    }

    public void executeAction() {
        action.doAction();
        pickup.play();
    }

    @Override
    public void draw(Graphics graphics) {
        if (graphics instanceof Graphics2D) {
            UiRenderer.drawPowerUp((Graphics2D) graphics, position.getX(), position.getY(),
                    Constants.POWER_UP_SIZE, angle, type);
        }
    }
}

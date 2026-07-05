package src.gameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import src.graphics.Animation;
import src.graphics.Assets;
import src.graphics.Sound;
import src.input.KeyBoard;
import src.io.GameSettings;
import src.math.Vector2D;
import src.project.GameState;

/**
 * Nave jugable. Los efectos temporales se almacenan por separado para que,
 * por ejemplo, Disparo rápido y Doble cañón funcionen simultáneamente.
 */
public class Player extends MovingObject {
    private static final long MAX_STACKED_EFFECT_TIME = 60_000L;

    private Vector2D heading = new Vector2D(0, 1);
    private Vector2D acceleration = new Vector2D();
    private boolean accelerating;
    private long fireRate;
    private long spawnTime;
    private long flickerTime;
    private long shieldRemaining;
    private long doubleScoreRemaining;
    private long fastFireRemaining;
    private long doubleGunRemaining;
    private boolean spawning;
    private boolean visible = true;
    private final Sound shoot;
    private final Sound lose;
    private final Animation shieldEffect;

    public Player(Vector2D position, Vector2D velocity, double maxVel,
                  BufferedImage texture, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        shoot = new Sound(Assets.playerShoot);
        lose = new Sound(Assets.playerLoose);
        shieldEffect = new Animation(Assets.shieldEffect, 80, null);
    }

    @Override
    public void update(float dt) {
        if (isDead()) return;

        long elapsed = Math.max(0L, Math.round(dt));
        fireRate += elapsed;
        tickEffects(elapsed);
        updateSpawning(elapsed);

        /*
         * La invulnerabilidad del respawn solo bloquea disparos y daño: los controles
         * de giro y propulsión se procesan siempre. Esto replica el comportamiento de
         * la versión final de Hernández y evita que la nave parezca inmóvil durante
         * el parpadeo de reaparición.
         */
        long fireSpeed = isFastFireOn() ? Math.max(80L, Constants.FIRERATE / 2L) : Constants.FIRERATE;
        if (KeyBoard.SHOOT && fireRate >= fireSpeed && !spawning) shoot();
        if (KeyBoard.RIGHT) angle += Constants.DELTAANGLE;
        if (KeyBoard.LEFT) angle -= Constants.DELTAANGLE;

        // Recalcular la dirección antes de aplicar aceleración evita usar el rumbo
        // anterior en el primer frame después de reiniciar la nave.
        heading = new Vector2D(0, 1).setDirection(angle - Math.PI / 2.0);
        if (KeyBoard.UP) {
            acceleration = heading.scale(Constants.ACC);
            accelerating = true;
        } else {
            acceleration = velocity.getMagnitude() == 0
                    ? new Vector2D()
                    : velocity.scale(-1).normalize().scale(Constants.ACC / 2.0);
            accelerating = false;
        }

        velocity = velocity.add(acceleration).limit(maxVel);
        position = position.add(velocity);
        wrapAround();

        if (isShieldOn()) shieldEffect.update(elapsed);

        /*
         * No se evalúan choques mientras el jugador reaparece. Así puede alejarse del
         * punto de respawn sin quedar encadenado a un meteorito o láser que quedó allí.
         */
        if (!spawning) collideWith();
    }

    /**
     * Los cañones y la cadencia son decisiones independientes. De este modo,
     * FAST FIRE + DOUBLE GUN dispara dos láseres en cada ciclo acelerado.
     */
    private void shoot() {
        BufferedImage laserSprite = Assets.getSelectedLaser();
        if (isDoubleGunOn()) {
            Vector2D rightDirection = new Vector2D(heading).setDirection(angle - 1.3).normalize();
            Vector2D leftDirection = new Vector2D(heading).setDirection(angle - 1.9).normalize();
            gameState.addMovingObject(new Laser(getCenter().add(leftDirection.scale(width)), heading,
                    Constants.LASER_VEL, angle, laserSprite, gameState, true));
            gameState.addMovingObject(new Laser(getCenter().add(rightDirection.scale(width)), heading,
                    Constants.LASER_VEL, angle, laserSprite, gameState, true));
        } else {
            gameState.addMovingObject(new Laser(getCenter().add(heading.scale(width)), heading,
                    Constants.LASER_VEL, angle, laserSprite, gameState, true));
        }
        fireRate = 0;
        shoot.play();
    }

    private void tickEffects(long elapsed) {
        shieldRemaining = decrease(shieldRemaining, elapsed);
        doubleScoreRemaining = decrease(doubleScoreRemaining, elapsed);
        fastFireRemaining = decrease(fastFireRemaining, elapsed);
        doubleGunRemaining = decrease(doubleGunRemaining, elapsed);
    }

    private static long decrease(long value, long elapsed) { return Math.max(0L, value - elapsed); }

    private void updateSpawning(long elapsed) {
        if (!spawning) return;
        flickerTime += elapsed;
        spawnTime += elapsed;
        if (flickerTime >= Constants.FLICKER_TIME) {
            visible = !visible;
            flickerTime = 0;
        }
        if (spawnTime >= Constants.SPAWNING_TIME) {
            spawning = false;
            visible = true;
        }
    }

    /** Devuelve true cuando el impacto fue consumido y el peligro debe desaparecer. */
    public boolean takeDamage() {
        if (spawning || isDead()) return false;
        if (isShieldOn()) {
            shieldRemaining = 0;
            gameState.addMessage(getCenter(), "SHIELD", Color.CYAN);
            return true;
        }
        gameState.playExplosion(getCenter());
        lose.play();
        boolean stillAlive = gameState.subtractLife(getCenter());
        if (stillAlive) resetValues();
        else destroy();
        return true;
    }

    /**
     * Restaura un estado de vuelo controlable y pide al juego una ubicación libre.
     * No reutiliza PLAYER_START_POSITION a ciegas porque un meteorito puede haberse
     * quedado sobre el centro cuando ocurre el impacto.
     */
    private void resetValues() {
        angle = 0;
        heading = new Vector2D(0, -1);
        velocity = new Vector2D();
        acceleration = new Vector2D();
        accelerating = false;
        fireRate = 0;
        position = gameState.findSafeRespawnPosition(width, height);
        spawnTime = 0;
        flickerTime = 0;
        spawning = true;
        visible = true;
        gameState.restorePlayerInputFocus();
    }

    private void wrapAround() {
        if (position.getX() > Constants.WIDTH) position.setX(-width);
        if (position.getY() > Constants.HEIGHT) position.setY(-height);
        if (position.getX() < -width) position.setX(Constants.WIDTH);
        if (position.getY() < -height) position.setY(Constants.HEIGHT);
    }

    /** Repetir un efecto prolonga su duración; activar otro nunca lo reemplaza. */
    public void setShield() { shieldRemaining = extend(shieldRemaining, Constants.SHIELD_TIME); }
    public void setDoubleScore() { doubleScoreRemaining = extend(doubleScoreRemaining, Constants.DOUBLE_SCORE_TIME); }
    public void setFastFire() { fastFireRemaining = extend(fastFireRemaining, Constants.FAST_FIRE_TIME); }
    public void setDoubleGun() { doubleGunRemaining = extend(doubleGunRemaining, Constants.DOUBLE_GUN_TIME); }

    private static long extend(long remaining, long baseDuration) {
        return Math.min(MAX_STACKED_EFFECT_TIME, Math.max(0L, remaining) + baseDuration);
    }

    public boolean isSpawning() { return spawning; }
    public boolean isShieldOn() { return shieldRemaining > 0; }
    public boolean isDoubleScoreOn() { return doubleScoreRemaining > 0; }
    public boolean isFastFireOn() { return fastFireRemaining > 0; }
    public boolean isDoubleGunOn() { return doubleGunRemaining > 0; }

    @Override
    public void draw(Graphics graphics) {
        if (!visible) return;
        Graphics2D g2d = (Graphics2D) graphics;
        AffineTransform flameRight = AffineTransform.getTranslateInstance(
                position.getX() + width / 2.0 + 5, position.getY() + height / 2.0 + 10);
        AffineTransform flameLeft = AffineTransform.getTranslateInstance(
                position.getX() + 5, position.getY() + height / 2.0 + 10);
        flameRight.rotate(angle, -5, -10);
        flameLeft.rotate(angle, width / 2.0 - 5, -10);
        if (accelerating) {
            g2d.drawImage(Assets.speed, flameRight, null);
            g2d.drawImage(Assets.speed, flameLeft, null);
        }
        if (isShieldOn() && shieldEffect.getCurrentFrame() != null) {
            BufferedImage current = shieldEffect.getCurrentFrame();
            AffineTransform shield = AffineTransform.getTranslateInstance(
                    position.getX() - current.getWidth() / 2.0 + width / 2.0,
                    position.getY() - current.getHeight() / 2.0 + height / 2.0);
            shield.rotate(angle, current.getWidth() / 2.0, current.getHeight() / 2.0);
            g2d.drawImage(current, shield, null);
        }
        AffineTransform ship = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        ship.rotate(angle, width / 2.0, height / 2.0);
        BufferedImage activeShip = isDoubleGunOn()
                ? Assets.getDoubleGunSprite(GameSettings.getShipStyle())
                : Assets.getPlayerSprite(GameSettings.getShipStyle());
        g2d.drawImage(activeShip == null ? texture : activeShip, ship, null);
    }
}

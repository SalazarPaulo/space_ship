package src.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.sound.sampled.Clip;

import src.io.GameSettings;

/**
 * Recursos de jugabilidad. La interfaz continúa dibujándose con Java2D.
 * Esta edición incorpora naves originales y un fondo del paquete Space Shooter
 * entregado por el usuario, conservando un tamaño de colisión uniforme.
 */
public final class Assets {
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 38;

    private Assets() { }

    public static volatile boolean loaded = false;
    public static volatile float count = 0;
    public static final float MAX_COUNT = 40;

    public static BufferedImage player;
    public static BufferedImage doubleGunPlayer;
    private static BufferedImage[] playerVariants;
    private static BufferedImage[] doubleGunVariants;

    public static BufferedImage gameBackground;
    public static BufferedImage speed;
    public static BufferedImage[] shieldEffect = new BufferedImage[3];
    public static BufferedImage[] exp = new BufferedImage[9];
    public static BufferedImage blueLaser;
    public static BufferedImage greenLaser;
    public static BufferedImage redLaser;
    public static BufferedImage ufo;
    public static BufferedImage[] bigs = new BufferedImage[4];
    public static BufferedImage[] meds = new BufferedImage[2];
    public static BufferedImage[] smalls = new BufferedImage[2];
    public static BufferedImage[] tinies = new BufferedImage[2];
    public static Font fontBig;
    public static Font fontMed;
    public static Clip backgroundMusic;
    public static Clip explosion;
    public static Clip playerLoose;
    public static Clip playerShoot;
    public static Clip ufoShoot;
    public static Clip powerUp;

    public static synchronized void init() {
        if (loaded) return;
        count = 0;

        speed = loadImage("/effects/fire08.png");
        blueLaser = loadImage("/lasers/laserBlue01.png");
        greenLaser = loadImage("/lasers/laserGreen11.png");
        redLaser = loadImage("/lasers/laserRed01.png");
        ufo = loadImage("/ships/ufo.png");
        fontBig = loadFont("/fonts/futureFont.ttf", 42);
        fontMed = loadFont("/fonts/futureFont.ttf", 20);

        playerVariants = new BufferedImage[] {
                normalizeShip(loadImage("/ships/interceptor_blue.png")),
                normalizeShip(loadImage("/ships/scout_green.png")),
                normalizeShip(loadImage("/ships/falcon_orange.png")),
                normalizeShip(loadImage("/ships/vanguard_red.png")),
                createPhantomInterceptor()
        };
        doubleGunVariants = new BufferedImage[playerVariants.length];
        for (int i = 0; i < playerVariants.length; i++) {
            doubleGunVariants[i] = addDoubleCannons(playerVariants[i]);
        }
        player = playerVariants[0];
        doubleGunPlayer = doubleGunVariants[0];
        gameBackground = loadImage("/backgrounds/kenney_dark_purple.png");

        for (int i = 0; i < shieldEffect.length; i++) {
            shieldEffect[i] = loadImage("/effects/shield" + (i + 1) + ".png");
        }
        for (int i = 0; i < bigs.length; i++) bigs[i] = loadImage("/meteors/big" + (i + 1) + ".png");
        for (int i = 0; i < meds.length; i++) meds[i] = loadImage("/meteors/med" + (i + 1) + ".png");
        for (int i = 0; i < smalls.length; i++) smalls[i] = loadImage("/meteors/small" + (i + 1) + ".png");
        for (int i = 0; i < tinies.length; i++) tinies[i] = loadImage("/meteors/tiny" + (i + 1) + ".png");
        for (int i = 0; i < exp.length; i++) exp[i] = loadImage("/explosion/" + i + ".png");

        backgroundMusic = loadSound("/sounds/backgroundMusic.wav");
        explosion = loadSound("/sounds/explosion.wav");
        playerLoose = loadSound("/sounds/playerLoose.wav");
        playerShoot = loadSound("/sounds/playerShoot.wav");
        ufoShoot = loadSound("/sounds/ufoShoot.wav");
        powerUp = loadSound("/sounds/powerUp.wav");

        loaded = true;
    }

    public static BufferedImage getSelectedPlayer() {
        return getPlayerSprite(GameSettings.getShipStyle());
    }

    public static BufferedImage getPlayerSprite(GameSettings.ShipStyle style) {
        if (playerVariants == null) return player;
        return playerVariants[Math.min(style.ordinal(), playerVariants.length - 1)];
    }

    public static BufferedImage getDoubleGunSprite(GameSettings.ShipStyle style) {
        if (doubleGunVariants == null) return doubleGunPlayer;
        return doubleGunVariants[Math.min(style.ordinal(), doubleGunVariants.length - 1)];
    }

    public static BufferedImage getSelectedLaser() {
        switch (GameSettings.getLaserStyle()) {
            case GREEN: return greenLaser;
            case RED: return redLaser;
            case BLUE:
            default: return blueLaser;
        }
    }

    private static BufferedImage normalizeShip(BufferedImage source) {
        BufferedImage result = new BufferedImage(PLAYER_WIDTH, PLAYER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            double scale = Math.min(PLAYER_WIDTH / (double) source.getWidth(), PLAYER_HEIGHT / (double) source.getHeight());
            int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
            int x = (PLAYER_WIDTH - width) / 2;
            int y = (PLAYER_HEIGHT - height) / 2;
            g.drawImage(source, x, y, width, height, null);
        } finally {
            g.dispose();
        }
        return result;
    }

    /**
     * Nave adicional dibujada con Java2D para la edición Episode Phantom.
     * Su silueta de cabina central y alas gemelas es una referencia espacial
     * original; no utiliza gráficos externos de franquicias.
     */
    private static BufferedImage createPhantomInterceptor() {
        BufferedImage result = new BufferedImage(PLAYER_WIDTH, PLAYER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Propulsores: la nave apunta hacia la parte superior del sprite.
            g.setColor(new Color(77, 193, 255, 210));
            g.fillRoundRect(20, 29, 4, 7, 3, 3);
            g.fillRoundRect(26, 29, 4, 7, 3, 3);
            g.setColor(new Color(205, 242, 255, 220));
            g.fillRoundRect(21, 30, 2, 5, 2, 2);
            g.fillRoundRect(27, 30, 2, 5, 2, 2);

            // Alas laterales de un interceptor espacial de estética oscura.
            java.awt.Polygon leftWing = new java.awt.Polygon(
                    new int[] {2, 17, 20, 10}, new int[] {7, 10, 29, 35}, 4);
            java.awt.Polygon rightWing = new java.awt.Polygon(
                    new int[] {48, 33, 30, 40}, new int[] {7, 10, 29, 35}, 4);
            g.setColor(new Color(31, 40, 58));
            g.fillPolygon(leftWing);
            g.fillPolygon(rightWing);
            g.setColor(new Color(116, 136, 165));
            g.setStroke(new BasicStroke(1.2f));
            g.drawPolygon(leftWing);
            g.drawPolygon(rightWing);

            // Uniones de ala y fuselaje.
            g.setColor(new Color(83, 99, 127));
            g.fillRoundRect(14, 17, 22, 5, 4, 4);
            g.setColor(new Color(178, 194, 219));
            g.drawRoundRect(14, 17, 22, 5, 4, 4);

            // Fuselaje y cabina.
            java.awt.Polygon hull = new java.awt.Polygon(
                    new int[] {25, 31, 31, 25, 19, 19}, new int[] {2, 11, 25, 32, 25, 11}, 6);
            g.setColor(new Color(73, 90, 117));
            g.fillPolygon(hull);
            g.setColor(new Color(198, 216, 238));
            g.drawPolygon(hull);
            g.setColor(new Color(21, 32, 52));
            g.fillOval(20, 8, 10, 13);
            g.setColor(new Color(110, 222, 255));
            g.drawOval(20, 8, 10, 13);
            g.setColor(new Color(221, 246, 255, 210));
            g.drawLine(24, 10, 27, 15);

            // Líneas de panel para reforzar la lectura a escala pequeña.
            g.setColor(new Color(141, 165, 197));
            g.drawLine(5, 12, 15, 15);
            g.drawLine(45, 12, 35, 15);
            g.drawLine(8, 28, 17, 25);
            g.drawLine(42, 28, 33, 25);
        } finally {
            g.dispose();
        }
        return result;
    }

    /** Dibuja dos cañones discretos sobre cada modelo, sin depender de una UI PNG. */
    private static BufferedImage addDoubleCannons(BufferedImage ship) {
        BufferedImage result = new BufferedImage(ship.getWidth(), ship.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(ship, 0, 0, null);
            int cannonHeight = Math.max(9, ship.getHeight() / 3);
            int cannonWidth = Math.max(3, ship.getWidth() / 10);
            int top = Math.max(0, ship.getHeight() / 8);
            int left = Math.max(1, ship.getWidth() / 8 - cannonWidth / 2);
            int right = ship.getWidth() - ship.getWidth() / 8 - cannonWidth / 2;
            g.setColor(new Color(191, 232, 255));
            g.fillRoundRect(left, top, cannonWidth, cannonHeight, 3, 3);
            g.fillRoundRect(right, top, cannonWidth, cannonHeight, 3, 3);
            g.setColor(new Color(60, 149, 255));
            g.setStroke(new BasicStroke(1.15f));
            g.drawRoundRect(left, top, cannonWidth, cannonHeight, 3, 3);
            g.drawRoundRect(right, top, cannonWidth, cannonHeight, 3, 3);
        } finally {
            g.dispose();
        }
        return result;
    }

    private static BufferedImage loadImage(String path) { count++; return Loader.ImageLoader(path); }
    private static Font loadFont(String path, int size) { count++; return Loader.loadFont(path, size); }
    private static Clip loadSound(String path) { count++; return Loader.loadSound(path); }
}

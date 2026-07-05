package src.gameObjects;

import java.nio.file.Path;

public final class Constants {
    private Constants() { }

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;

    public static final long FIRERATE = 300L;
    public static final double DELTAANGLE = 0.10;
    public static final double ACC = 0.20;
    public static final double PLAYER_MAX_VEL = 7.0;
    public static final long FLICKER_TIME = 200L;
    public static final long SPAWNING_TIME = 3_000L;
    public static final long GAME_OVER_TIME = 3_000L;

    public static final double LASER_VEL = 15.0;

    public static final double METEOR_INIT_VEL = 2.0;
    public static final int METEOR_SCORE = 20;
    public static final double METEOR_MAX_VEL = 6.0;
    public static final int SHIELD_DISTANCE = 150;

    public static final int NODE_RADIUS = 160;
    public static final double UFO_MASS = 60.0;
    public static final int UFO_MAX_VEL = 3;
    public static final long UFO_FIRE_RATE = 1_000L;
    public static final double UFO_ANGLE_RANGE = Math.PI / 2.0;
    public static final int UFO_SCORE = 40;
    public static final long UFO_SPAWN_RATE = 10_000L;

    public static final String PLAY = "PLAY";
    public static final String EXIT = "EXIT";
    public static final String RETURN = "RETURN";
    public static final String HIGH_SCORES = "HIGHEST SCORES";
    public static final String SETTINGS = "CONFIGURACIÓN";
    public static final String HOW_TO_PLAY = "CÓMO JUGAR";
    public static final String PAUSE = "PAUSA";
    public static final String RESUME = "CONTINUAR";
    public static final String SCORE = "SCORE";
    public static final String DATE = "DATE";
    public static final int LOADING_BAR_WIDTH = 500;
    public static final int LOADING_BAR_HEIGHT = 50;

    public static final int POWER_UP_SIZE = 54;
    public static final long POWER_UP_DURATION = 10_000L;
    public static final long POWER_UP_SPAWN_TIME = 8_000L;
    public static final long SHIELD_TIME = 12_000L;
    public static final long DOUBLE_SCORE_TIME = 10_000L;
    public static final long FAST_FIRE_TIME = 14_000L;
    public static final long DOUBLE_GUN_TIME = 12_000L;
    public static final int SCORE_STACK = 1_000;

    public static final String PLAYER = "PLAYER";
    public static final String PLAYERS = "PLAYERS";
    public static final String SCORE_PATH = Path.of(System.getProperty("user.home"), "Space_Ship_Game", "data.json").toString();
    public static final String SCORE_XML_PATH = Path.of(System.getProperty("user.home"), "Space_Ship_Game", "data.xml").toString();
}

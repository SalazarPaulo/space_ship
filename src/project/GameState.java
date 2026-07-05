package src.project;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import src.gameObjects.Constants;
import src.gameObjects.Laser;
import src.gameObjects.Message;
import src.gameObjects.Meteor;
import src.gameObjects.MovingObject;
import src.gameObjects.Player;
import src.gameObjects.PowerUp;
import src.gameObjects.PowerUpTypes;
import src.gameObjects.Size;
import src.gameObjects.Ufo;
import src.graphics.Animation;
import src.graphics.Assets;
import src.graphics.Sound;
import src.input.KeyBoard;
import src.io.JSONParser;
import src.io.ScoreData;
import src.math.Vector2D;
import src.ui.Action;
import src.ui.Button;
import src.ui.UiRenderer;

/** Estado principal de juego con pausa, configuración rápida y HUD compacto. */
public class GameState extends State {
    public static final Vector2D PLAYER_START_POSITION = new Vector2D(
            Constants.WIDTH / 2.0 - Assets.player.getWidth() / 2.0,
            Constants.HEIGHT / 2.0 - Assets.player.getHeight() / 2.0);

    private final Player player;
    private final ArrayList<MovingObject> movingObjects = new ArrayList<>();
    private final ArrayList<MovingObject> pendingObjects = new ArrayList<>();
    private final ArrayList<Animation> explosions = new ArrayList<>();
    private final ArrayList<Message> messages = new ArrayList<>();
    private final Button pauseButton;
    private final Button settingsButton;

    private int score;
    private int lives = 3;
    private int meteors = 1;
    private int waves = 1;
    private long ufoSpawner;
    private long powerUpSpawner;
    private boolean gameOver;
    private boolean updatingObjects;
    private boolean pauseKeyHeld;
    private final Sound backgroundMusic;

    public GameState() {
        player = new Player(PLAYER_START_POSITION, new Vector2D(),
                Constants.PLAYER_MAX_VEL, Assets.getSelectedPlayer(), this);
        movingObjects.add(player);
        pauseButton = new Button(806, 12, 102, 38, Constants.PAUSE, Button.Icon.PAUSE,
                () -> State.changeState(new PauseState(this)));
        settingsButton = new Button(914, 12, 72, 38, "", Button.Icon.SETTINGS,
                () -> State.changeState(new SettingsState(this)));
        startWave();
        backgroundMusic = new Sound(Assets.backgroundMusic);
        backgroundMusic.changeVolume(-10.0f);
        if (Sound.isEnabled()) backgroundMusic.loop();
    }

    @Override
    public void update(float dt) {
        if (gameOver) return;

        if (KeyBoard.PAUSE && !pauseKeyHeld) {
            pauseKeyHeld = true;
            State.changeState(new PauseState(this));
            return;
        }
        if (!KeyBoard.PAUSE) pauseKeyHeld = false;

        pauseButton.update();
        settingsButton.update();
        if (State.getCurrentState() != this) return;

        ufoSpawner += dt;
        powerUpSpawner += dt;

        updatingObjects = true;
        for (int i = 0; i < movingObjects.size(); i++) {
            MovingObject object = movingObjects.get(i);
            if (!object.isDead()) object.update(dt);
            // Perder la última vida puede abrir GameOverState desde Player.
            // Salimos de inmediato para no seguir actualizando entidades de una partida terminada.
            if (State.getCurrentState() != this) {
                updatingObjects = false;
                return;
            }
        }
        updatingObjects = false;
        movingObjects.removeIf(MovingObject::isDead);
        flushPendingObjects();

        updateVisualEffects(dt);

        if (ufoSpawner >= Constants.UFO_SPAWN_RATE) {
            spawnUfo();
            ufoSpawner = 0;
        }
        if (powerUpSpawner >= Constants.POWER_UP_SPAWN_TIME) {
            spawnPowerUp();
            powerUpSpawner = 0;
        }
        if (!hasMeteor()) startWave();
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        drawGameBackground(g2d);
        for (MovingObject object : movingObjects) object.draw(graphics);
        for (Animation animation : explosions) {
            g2d.drawImage(animation.getCurrentFrame(), (int) animation.getPosition().getX(),
                    (int) animation.getPosition().getY(), null);
        }
        for (Message message : messages) message.draw(g2d);
        UiRenderer.drawHud(g2d, score, lives, Window.getAverageFps(), Assets.fontMed,
                player.isShieldOn(), player.isDoubleScoreOn(), player.isFastFireOn(), player.isDoubleGunOn());
        pauseButton.draw(g2d);
        settingsButton.draw(g2d);
    }

    private void drawGameBackground(Graphics2D graphics) {
        if (Assets.gameBackground != null) {
            for (int y = 0; y < Constants.HEIGHT; y += Assets.gameBackground.getHeight()) {
                for (int x = 0; x < Constants.WIDTH; x += Assets.gameBackground.getWidth()) {
                    graphics.drawImage(Assets.gameBackground, x, y, null);
                }
            }
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.58f));
            graphics.setColor(new Color(3, 9, 28));
            graphics.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
            graphics.setComposite(AlphaComposite.SrcOver);
        } else {
            graphics.setColor(new Color(3, 9, 26));
            graphics.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
        }
        for (int i = 0; i < 52; i++) {
            int x = Math.floorMod(i * 163 + 31, Constants.WIDTH);
            int y = Math.floorMod(i * 83 + 17, Constants.HEIGHT);
            graphics.setColor(new Color(180, 223, 255, 48 + (i % 5) * 25));
            graphics.fillRect(x, y, i % 11 == 0 ? 2 : 1, i % 11 == 0 ? 2 : 1);
        }
    }

    private void updateVisualEffects(float dt) {
        for (int i = 0; i < explosions.size(); i++) {
            Animation animation = explosions.get(i);
            animation.update(dt);
            if (!animation.isRunning()) explosions.remove(i--);
        }
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            message.update(dt);
            if (message.isDead()) messages.remove(i--);
        }
    }

    private boolean hasMeteor() {
        for (MovingObject object : movingObjects) {
            if (object instanceof Meteor && !object.isDead()) return true;
        }
        for (MovingObject object : pendingObjects) {
            if (object instanceof Meteor && !object.isDead()) return true;
        }
        return false;
    }

    /**
     * Elige la posición de reaparición con mayor separación respecto a amenazas
     * activas. Mantiene el centro cuando está libre y usa posiciones alternativas
     * cuando un meteorito, UFO o láser enemigo quedó en esa zona.
     */
    public Vector2D findSafeRespawnPosition(int playerWidth, int playerHeight) {
        Vector2D[] candidates = {
                new Vector2D(Constants.WIDTH / 2.0 - playerWidth / 2.0,
                        Constants.HEIGHT / 2.0 - playerHeight / 2.0),
                new Vector2D(140, 110),
                new Vector2D(Constants.WIDTH - playerWidth - 140, 110),
                new Vector2D(140, Constants.HEIGHT - playerHeight - 110),
                new Vector2D(Constants.WIDTH - playerWidth - 140,
                        Constants.HEIGHT - playerHeight - 110),
                new Vector2D(Constants.WIDTH / 2.0 - playerWidth / 2.0, 120),
                new Vector2D(Constants.WIDTH / 2.0 - playerWidth / 2.0,
                        Constants.HEIGHT - playerHeight - 120)
        };

        Vector2D best = new Vector2D(candidates[0]);
        double bestClearance = Double.NEGATIVE_INFINITY;
        for (Vector2D candidate : candidates) {
            double clearance = getRespawnClearance(candidate, playerWidth, playerHeight);
            if (clearance > bestClearance) {
                bestClearance = clearance;
                best = new Vector2D(candidate);
            }
        }
        return best;
    }

    private double getRespawnClearance(Vector2D candidate, int playerWidth, int playerHeight) {
        Vector2D candidateCenter = new Vector2D(
                candidate.getX() + playerWidth / 2.0,
                candidate.getY() + playerHeight / 2.0);
        double minimum = Double.POSITIVE_INFINITY;

        minimum = Math.min(minimum, clearanceAgainst(movingObjects, candidateCenter, playerWidth, playerHeight));
        minimum = Math.min(minimum, clearanceAgainst(pendingObjects, candidateCenter, playerWidth, playerHeight));
        return Double.isInfinite(minimum) ? Constants.WIDTH + Constants.HEIGHT : minimum;
    }

    private double clearanceAgainst(ArrayList<MovingObject> objects, Vector2D candidateCenter,
                                    int playerWidth, int playerHeight) {
        double minimum = Double.POSITIVE_INFINITY;
        double playerRadius = Math.max(playerWidth, playerHeight) / 2.0;

        for (MovingObject object : objects) {
            if (object == player || object.isDead() || !isRespawnThreat(object)) continue;
            double objectRadius = Math.max(object.getWidth(), object.getHeight()) / 2.0;
            double distance = object.getCenter().subtract(candidateCenter).getMagnitude();
            // Margen adicional para que la nave pueda arrancar sin recibir otro impacto inmediato.
            minimum = Math.min(minimum, distance - playerRadius - objectRadius - 96.0);
        }
        return minimum;
    }

    private boolean isRespawnThreat(MovingObject object) {
        if (object instanceof Meteor || object instanceof Ufo) return true;
        return object instanceof Laser && !((Laser) object).isPlayerOwned();
    }

    /** Reafirma el foco del Canvas al reaparecer la nave para que el teclado siga activo. */
    public void restorePlayerInputFocus() {
        Window.requestGameInputFocus();
    }

    public void addMovingObject(MovingObject object) {
        if (object == null) return;
        if (updatingObjects) pendingObjects.add(object);
        else movingObjects.add(object);
    }

    private void flushPendingObjects() {
        if (!pendingObjects.isEmpty()) {
            movingObjects.addAll(pendingObjects);
            pendingObjects.clear();
        }
    }

    /** Punto central de resolución de choques, sin modificar la lista durante su iteración. */
    public void resolveCollision(MovingObject first, MovingObject second) {
        if (first.isDead() || second.isDead() || gameOver) return;
        if (first instanceof Meteor && second instanceof Meteor) return;

        if (first instanceof PowerUp || second instanceof PowerUp) {
            resolvePowerUpCollision(first, second);
            return;
        }
        if (first instanceof Laser || second instanceof Laser) {
            resolveLaserCollision(first, second);
            return;
        }
        if (first instanceof Player || second instanceof Player) {
            Player ship = first instanceof Player ? (Player) first : (Player) second;
            MovingObject hazard = first instanceof Player ? second : first;
            if (ship.takeDamage()) destroyHazardWithoutReward(hazard);
            return;
        }

        first.destroy();
        second.destroy();
    }

    private void resolvePowerUpCollision(MovingObject first, MovingObject second) {
        PowerUp powerUp = first instanceof PowerUp ? (PowerUp) first : (PowerUp) second;
        MovingObject other = first instanceof PowerUp ? second : first;
        if (other instanceof Player && !other.isDead()) {
            powerUp.executeAction();
            powerUp.destroy();
        }
    }

    private void resolveLaserCollision(MovingObject first, MovingObject second) {
        Laser laser = first instanceof Laser ? (Laser) first : (Laser) second;
        MovingObject target = first instanceof Laser ? second : first;
        if (target instanceof Laser || target instanceof PowerUp) return;

        if (laser.isPlayerOwned()) {
            if (target instanceof Player) return;
            if (target instanceof Meteor) {
                laser.destroy();
                ((Meteor) target).destroy(true);
            } else if (target instanceof Ufo) {
                laser.destroy();
                ((Ufo) target).destroy(true);
            }
        } else if (target instanceof Player) {
            laser.destroy();
            ((Player) target).takeDamage();
        }
    }

    private void destroyHazardWithoutReward(MovingObject hazard) {
        if (hazard instanceof Meteor) ((Meteor) hazard).destroy(false);
        else if (hazard instanceof Ufo) ((Ufo) hazard).destroy(false);
        else hazard.destroy();
    }

    public void addScore(int value, Vector2D position) {
        Color color = Color.WHITE;
        String text = "+" + value + " SCORE";
        if (player.isDoubleScoreOn()) {
            value *= 2;
            text = "+" + value + " SCORE (X2)";
            color = Color.YELLOW;
        }
        score += value;
        messages.add(new Message(position, true, text, color, false, Assets.fontMed));
    }

    public void addMessage(Vector2D position, String text, Color color) {
        messages.add(new Message(position, true, text, color, false, Assets.fontMed));
    }

    public void divideMeteor(Meteor meteor) {
        Size size = meteor.getSize();
        if (size == Size.TINY || size.textures == null) return;
        Size newSize;
        switch (size) {
            case BIG: newSize = Size.MED; break;
            case MED: newSize = Size.SMALL; break;
            case SMALL: newSize = Size.TINY; break;
            default: return;
        }
        for (int i = 0; i < size.quantity; i++) {
            BufferedImage texture = size.textures[(int) (Math.random() * size.textures.length)];
            addMovingObject(new Meteor(meteor.getPosition(),
                    new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2.0),
                    Constants.METEOR_INIT_VEL * Math.random() + 1,
                    texture, this, newSize));
        }
    }

    private void startWave() {
        messages.add(new Message(new Vector2D(Constants.WIDTH / 2.0, Constants.HEIGHT / 2.0), false,
                "WAVE " + waves, Color.WHITE, true, Assets.fontBig));
        for (int i = 0; i < meteors; i++) {
            double x = i % 2 == 0 ? Math.random() * Constants.WIDTH : 0;
            double y = i % 2 == 0 ? 0 : Math.random() * Constants.HEIGHT;
            BufferedImage texture = Assets.bigs[(int) (Math.random() * Assets.bigs.length)];
            addMovingObject(new Meteor(new Vector2D(x, y),
                    new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2.0),
                    Constants.METEOR_INIT_VEL * Math.random() + 1,
                    texture, this, Size.BIG));
        }
        meteors++;
        waves++;
    }

    public void playExplosion(Vector2D position) {
        explosions.add(new Animation(Assets.exp, 50,
                position.subtract(new Vector2D(Assets.exp[0].getWidth() / 2.0, Assets.exp[0].getHeight() / 2.0))));
    }

    private void spawnUfo() {
        int side = (int) (Math.random() * 2);
        double x = side == 0 ? Math.random() * Constants.WIDTH : Constants.WIDTH;
        double y = side == 0 ? Constants.HEIGHT : Math.random() * Constants.HEIGHT;
        ArrayList<Vector2D> path = new ArrayList<>();
        path.add(new Vector2D(Math.random() * Constants.WIDTH / 2.0, Math.random() * Constants.HEIGHT / 2.0));
        path.add(new Vector2D(Math.random() * Constants.WIDTH / 2.0 + Constants.WIDTH / 2.0, Math.random() * Constants.HEIGHT / 2.0));
        path.add(new Vector2D(Math.random() * Constants.WIDTH / 2.0, Math.random() * Constants.HEIGHT / 2.0 + Constants.HEIGHT / 2.0));
        path.add(new Vector2D(Math.random() * Constants.WIDTH / 2.0 + Constants.WIDTH / 2.0,
                Math.random() * Constants.HEIGHT / 2.0 + Constants.HEIGHT / 2.0));
        addMovingObject(new Ufo(new Vector2D(x, y), new Vector2D(), Constants.UFO_MAX_VEL, Assets.ufo, path, this));
    }

    private void spawnPowerUp() {
        final Vector2D position = new Vector2D(
                (int) ((Constants.WIDTH - Constants.POWER_UP_SIZE) * Math.random()),
                (int) ((Constants.HEIGHT - Constants.POWER_UP_SIZE) * Math.random()));
        final PowerUpTypes type = PowerUpTypes.values()[(int) (Math.random() * PowerUpTypes.values().length)];
        final Action action;
        switch (type) {
            case LIFE:
                action = () -> { lives++; addMessage(position, type.text, Color.GREEN); };
                break;
            case SHIELD:
                action = () -> { player.setShield(); addMessage(position, type.text, Color.CYAN); };
                break;
            case SCORE_X2:
                action = () -> { player.setDoubleScore(); addMessage(position, type.text, Color.YELLOW); };
                break;
            case FASTER_FIRE:
                action = () -> { player.setFastFire(); addMessage(position, type.text, Color.BLUE); };
                break;
            case SCORE_STACK:
                action = () -> { score += Constants.SCORE_STACK; addMessage(position, type.text, Color.MAGENTA); };
                break;
            case DOUBLE_GUN:
                action = () -> { player.setDoubleGun(); addMessage(position, type.text, Color.ORANGE); };
                break;
            default:
                return;
        }
        addMovingObject(new PowerUp(position, type, action, this));
    }

    public boolean subtractLife(Vector2D position) {
        lives--;
        messages.add(new Message(position, false, "-1 LIFE", Color.RED, false, Assets.fontMed));
        if (lives > 0) return true;
        gameOver();
        return false;
    }

    /** Congela la partida y solicita el nombre del piloto antes de guardar el resultado. */
    public void gameOver() {
        if (gameOver) return;
        gameOver = true;
        backgroundMusic.stop();
        State.changeState(new GameOverState(this, score));
    }

    public void onSettingsChanged() {
        if (Sound.isEnabled()) backgroundMusic.loop();
        else backgroundMusic.stop();
    }

    public void quitToMenu() { backgroundMusic.stop(); }

    /** Guarda una vez el resultado de una partida después de que el jugador indique su nombre. */
    public void saveScore(String playerName) {
        try {
            ArrayList<ScoreData> scores = JSONParser.readFile();
            scores.add(new ScoreData(score, playerName));
            JSONParser.writeFile(scores);
        } catch (IOException ignored) {
            // La partida termina aunque el sistema no pueda escribir en el perfil local.
        }
    }

    public ArrayList<MovingObject> getMovingObjects() { return movingObjects; }
    public ArrayList<Message> getMessages() { return messages; }
    public Player getPlayer() { return player; }
    public int getScore() { return score; }
}

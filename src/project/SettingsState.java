package src.project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.graphics.Text;
import src.input.MouseInput;
import src.io.GameSettings;
import src.math.Vector2D;
import src.ui.Button;
import src.ui.UiRenderer;

/** Configuración disponible desde el menú y durante una partida. */
public class SettingsState extends State {
    private final State returnState;
    private final Button previousShip;
    private final Button nextShip;
    private final Button previousLaser;
    private final Button nextLaser;
    private final Button soundButton;
    private final Button howToPlayButton;
    private final Button backButton;

    public SettingsState(State returnState) {
        this.returnState = returnState;
        previousShip = new Button(650, 146, 52, 46, "", Button.Icon.PREVIOUS, GameSettings::previousShip);
        nextShip = new Button(712, 146, 52, 46, "", Button.Icon.NEXT, GameSettings::nextShip);
        previousLaser = new Button(650, 222, 52, 46, "", Button.Icon.PREVIOUS, GameSettings::previousLaser);
        nextLaser = new Button(712, 222, 52, 46, "", Button.Icon.NEXT, GameSettings::nextLaser);
        soundButton = new Button(610, 298, 154, 46, "", Button.Icon.SOUND, GameSettings::toggleSound);
        howToPlayButton = new Button(326, 366, 348, 54, Constants.HOW_TO_PLAY, Button.Icon.HELP,
                () -> State.changeState(new HowToPlayState(this)));
        backButton = new Button(34, Constants.HEIGHT - 76, 218, 48, Constants.RETURN, Button.Icon.BACK,
                this::returnToCaller);
    }

    @Override
    public void update(float dt) {
        // Los estados visuales se actualizan todos, pero el clic se consume una sola vez.
        // Así una flecha de láser no puede ejecutar una acción de nave por accidente.
        previousShip.refreshHover();
        nextShip.refreshHover();
        previousLaser.refreshHover();
        nextLaser.refreshHover();
        soundButton.refreshHover();
        howToPlayButton.refreshHover();
        backButton.refreshHover();

        if (!MouseInput.consumeLeftClick()) return;

        int x = MouseInput.X;
        int y = MouseInput.Y;
        if (previousShip.contains(x, y)) previousShip.activate();
        else if (nextShip.contains(x, y)) nextShip.activate();
        else if (previousLaser.contains(x, y)) previousLaser.activate();
        else if (nextLaser.contains(x, y)) nextLaser.activate();
        else if (soundButton.contains(x, y)) soundButton.activate();
        else if (howToPlayButton.contains(x, y)) howToPlayButton.activate();
        else if (backButton.contains(x, y)) backButton.activate();
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        UiRenderer.drawMenuBackground(g);
        UiRenderer.drawScreenCard(g, 172, 54, 656, 500);
        Text.drawText(g, Constants.SETTINGS, new Vector2D(Constants.WIDTH / 2.0, 103),
                true, new Color(164, 222, 255), Assets.fontBig);
        Text.drawText(g, "PERSONALIZA TU CABINA", new Vector2D(Constants.WIDTH / 2.0, 133),
                true, new Color(151, 201, 236), Assets.fontMed);

        GameSettings.ShipStyle shipStyle = GameSettings.getShipStyle();
        GameSettings.LaserStyle laserStyle = GameSettings.getLaserStyle();
        BufferedImage ship = Assets.getPlayerSprite(shipStyle);
        Color laserColor = getLaserColor(laserStyle);

        UiRenderer.drawSettingRow(g, 230, 145, 390, "NAVE", shipStyle.label(), Button.Icon.SHIP, Assets.fontMed, true);
        UiRenderer.drawChoicePreview(g, 445, 149, 160, 50, ship, laserColor);
        previousShip.draw(g);
        nextShip.draw(g);

        UiRenderer.drawSettingRow(g, 230, 221, 390, "LÁSER", laserStyle.label(), Button.Icon.LASER, Assets.fontMed, true);
        UiRenderer.drawChoicePreview(g, 445, 225, 160, 50, null, laserColor);
        previousLaser.draw(g);
        nextLaser.draw(g);

        boolean soundOn = GameSettings.isSoundEnabled();
        UiRenderer.drawSettingRow(g, 230, 297, 365, "SONIDO", soundOn ? "ACTIVADO" : "SILENCIADO",
                Button.Icon.SOUND, Assets.fontMed, soundOn);
        soundButton.draw(g);
        // El estado queda a la derecha del icono para no superponerse con él.
        Text.drawText(g, soundOn ? "ON" : "OFF", new Vector2D(713, 327), false,
                soundOn ? Color.WHITE : new Color(255, 202, 202), Assets.fontMed.deriveFont(11f));
        howToPlayButton.draw(g);
        backButton.draw(g);
    }

    public void returnToCaller() {
        if (returnState instanceof GameState) ((GameState) returnState).onSettingsChanged();
        if (returnState instanceof PauseState) ((PauseState) returnState).onSettingsChanged();
        State.changeState(returnState);
    }

    private Color getLaserColor(GameSettings.LaserStyle style) {
        switch (style) {
            case GREEN: return new Color(107, 245, 149);
            case RED: return new Color(255, 104, 120);
            case BLUE:
            default: return new Color(100, 194, 255);
        }
    }
}

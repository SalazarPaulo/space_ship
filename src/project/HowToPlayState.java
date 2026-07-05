package src.project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.graphics.Text;
import src.math.Vector2D;
import src.ui.Button;
import src.ui.UiRenderer;

/** Pantalla de controles accesible únicamente desde Configuración. */
public class HowToPlayState extends State {
    private final SettingsState settingsState;
    private final Button backButton;

    public HowToPlayState(SettingsState settingsState) {
        this.settingsState = settingsState;
        backButton = new Button(34, Constants.HEIGHT - 76, 218, 48, Constants.RETURN, Button.Icon.BACK,
                () -> State.changeState(settingsState));
    }

    @Override
    public void update(float dt) { backButton.update(); }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        UiRenderer.drawMenuBackground(g);
        UiRenderer.drawScreenCard(g, 188, 54, 624, 490);
        Text.drawText(g, Constants.HOW_TO_PLAY, new Vector2D(Constants.WIDTH / 2.0, 108),
                true, new Color(164, 222, 255), Assets.fontBig);
        Text.drawText(g, "CONTROLES DE VUELO", new Vector2D(Constants.WIDTH / 2.0, 141),
                true, new Color(151, 201, 236), Assets.fontMed);

        drawCommand(g, 245, 185, "W  /  ↑", "Acelerar la nave");
        drawCommand(g, 245, 245, "A  /  ←", "Girar a la izquierda");
        drawCommand(g, 245, 305, "D  /  →", "Girar a la derecha");
        drawCommand(g, 245, 365, "ESPACIO", "Disparar láser");
        drawCommand(g, 245, 425, "P  /  ESC", "Pausar la partida");
        backButton.draw(g);
    }

    private void drawCommand(Graphics2D g, int x, int y, String key, String description) {
        UiRenderer.drawScreenCard(g, x, y - 29, 170, 44);
        Text.drawText(g, key, new Vector2D(x + 85, y), true, Color.WHITE, Assets.fontMed);
        Text.drawText(g, description, new Vector2D(x + 205, y), false, new Color(220, 239, 255), Assets.fontMed);
    }
}

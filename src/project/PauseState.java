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

/** Superposición de pausa: la partida queda intacta y puede retomarse. */
public class PauseState extends State {
    private final GameState gameState;
    private final Button resumeButton;
    private final Button settingsButton;
    private final Button menuButton;

    public PauseState(GameState gameState) {
        this.gameState = gameState;
        int x = Constants.WIDTH / 2 - 155;
        resumeButton = new Button(x, 278, 310, 52, Constants.RESUME, Button.Icon.RESUME,
                () -> State.changeState(gameState));
        settingsButton = new Button(x, 343, 310, 52, Constants.SETTINGS, Button.Icon.SETTINGS,
                () -> State.changeState(new SettingsState(this)));
        menuButton = new Button(x, 408, 310, 52, "MENÚ PRINCIPAL", Button.Icon.HOME,
                () -> { gameState.quitToMenu(); State.changeState(new MenuState()); });
    }

    @Override
    public void update(float dt) {
        resumeButton.update();
        settingsButton.update();
        menuButton.update();
    }

    public void onSettingsChanged() { gameState.onSettingsChanged(); }

    @Override
    public void draw(Graphics graphics) {
        gameState.draw(graphics);
        Graphics2D g = (Graphics2D) graphics;
        UiRenderer.drawPauseOverlay(g);
        UiRenderer.drawScreenCard(g, 320, 152, 360, 348);
        Text.drawText(g, Constants.PAUSE, new Vector2D(Constants.WIDTH / 2.0, 220),
                true, new Color(170, 226, 255), Assets.fontBig);
        Text.drawText(g, "LA PARTIDA ESTÁ DETENIDA", new Vector2D(Constants.WIDTH / 2.0, 250),
                true, new Color(205, 228, 246), Assets.fontMed);
        resumeButton.draw(g);
        settingsButton.draw(g);
        menuButton.draw(g);
    }
}

package src.project;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.graphics.Text;
import src.math.Vector2D;
import src.ui.Button;
import src.ui.UiRenderer;

/** Menú principal con fondo espacial y accesos a partida, opciones y puntajes. */
public class MenuState extends State {
    private final ArrayList<Button> buttons = new ArrayList<>();

    public MenuState() {
        int width = 350;
        int height = 54;
        int x = Constants.WIDTH / 2 - width / 2;
        int firstY = 230;
        int gap = 13;
        buttons.add(new Button(x, firstY, width, height, Constants.PLAY, Button.Icon.PLAY,
                () -> State.changeState(new GameState())));
        buttons.add(new Button(x, firstY + (height + gap), width, height, Constants.SETTINGS, Button.Icon.SETTINGS,
                () -> State.changeState(new SettingsState(this))));
        buttons.add(new Button(x, firstY + (height + gap) * 2, width, height, Constants.HIGH_SCORES, Button.Icon.TROPHY,
                () -> State.changeState(new ScoreState())));
        buttons.add(new Button(x, firstY + (height + gap) * 3, width, height, Constants.EXIT, Button.Icon.EXIT,
                () -> System.exit(0)));
    }

    @Override
    public void update(float dt) {
        for (Button button : buttons) button.update();
    }

    @Override
    public void draw(Graphics graphics) {
        UiRenderer.drawMenuBackground((java.awt.Graphics2D) graphics);
        UiRenderer.drawScreenCard((java.awt.Graphics2D) graphics, 286, 70, 428, 455);
        Text.drawText(graphics, "SPACE SHIP", new Vector2D(Constants.WIDTH / 2.0, 126),
                true, new Color(162, 222, 255), Assets.fontBig);
        Text.drawText(graphics, "GAME", new Vector2D(Constants.WIDTH / 2.0, 172),
                true, Color.WHITE, Assets.fontBig);
        Text.drawText(graphics, "PILOTA · SOBREVIVE · SUPERA TU MARCA",
                new Vector2D(Constants.WIDTH / 2.0, 205), true, new Color(151, 203, 239),
                Assets.fontMed.deriveFont(Math.max(10f, Assets.fontMed.getSize2D() - 3f)));
        for (Button button : buttons) button.draw(graphics);
        Text.drawText(graphics, "Episode Phantom",
                new Vector2D(Constants.WIDTH / 2.0, 550), true, new Color(140, 181, 220), Assets.fontMed);
    }
}

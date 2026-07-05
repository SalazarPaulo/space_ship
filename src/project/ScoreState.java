package src.project;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.graphics.Text;
import src.io.JSONParser;
import src.io.ScoreData;
import src.math.Vector2D;
import src.ui.Button;
import src.ui.UiRenderer;

/** Tabla fija con las diez mejores puntuaciones registradas. */
public class ScoreState extends State {
    private static final int MAX_VISIBLE_SCORES = 10;

    private final Button returnButton;
    private final ArrayList<ScoreData> highScores;

    public ScoreState() {
        returnButton = new Button(30, Constants.HEIGHT - 82, 218, 54, Constants.RETURN, Button.Icon.BACK,
                () -> State.changeState(new MenuState()));
        highScores = JSONParser.readFile();
        highScores.sort(Comparator.comparingInt(ScoreData::getScore).reversed());
        if (highScores.size() > MAX_VISIBLE_SCORES) {
            highScores.subList(MAX_VISIBLE_SCORES, highScores.size()).clear();
        }
    }

    @Override
    public void update(float dt) {
        returnButton.update();
    }

    @Override
    public void draw(Graphics graphics) {
        UiRenderer.drawMenuBackground((java.awt.Graphics2D) graphics);
        UiRenderer.drawScreenCard((java.awt.Graphics2D) graphics, 150, 42, 700, 500);
        returnButton.draw(graphics);

        Text.drawText(graphics, Constants.HIGH_SCORES, new Vector2D(Constants.WIDTH / 2.0, 84),
                true, new Color(135, 205, 255), Assets.fontBig);
        Text.drawText(graphics, "TOP " + MAX_VISIBLE_SCORES, new Vector2D(Constants.WIDTH / 2.0, 111),
                true, new Color(151, 203, 239), Assets.fontMed.deriveFont(12f));

        double rankX = 240;
        double nameX = 410;
        double scoreX = 590;
        double dateX = 735;
        double headerY = 150;
        Text.drawText(graphics, "#", new Vector2D(rankX, headerY), true, Color.CYAN, Assets.fontMed);
        Text.drawText(graphics, "PILOTO", new Vector2D(nameX, headerY), true, Color.CYAN, Assets.fontMed);
        Text.drawText(graphics, "SCORE", new Vector2D(scoreX, headerY), true, Color.CYAN, Assets.fontMed);
        Text.drawText(graphics, "FECHA", new Vector2D(dateX, headerY), true, Color.CYAN, Assets.fontMed);

        if (highScores.isEmpty()) {
            Text.drawText(graphics, "Aún no hay puntajes guardados.", new Vector2D(Constants.WIDTH / 2.0, 230),
                    true, Color.WHITE, Assets.fontMed);
            return;
        }

        double rowY = 190;
        for (int i = 0; i < highScores.size(); i++) {
            ScoreData data = highScores.get(i);
            Text.drawText(graphics, Integer.toString(i + 1), new Vector2D(rankX, rowY), true,
                    new Color(170, 214, 244), Assets.fontMed);
            Text.drawText(graphics, data.getName(), new Vector2D(nameX, rowY), true,
                    Color.WHITE, Assets.fontMed);
            Text.drawText(graphics, Integer.toString(data.getScore()), new Vector2D(scoreX, rowY), true,
                    Color.WHITE, Assets.fontMed);
            Text.drawText(graphics, data.getDate(), new Vector2D(dateX, rowY), true,
                    new Color(210, 226, 242), Assets.fontMed);
            rowY += 32;
        }
    }
}

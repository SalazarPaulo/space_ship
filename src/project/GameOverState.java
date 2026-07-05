package src.project;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.graphics.Text;
import src.input.KeyBoard;
import src.math.Vector2D;
import src.ui.Button;
import src.ui.UiRenderer;

/** Pantalla final: permite registrar el nombre del piloto antes de continuar. */
public final class GameOverState extends State {
    private static final int MAX_NAME_LENGTH = 16;

    private final GameState completedGame;
    private final int finalScore;
    private final Button playAgainButton;
    private final Button mainMenuButton;
    private final StringBuilder pilotName = new StringBuilder();
    private boolean scoreSaved;

    public GameOverState(GameState completedGame, int finalScore) {
        this.completedGame = completedGame;
        this.finalScore = Math.max(0, finalScore);
        int buttonX = Constants.WIDTH / 2 - 155;
        playAgainButton = new Button(buttonX, 382, 310, 52, "JUGAR DE NUEVO", Button.Icon.PLAY,
                () -> {
                    saveScoreIfNeeded();
                    State.changeState(new GameState());
                });
        mainMenuButton = new Button(buttonX, 447, 310, 52, "PANTALLA PRINCIPAL", Button.Icon.HOME,
                () -> {
                    saveScoreIfNeeded();
                    completedGame.quitToMenu();
                    State.changeState(new MenuState());
                });
        KeyBoard.clearTextInput();
    }

    @Override
    public void update(float dt) {
        updateNameInput();
        playAgainButton.update();
        mainMenuButton.update();
    }

    private void updateNameInput() {
        if (KeyBoard.consumeBackspace() && pilotName.length() > 0) {
            pilotName.deleteCharAt(pilotName.length() - 1);
        }
        String typed = KeyBoard.consumeTypedText();
        for (int i = 0; i < typed.length() && pilotName.length() < MAX_NAME_LENGTH; i++) {
            char value = typed.charAt(i);
            if (Character.isLetterOrDigit(value) || value == ' ' || value == '-' || value == '_') {
                pilotName.append(value);
            }
        }
    }

    private void saveScoreIfNeeded() {
        if (scoreSaved) return;
        completedGame.saveScore(pilotName.toString());
        scoreSaved = true;
    }

    @Override
    public void draw(Graphics graphics) {
        completedGame.draw(graphics);
        Graphics2D g = (Graphics2D) graphics;
        UiRenderer.drawPauseOverlay(g);
        UiRenderer.drawScreenCard(g, 300, 82, 400, 452);

        Text.drawText(g, "GAME OVER", new Vector2D(Constants.WIDTH / 2.0, 143),
                true, new Color(255, 139, 157), Assets.fontBig);
        Text.drawText(g, "PUNTUACIÓN OBTENIDA", new Vector2D(Constants.WIDTH / 2.0, 190),
                true, new Color(170, 214, 244), Assets.fontMed);
        Text.drawText(g, String.format("%06d", finalScore), new Vector2D(Constants.WIDTH / 2.0, 226),
                true, Color.WHITE, Assets.fontBig);
        Text.drawText(g, "NOMBRE DEL PILOTO", new Vector2D(Constants.WIDTH / 2.0, 274),
                true, new Color(151, 203, 239), Assets.fontMed.deriveFont(14f));
        drawNameField(g);
        Text.drawText(g, "ESCRIBE TU NOMBRE Y ELIGE UNA OPCIÓN", new Vector2D(Constants.WIDTH / 2.0, 354),
                true, new Color(151, 203, 239), Assets.fontMed.deriveFont(12f));

        playAgainButton.draw(g);
        mainMenuButton.draw(g);
    }

    private void drawNameField(Graphics2D graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = 335;
            int y = 292;
            int width = 330;
            int height = 46;
            g.setColor(new Color(2, 8, 22, 220));
            g.fillRoundRect(x, y, width, height, 14, 14);
            g.setColor(new Color(118, 211, 255, 225));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(x, y, width, height, 14, 14);

            String value = pilotName.length() == 0 ? "PILOTO" : pilotName.toString();
            boolean placeholder = pilotName.length() == 0;
            g.setFont(Assets.fontMed.deriveFont(18f));
            FontMetrics metrics = g.getFontMetrics();
            g.setColor(placeholder ? new Color(145, 170, 202) : Color.WHITE);
            int textX = x + 16;
            int textY = y + (height - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(value, textX, textY);
            if (!placeholder) {
                int caretX = textX + metrics.stringWidth(value) + 2;
                g.setColor(new Color(112, 223, 255));
                g.drawLine(caretX, y + 11, caretX, y + height - 11);
            }
        } finally {
            g.dispose();
        }
    }
}

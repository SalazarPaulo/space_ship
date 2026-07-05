package src.project;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.graphics.Loader;
import src.graphics.Text;
import src.math.Vector2D;
import src.ui.UiRenderer;

public class LoadingState extends State {
    private final Thread loadingThread;
    private final Font font;

    public LoadingState(Thread loadingThread) {
        this.loadingThread = loadingThread;
        this.font = Loader.loadFont("/fonts/futureFont.ttf", 38);
        this.loadingThread.start();
    }

    @Override
    public void update(float dt) {
        if (!Assets.loaded) return;
        try {
            loadingThread.join();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        State.changeState(new MenuState());
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        UiRenderer.drawMenuBackground(g2d);
        UiRenderer.drawScreenCard(g2d, 210, 175, 580, 245);
        int x = Constants.WIDTH / 2 - Constants.LOADING_BAR_WIDTH / 2;
        int y = Constants.HEIGHT / 2 - Constants.LOADING_BAR_HEIGHT / 2;
        float progress = Math.min(1f, Assets.count / Assets.MAX_COUNT);
        g2d.setPaint(new GradientPaint(x, y, Color.WHITE, x + Constants.LOADING_BAR_WIDTH,
                y + Constants.LOADING_BAR_HEIGHT, Color.BLUE));
        g2d.fillRect(x, y, (int) (Constants.LOADING_BAR_WIDTH * progress), Constants.LOADING_BAR_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, Constants.LOADING_BAR_WIDTH, Constants.LOADING_BAR_HEIGHT);
        Text.drawText(g2d, "SPACE SHIP GAME", new Vector2D(Constants.WIDTH / 2.0, Constants.HEIGHT / 2.0 - 55),
                true, Color.WHITE, font);
        Text.drawText(g2d, "LOADING... " + (int) (progress * 100) + "%",
                new Vector2D(Constants.WIDTH / 2.0, Constants.HEIGHT / 2.0 + 50), true, Color.WHITE, font);
    }
}

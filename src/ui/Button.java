package src.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import src.graphics.Assets;
import src.input.MouseInput;

/** Botón lógico del lienzo 1000x600, pintado completamente con Java2D. */
public class Button {
    public enum Icon {
        PLAY, TROPHY, EXIT, BACK, SETTINGS, PAUSE, RESUME, HOME,
        PREVIOUS, NEXT, SOUND, SHIP, LASER, HELP
    }

    private final Rectangle bounds;
    private final Action action;
    private final String text;
    private final Icon icon;
    private boolean hovered;

    public Button(int x, int y, int width, int height, String text, Icon icon, Action action) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
        this.icon = icon;
        this.action = action;
    }

    public void update() {
        refreshHover();
        if (hovered && MouseInput.consumeLeftClick()) activate();
    }

    /** Permite que una pantalla resuelva una única acción por clic. */
    public void refreshHover() {
        hovered = bounds.contains(MouseInput.X, MouseInput.Y);
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public void activate() {
        if (action != null) action.doAction();
    }

    public void draw(Graphics graphics) {
        if (graphics instanceof Graphics2D) {
            UiRenderer.drawButton((Graphics2D) graphics, bounds, text, icon, hovered, Assets.fontMed);
        }
    }
}

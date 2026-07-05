package src.gameObjects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import src.graphics.Text;
import src.math.Vector2D;

public class Message {
    private float alpha;
    private final String text;
    private final Vector2D position;
    private final Color color;
    private final boolean center;
    private boolean fade;
    private final Font font;
    private boolean dead;

    public Message(Vector2D position, boolean fade, String text, Color color,
                   boolean center, Font font) {
        this.text = text;
        this.position = new Vector2D(position);
        this.fade = fade;
        this.color = color;
        this.center = center;
        this.font = font;
        this.alpha = fade ? 1f : 0f;
    }

    public void update(float dt) {
        position.setY(position.getY() - dt * 0.06);
        float deltaAlpha = dt * 0.0006f;
        if (fade) alpha -= deltaAlpha;
        else alpha += deltaAlpha;
        if (!fade && alpha >= 1f) { alpha = 1f; fade = true; }
        if (fade && alpha <= 0f) dead = true;
    }

    public void draw(Graphics2D graphics) {
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
        Text.drawText(graphics, text, position, center, color, font);
        graphics.setComposite(AlphaComposite.SrcOver);
    }

    public boolean isDead() { return dead; }
}

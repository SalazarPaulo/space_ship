package src.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import src.math.Vector2D;

public final class Text {
    private Text() { }

    public static void drawText(Graphics graphics, String text, Vector2D position,
                                boolean center, Color color, Font font) {
        graphics.setColor(color);
        graphics.setFont(font);
        double x = position.getX();
        double y = position.getY();
        if (center) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            x -= metrics.stringWidth(text) / 2.0;
            y += metrics.getAscent() / 2.0 - metrics.getDescent();
        }
        graphics.drawString(text, (int) x, (int) y);
    }
}

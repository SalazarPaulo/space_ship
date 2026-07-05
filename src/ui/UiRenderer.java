package src.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import src.gameObjects.Constants;
import src.gameObjects.PowerUpTypes;

/** Interfaz completa dibujada con Java2D; no usa imágenes para botones ni HUD. */
public final class UiRenderer {
    private static final Color PANEL = new Color(8, 18, 42, 224);
    private static final Color PANEL_BORDER = new Color(119, 205, 255, 220);
    private static final Color ACCENT = new Color(78, 203, 255);
    private static final Color TEXT = new Color(238, 247, 255);
    private static final Color MUTED = new Color(155, 191, 224);
    private static final Color TITLE_START = new Color(13, 40, 84);
    private static final Color TITLE_END = new Color(7, 17, 42);

    private UiRenderer() { }

    public static void drawMenuBackground(Graphics2D graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            g.setPaint(new GradientPaint(0, 0, new Color(5, 12, 32), 0, Constants.HEIGHT, new Color(18, 46, 90)));
            g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
            g.setColor(new Color(34, 151, 255));
            g.fill(new Ellipse2D.Double(-160, 145, 520, 360));
            g.setColor(new Color(176, 82, 255));
            g.fill(new Ellipse2D.Double(650, -160, 430, 310));
            g.setColor(new Color(85, 235, 201));
            g.fill(new Ellipse2D.Double(680, 380, 370, 270));
            g.setComposite(AlphaComposite.SrcOver);

            drawStarField(g);
            drawPerspectiveGrid(g);
            drawDecorativeShip(g, 770, 230, 1.3);
            drawDecorativePlanet(g, 125, 420, 74, new Color(84, 134, 232));

            g.setColor(new Color(0, 0, 0, 62));
            g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
        } finally {
            g.dispose();
        }
    }

    public static void drawScreenCard(Graphics2D graphics, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            g.setColor(new Color(0, 0, 0, 105));
            g.fillRoundRect(x + 5, y + 8, width, height, 28, 28);
            g.setPaint(new GradientPaint(x, y, new Color(10, 30, 68, 238), x, y + height, new Color(5, 12, 31, 244)));
            g.fillRoundRect(x, y, width, height, 28, 28);
            g.setColor(new Color(132, 212, 255, 205));
            g.setStroke(new BasicStroke(1.45f));
            g.drawRoundRect(x, y, width, height, 28, 28);
        } finally {
            g.dispose();
        }
    }

    public static void drawButton(Graphics2D graphics, Rectangle bounds, String text,
                                  Button.Icon icon, boolean hovered, Font font) {
        Graphics2D g2d = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g2d);
            int arc = Math.min(22, bounds.height / 2);
            int lift = hovered ? 2 : 0;
            Color start = hovered ? new Color(71, 184, 255) : new Color(30, 76, 138);
            Color end = hovered ? new Color(12, 99, 181) : new Color(12, 36, 77);
            if (icon == Button.Icon.EXIT) {
                start = hovered ? new Color(244, 110, 130) : new Color(133, 47, 70);
                end = hovered ? new Color(173, 50, 75) : new Color(75, 26, 49);
            }

            g2d.setColor(new Color(0, 0, 0, 115));
            g2d.fillRoundRect(bounds.x + 3, bounds.y + 5, bounds.width, bounds.height, arc, arc);
            g2d.setPaint(new GradientPaint(bounds.x, bounds.y - lift, start,
                    bounds.x, bounds.y + bounds.height - lift, end));
            g2d.fillRoundRect(bounds.x, bounds.y - lift, bounds.width, bounds.height, arc, arc);
            g2d.setColor(hovered ? Color.WHITE : new Color(159, 220, 255));
            g2d.setStroke(new BasicStroke(hovered ? 2.25f : 1.35f));
            g2d.drawRoundRect(bounds.x, bounds.y - lift, bounds.width, bounds.height, arc, arc);

            int iconCenterX = text == null || text.isBlank() ? bounds.x + bounds.width / 2 : bounds.x + 30;
            int iconCenterY = bounds.y - lift + bounds.height / 2;
            drawMenuIcon(g2d, icon, iconCenterX, iconCenterY, Math.min(20, bounds.height / 2), TEXT);

            if (text != null && !text.isBlank()) {
                Font buttonFont = font == null
                        ? new Font(Font.SANS_SERIF, Font.BOLD, 17)
                        : font.deriveFont(Font.BOLD, Math.min(18f, Math.max(13f, bounds.height * 0.31f)));
                g2d.setFont(buttonFont);
                g2d.setColor(TEXT);
                FontMetrics metrics = g2d.getFontMetrics(buttonFont);
                int textX = bounds.x + 56;
                int textY = bounds.y - lift + (bounds.height - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.drawString(text, textX, textY);
            }
        } finally {
            g2d.dispose();
        }
    }

    /** HUD compacto. El valor del puntaje inicia en la misma guía vertical que su etiqueta. */
    public static void drawHud(Graphics2D graphics, int score, int lives, int fps, Font font,
                               boolean shield, boolean doubleScore, boolean fastFire, boolean doubleGun) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            Font hud = font == null ? new Font(Font.MONOSPACED, Font.BOLD, 16)
                    : font.deriveFont(Font.BOLD, 16f);
            Font small = hud.deriveFont(Font.BOLD, 12f);

            drawPanel(g, 14, 12, 132, 38, 14);
            drawPanel(g, 154, 12, 112, 38, 14);
            // Bloque compacto, anclado junto al botón Pausa. Etiqueta y valor comparten la misma guía.
            drawPanel(g, 610, 12, 190, 42, 14);

            drawHeart(g, 34, 31, 14, new Color(255, 91, 117));
            g.setFont(hud);
            g.setColor(TEXT);
            g.drawString("× " + Math.max(0, lives), 49, 37);

            g.setFont(small);
            g.setColor(MUTED);
            g.drawString("FPS", 168, 27);
            g.setFont(hud);
            g.setColor(TEXT);
            g.drawString(Integer.toString(Math.max(0, fps)), 201, 37);

            int scoreX = 624;
            g.setFont(small);
            g.setColor(MUTED);
            g.drawString("PUNTAJE", scoreX, 27);
            String formatted = String.format("%06d", Math.max(0, score));
            g.setFont(hud);
            g.setColor(TEXT);
            g.drawString(formatted, scoreX, 46);

            int effectX = 610;
            final int effectY = 61;
            if (shield) effectX = drawEffectChip(g, effectX, effectY, "ESCUDO", new Color(70, 205, 255));
            if (doubleScore) effectX = drawEffectChip(g, effectX, effectY, "X2", new Color(250, 208, 67));
            if (fastFire) effectX = drawEffectChip(g, effectX, effectY, "RÁPIDO", new Color(102, 161, 255));
            if (doubleGun) drawEffectChip(g, effectX, effectY, "DOBLE", new Color(255, 165, 74));
        } finally {
            g.dispose();
        }
    }

    private static int drawEffectChip(Graphics2D g, int x, int y, String text, Color color) {
        Font chipFont = new Font(Font.SANS_SERIF, Font.BOLD, 11);
        g.setFont(chipFont);
        FontMetrics metrics = g.getFontMetrics(chipFont);
        int width = metrics.stringWidth(text) + 23;
        g.setColor(new Color(3, 12, 31, 205));
        g.fillRoundRect(x + 1, y + 2, width, 20, 10, 10);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 210));
        g.setStroke(new BasicStroke(1.1f));
        g.drawRoundRect(x, y, width, 20, 10, 10);
        g.setColor(color);
        g.fillOval(x + 8, y + 8, 5, 5);
        g.setColor(TEXT);
        g.drawString(text, x + 17, y + 14);
        return x + width + 6;
    }

    public static void drawSettingRow(Graphics2D graphics, int x, int y, int width, String label,
                                      String value, Button.Icon icon, Font font, boolean enabled) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            drawPanel(g, x, y, width, 58, 16);
            drawMenuIcon(g, icon, x + 30, y + 29, 19, enabled ? ACCENT : new Color(148, 160, 176));
            Font labelFont = font == null ? new Font(Font.SANS_SERIF, Font.BOLD, 15) : font.deriveFont(Font.BOLD, 15f);
            Font valueFont = labelFont.deriveFont(Font.BOLD, 16f);
            g.setFont(labelFont);
            g.setColor(MUTED);
            g.drawString(label, x + 58, y + 24);
            g.setFont(valueFont);
            g.setColor(enabled ? TEXT : new Color(255, 166, 166));
            g.drawString(value, x + 58, y + 45);
        } finally {
            g.dispose();
        }
    }

    /**
     * Vista previa coherente con la orientación de juego: las naves y los láseres
     * apuntan hacia arriba. La fila de nave no dibuja un láser adicional, para que
     * cada selector represente solo la propiedad que modifica.
     */
    public static void drawChoicePreview(Graphics2D graphics, int x, int y, int width, int height,
                                         Image image, Color laserColor) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            drawPanel(g, x, y, width, height, 18);
            int centerX = x + width / 2;
            int centerY = y + height / 2;

            if (image != null) {
                // Retícula circular para la nave, orientada al norte como el sprite real.
                int radius = Math.min(19, height / 2 - 5);
                g.setColor(new Color(116, 207, 255, 55));
                g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                g.setColor(new Color(205, 243, 255, 150));
                g.setStroke(new BasicStroke(1.15f));
                g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                g.drawLine(centerX, centerY - radius - 3, centerX, centerY - radius + 3);

                int drawH = Math.min(height - 7, Math.max(1, image.getHeight(null)));
                int drawW = Math.max(1, (int) Math.round(drawH * image.getWidth(null) / (double) image.getHeight(null)));
                drawW = Math.min(drawW, width - 24);
                g.drawImage(image, centerX - drawW / 2, centerY - drawH / 2, drawW, drawH, null);
            } else {
                // Haz vertical: el láser sale hacia el norte, igual que la nave del selector.
                int radius = Math.min(18, height / 2 - 5);
                g.setColor(new Color(laserColor.getRed(), laserColor.getGreen(), laserColor.getBlue(), 42));
                g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                g.setColor(new Color(226, 246, 255, 165));
                g.setStroke(new BasicStroke(1.15f));
                g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

                int beamTop = centerY - radius + 5;
                int beamBottom = centerY + radius - 5;
                g.setColor(laserColor);
                g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(centerX, beamBottom, centerX, beamTop);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1.25f));
                g.drawLine(centerX, beamBottom, centerX, beamTop);
                g.fillOval(centerX - 3, beamTop - 3, 6, 6);
            }
        } finally {
            g.dispose();
        }
    }

    public static void drawPauseOverlay(Graphics2D graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setColor(new Color(3, 8, 22, 175));
            g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
        } finally {
            g.dispose();
        }
    }

    public static BufferedImage createAppIcon() {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            enableAntialiasing(g);
            g.setPaint(new GradientPaint(0, 0, new Color(60, 181, 255), 64, 64, new Color(45, 66, 165)));
            g.fillRoundRect(2, 2, 60, 60, 17, 17);
            g.setColor(new Color(255, 255, 255, 220));
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(3, 3, 58, 58, 16, 16);
            drawDecorativeShip(g, 32, 32, 0.38);
        } finally {
            g.dispose();
        }
        return image;
    }

    /** Barra superior propia, con iconos de minimizar, maximizar/restaurar y cerrar. */
    public static void drawWindowChrome(Graphics2D graphics, int width, int height,
                                        boolean maximized, int mouseX, int mouseY, int titleBarHeight) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            g.setPaint(new GradientPaint(0, 0, TITLE_START, width, 0, TITLE_END));
            g.fillRect(0, 0, width, titleBarHeight);
            g.setColor(new Color(119, 205, 255, 145));
            g.drawLine(0, titleBarHeight - 1, width, titleBarHeight - 1);

            BufferedImage icon = createAppIcon();
            int iconSize = Math.max(22, titleBarHeight - 12);
            g.drawImage(icon, 9, (titleBarHeight - iconSize) / 2, iconSize, iconSize, null);
            Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, Math.max(13, titleBarHeight / 3));
            g.setFont(titleFont);
            g.setColor(TEXT);
            g.drawString("SPACE SHIP GAME", iconSize + 17, titleBarHeight / 2 + titleFont.getSize() / 3);

            int controlWidth = 46;
            int startX = width - controlWidth * 3;
            drawChromeButton(g, startX, 0, controlWidth, titleBarHeight, mouseX, mouseY, ChromeIcon.MINIMIZE);
            drawChromeButton(g, startX + controlWidth, 0, controlWidth, titleBarHeight, mouseX, mouseY,
                    maximized ? ChromeIcon.RESTORE : ChromeIcon.MAXIMIZE);
            drawChromeButton(g, startX + controlWidth * 2, 0, controlWidth, titleBarHeight, mouseX, mouseY, ChromeIcon.CLOSE);
        } finally {
            g.dispose();
        }
    }

    public enum ChromeIcon { MINIMIZE, MAXIMIZE, RESTORE, CLOSE }

    private static void drawChromeButton(Graphics2D g, int x, int y, int w, int h,
                                         int mouseX, int mouseY, ChromeIcon icon) {
        boolean hover = new Rectangle(x, y, w, h).contains(mouseX, mouseY);
        if (hover) {
            g.setColor(icon == ChromeIcon.CLOSE ? new Color(224, 74, 91) : new Color(56, 108, 174));
            g.fillRect(x, y, w, h);
        }
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + w / 2;
        int cy = y + h / 2;
        switch (icon) {
            case MINIMIZE:
                g.drawLine(cx - 7, cy + 5, cx + 7, cy + 5);
                break;
            case MAXIMIZE:
                g.drawRect(cx - 7, cy - 7, 14, 14);
                break;
            case RESTORE:
                g.drawRect(cx - 5, cy - 8, 12, 12);
                g.drawRect(cx - 8, cy - 5, 12, 12);
                break;
            case CLOSE:
                g.drawLine(cx - 6, cy - 6, cx + 6, cy + 6);
                g.drawLine(cx + 6, cy - 6, cx - 6, cy + 6);
                break;
            default:
                break;
        }
    }

    public static void drawPowerUp(Graphics2D graphics, double x, double y, int size,
                                   double angle, PowerUpTypes type) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            enableAntialiasing(g);
            int centerX = (int) Math.round(x + size / 2.0);
            int centerY = (int) Math.round(y + size / 2.0);
            int radius = size / 2 - 3;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.24f));
            g.setColor(type.color);
            g.fill(new Ellipse2D.Double(centerX - radius - 6, centerY - radius - 6,
                    (radius + 6) * 2.0, (radius + 6) * 2.0));
            g.setComposite(AlphaComposite.SrcOver);
            g.setPaint(new GradientPaint(centerX - radius, centerY - radius, new Color(255, 255, 255, 205),
                    centerX + radius, centerY + radius, type.color.darker()));
            g.fill(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0));
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.8f));
            g.draw(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0));
            g.rotate(angle, centerX, centerY);
            drawPowerIcon(g, type, centerX, centerY, Math.max(13, size / 3));
        } finally {
            g.dispose();
        }
    }

    public static void drawMenuIcon(Graphics2D g, Button.Icon icon, int centerX, int centerY, int size, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(2.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (icon) {
            case PLAY:
            case RESUME:
                g.fillPolygon(new int[] {centerX - size / 3, centerX - size / 3, centerX + size / 2},
                        new int[] {centerY - size / 2, centerY + size / 2, centerY}, 3);
                break;
            case PAUSE:
                g.fillRoundRect(centerX - size / 2, centerY - size / 2, Math.max(4, size / 4), size, 3, 3);
                g.fillRoundRect(centerX + size / 4, centerY - size / 2, Math.max(4, size / 4), size, 3, 3);
                break;
            case TROPHY:
                drawTrophy(g, centerX, centerY, size, color);
                break;
            case EXIT:
                g.draw(new Arc2D.Double(centerX - size / 2.0, centerY - size / 2.0, size, size, 35, 290, Arc2D.OPEN));
                g.drawLine(centerX, centerY - size / 2 - 2, centerX, centerY + 1);
                break;
            case BACK:
                g.drawLine(centerX + size / 2, centerY, centerX - size / 2, centerY);
                g.drawLine(centerX - size / 2, centerY, centerX - size / 6, centerY - size / 3);
                g.drawLine(centerX - size / 2, centerY, centerX - size / 6, centerY + size / 3);
                break;
            case SETTINGS:
                drawGear(g, centerX, centerY, size, color);
                break;
            case HOME:
                drawHome(g, centerX, centerY, size, color);
                break;
            case PREVIOUS:
                g.drawLine(centerX + size / 2, centerY, centerX - size / 2, centerY);
                g.drawLine(centerX - size / 2, centerY, centerX - size / 8, centerY - size / 3);
                g.drawLine(centerX - size / 2, centerY, centerX - size / 8, centerY + size / 3);
                break;
            case NEXT:
                g.drawLine(centerX - size / 2, centerY, centerX + size / 2, centerY);
                g.drawLine(centerX + size / 2, centerY, centerX + size / 8, centerY - size / 3);
                g.drawLine(centerX + size / 2, centerY, centerX + size / 8, centerY + size / 3);
                break;
            case SOUND:
                drawSpeaker(g, centerX, centerY, size, color);
                break;
            case SHIP:
                drawDecorativeShip(g, centerX, centerY, Math.max(0.12, size / 54.0));
                break;
            case LASER:
                g.setStroke(new BasicStroke(3.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(centerX - size / 2, centerY, centerX + size / 2, centerY);
                g.fillOval(centerX - size / 2 - 3, centerY - 3, 6, 6);
                break;
            case HELP:
                Font question = new Font(Font.SANS_SERIF, Font.BOLD, Math.max(14, size));
                g.setFont(question);
                FontMetrics metrics = g.getFontMetrics(question);
                g.drawString("?", centerX - metrics.stringWidth("?") / 2, centerY + metrics.getAscent() / 2 - 2);
                break;
            default:
                break;
        }
    }

    private static void drawPanel(Graphics2D g, int x, int y, int width, int height, int arc) {
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(x + 2, y + 3, width, height, arc, arc);
        g.setColor(PANEL);
        g.fillRoundRect(x, y, width, height, arc, arc);
        g.setColor(PANEL_BORDER);
        g.setStroke(new BasicStroke(1.05f));
        g.drawRoundRect(x, y, width, height, arc, arc);
    }

    private static void drawStarField(Graphics2D g) {
        for (int i = 0; i < 105; i++) {
            int x = Math.floorMod(i * 137 + 59, Constants.WIDTH);
            int y = Math.floorMod(i * 67 + 23, Constants.HEIGHT);
            int alpha = 55 + Math.floorMod(i * 31, 150);
            int radius = i % 9 == 0 ? 2 : 1;
            g.setColor(new Color(200, 235, 255, alpha));
            g.fillOval(x, y, radius, radius);
        }
    }

    private static void drawPerspectiveGrid(Graphics2D g) {
        g.setColor(new Color(78, 188, 255, 55));
        g.setStroke(new BasicStroke(1f));
        int horizon = 470;
        for (int i = -8; i <= 8; i++) {
            int endX = Constants.WIDTH / 2 + i * 110;
            g.drawLine(Constants.WIDTH / 2 + i * 16, horizon, endX, Constants.HEIGHT);
        }
        for (int y = horizon; y < Constants.HEIGHT; y += 25) {
            int spread = (y - horizon) * 2;
            g.drawLine(Constants.WIDTH / 2 - spread, y, Constants.WIDTH / 2 + spread, y);
        }
    }

    private static void drawDecorativePlanet(Graphics2D g, int centerX, int centerY, int radius, Color color) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.80f));
        g.setPaint(new GradientPaint(centerX - radius, centerY - radius, color.brighter(), centerX + radius, centerY + radius, color.darker()));
        g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(new Color(255, 255, 255, 135));
        g.setStroke(new BasicStroke(2f));
        g.drawArc(centerX - radius - 25, centerY - 17, radius * 2 + 50, 45, 190, 165);
    }

    private static void drawDecorativeShip(Graphics2D g, int centerX, int centerY, double scale) {
        Graphics2D copy = (Graphics2D) g.create();
        try {
            enableAntialiasing(copy);
            copy.translate(centerX, centerY);
            copy.scale(scale, scale);
            Path2D ship = new Path2D.Double();
            ship.moveTo(0, -28);
            ship.lineTo(18, 20);
            ship.lineTo(0, 13);
            ship.lineTo(-18, 20);
            ship.closePath();
            copy.setPaint(new GradientPaint(0, -28, new Color(185, 235, 255), 0, 25, new Color(47, 126, 240)));
            copy.fill(ship);
            copy.setColor(Color.WHITE);
            copy.setStroke(new BasicStroke(1.7f));
            copy.draw(ship);
            copy.setColor(new Color(74, 244, 255, 180));
            copy.fillOval(-5, -9, 10, 18);
            copy.setColor(new Color(68, 216, 255, 155));
            copy.fillOval(-5, 18, 10, 23);
        } finally {
            copy.dispose();
        }
    }

    private static void drawHeart(Graphics2D g, int centerX, int centerY, int size, Color color) {
        int half = Math.max(5, size / 2);
        Path2D.Double heart = new Path2D.Double();
        heart.moveTo(centerX, centerY + half);
        heart.curveTo(centerX - half * 2.0, centerY - half / 3.0,
                centerX - half, centerY - half * 1.7, centerX, centerY - half / 2.0);
        heart.curveTo(centerX + half, centerY - half * 1.7,
                centerX + half * 2.0, centerY - half / 3.0, centerX, centerY + half);
        g.setColor(color);
        g.fill(heart);
    }

    private static void drawPowerIcon(Graphics2D g, PowerUpTypes type, int centerX, int centerY, int size) {
        switch (type) {
            case SHIELD: drawShield(g, centerX, centerY, size, Color.WHITE); break;
            case LIFE: drawHeart(g, centerX, centerY, size, Color.WHITE); break;
            case SCORE_X2:
                Font multiplier = new Font(Font.SANS_SERIF, Font.BOLD, Math.max(14, size));
                g.setFont(multiplier);
                g.setColor(Color.WHITE);
                FontMetrics metrics = g.getFontMetrics(multiplier);
                g.drawString("×2", centerX - metrics.stringWidth("×2") / 2, centerY + metrics.getAscent() / 2 - 2);
                break;
            case FASTER_FIRE: drawLightning(g, centerX, centerY, size, Color.WHITE); break;
            case SCORE_STACK: drawStar(g, centerX, centerY, size, Color.WHITE); break;
            case DOUBLE_GUN: drawDoubleGun(g, centerX, centerY, size, Color.WHITE); break;
            default: break;
        }
    }

    private static void drawShield(Graphics2D g, int cx, int cy, int size, Color color) {
        int half = Math.max(7, size / 2);
        Polygon shield = new Polygon(new int[] {cx - half, cx + half, cx + half - 2, cx, cx - half + 2},
                new int[] {cy - half, cy - half, cy + half / 3, cy + half, cy + half / 3}, 5);
        g.setColor(color);
        g.fillPolygon(shield);
        g.setColor(new Color(20, 60, 110));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx, cy - half + 3, cx, cy + half - 5);
    }

    private static void drawLightning(Graphics2D g, int cx, int cy, int size, Color color) {
        int half = Math.max(8, size / 2);
        Polygon bolt = new Polygon(new int[] {cx + 2, cx - half, cx - 1, cx - 4, cx + half, cx + 2},
                new int[] {cy - half, cy + 1, cy + 1, cy + half, cy - 2, cy - 2}, 6);
        g.setColor(color);
        g.fillPolygon(bolt);
    }

    private static void drawStar(Graphics2D g, int cx, int cy, int size, Color color) {
        int points = 10;
        int outer = Math.max(9, size / 2);
        int inner = Math.max(4, outer / 2);
        int[] xs = new int[points];
        int[] ys = new int[points];
        for (int i = 0; i < points; i++) {
            double angle = -Math.PI / 2.0 + i * Math.PI / 5.0;
            int radius = i % 2 == 0 ? outer : inner;
            xs[i] = cx + (int) Math.round(Math.cos(angle) * radius);
            ys[i] = cy + (int) Math.round(Math.sin(angle) * radius);
        }
        g.setColor(color);
        g.fillPolygon(xs, ys, points);
    }

    private static void drawDoubleGun(Graphics2D g, int cx, int cy, int size, Color color) {
        int height = Math.max(15, size);
        int width = Math.max(4, size / 5);
        g.setColor(color);
        g.fillRoundRect(cx - width - 3, cy - height / 2, width, height, 4, 4);
        g.fillRoundRect(cx + 3, cy - height / 2, width, height, 4, 4);
    }

    private static void drawTrophy(Graphics2D g, int cx, int cy, int size, Color color) {
        int half = Math.max(8, size / 2);
        g.setColor(color);
        g.fillRoundRect(cx - half / 2, cy - half, half, half + 3, 4, 4);
        g.setStroke(new BasicStroke(2f));
        g.drawArc(cx - half - 4, cy - half + 2, half, half - 2, 270, 180);
        g.drawArc(cx + 4, cy - half + 2, half, half - 2, 90, 180);
        g.fillRect(cx - 2, cy + 2, 4, half / 2 + 2);
        g.fillRoundRect(cx - half / 2, cy + half / 2 + 1, half, 4, 4, 4);
    }

    private static void drawGear(Graphics2D g, int cx, int cy, int size, Color color) {
        int r = Math.max(7, size / 2);
        g.setColor(color);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4.0;
            int x = cx + (int) Math.round(Math.cos(angle) * r);
            int y = cy + (int) Math.round(Math.sin(angle) * r);
            g.fillRoundRect(x - 3, y - 3, 6, 6, 2, 2);
        }
        g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        g.setColor(new Color(27, 73, 130));
        g.fillOval(cx - r / 2, cy - r / 2, r, r);
    }

    private static void drawHome(Graphics2D g, int cx, int cy, int size, Color color) {
        int half = Math.max(7, size / 2);
        g.setColor(color);
        g.fillPolygon(new int[] {cx - half, cx, cx + half}, new int[] {cy, cy - half, cy}, 3);
        g.fillRoundRect(cx - half + 3, cy - 1, half * 2 - 6, half + 3, 3, 3);
        g.setColor(new Color(25, 60, 112));
        g.fillRect(cx - 2, cy + half / 2, 4, half / 2 + 3);
    }

    private static void drawSpeaker(Graphics2D g, int cx, int cy, int size, Color color) {
        int half = Math.max(7, size / 2);
        g.setColor(color);
        g.fillPolygon(new int[] {cx - half, cx - half / 3, cx - half / 3},
                new int[] {cy - half / 3, cy - half, cy + half}, 3);
        g.setStroke(new BasicStroke(2f));
        g.drawArc(cx - half / 4, cy - half / 2, half, half, -55, 110);
        g.drawArc(cx, cy - half, half + 4, half + 4, -55, 110);
    }

    private static void enableAntialiasing(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}

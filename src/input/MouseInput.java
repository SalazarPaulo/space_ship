package src.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Conserva coordenadas físicas para la ventana y coordenadas lógicas para el juego escalado.
 */
public class MouseInput extends MouseAdapter {
    /** Coordenadas lógicas en el área 1000x600 del juego. */
    public static volatile int X = -10_000;
    public static volatile int Y = -10_000;
    /** Coordenadas físicas dentro del Canvas, usadas por la barra de ventana. */
    public static volatile int rawX;
    public static volatile int rawY;
    private static volatile boolean leftClickPending;
    private static volatile boolean leftDown;

    @Override
    public void mousePressed(MouseEvent event) {
        updateRaw(event);
        if (event.getButton() == MouseEvent.BUTTON1) leftDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        updateRaw(event);
        if (event.getButton() == MouseEvent.BUTTON1) {
            leftDown = false;
            leftClickPending = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) { updateRaw(event); }

    @Override
    public void mouseMoved(MouseEvent event) { updateRaw(event); }

    @Override
    public void mouseExited(MouseEvent event) {
        rawX = event.getX();
        rawY = event.getY();
    }

    public static void updateGameCoordinates(double scale, int originX, int originY, int logicalWidth, int logicalHeight) {
        if (scale <= 0) {
            X = -10_000;
            Y = -10_000;
            return;
        }
        int x = (int) Math.floor((rawX - originX) / scale);
        int y = (int) Math.floor((rawY - originY) / scale);
        if (x < 0 || y < 0 || x >= logicalWidth || y >= logicalHeight) {
            X = -10_000;
            Y = -10_000;
        } else {
            X = x;
            Y = y;
        }
    }

    public static boolean consumeLeftClick() {
        if (!leftClickPending) return false;
        leftClickPending = false;
        return true;
    }

    public static void discardPendingClick() { leftClickPending = false; }
    public static boolean isLeftDown() { return leftDown; }

    private static void updateRaw(MouseEvent event) {
        rawX = event.getX();
        rawY = event.getY();
    }
}

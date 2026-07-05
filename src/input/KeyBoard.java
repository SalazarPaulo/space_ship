package src.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Entrada de teclado del lienzo de juego. */
public class KeyBoard implements KeyListener {
    private static final int MAX_TYPED_QUEUE = 64;
    private final boolean[] keys = new boolean[512];
    private static final ConcurrentLinkedQueue<Character> typedCharacters = new ConcurrentLinkedQueue<>();
    private static volatile boolean backspacePending;

    public static boolean UP;
    public static boolean LEFT;
    public static boolean RIGHT;
    public static boolean SHOOT;
    public static boolean PAUSE;

    public void update() {
        UP = isDown(KeyEvent.VK_W) || isDown(KeyEvent.VK_UP);
        LEFT = isDown(KeyEvent.VK_A) || isDown(KeyEvent.VK_LEFT);
        RIGHT = isDown(KeyEvent.VK_D) || isDown(KeyEvent.VK_RIGHT);
        SHOOT = isDown(KeyEvent.VK_SPACE);
        PAUSE = isDown(KeyEvent.VK_P) || isDown(KeyEvent.VK_ESCAPE);
    }

    /** Evita que una tecla quede marcada al perder el foco de la ventana. */
    public void clear() {
        Arrays.fill(keys, false);
        UP = false;
        LEFT = false;
        RIGHT = false;
        SHOOT = false;
        PAUSE = false;
    }

    /** Obtiene el texto escrito desde la última lectura, para los campos de interfaz. */
    public static String consumeTypedText() {
        StringBuilder text = new StringBuilder();
        Character character;
        while ((character = typedCharacters.poll()) != null) text.append(character.charValue());
        return text.toString();
    }

    public static boolean consumeBackspace() {
        if (!backspacePending) return false;
        backspacePending = false;
        return true;
    }

    public static void clearTextInput() {
        typedCharacters.clear();
        backspacePending = false;
    }

    private boolean isDown(int keyCode) {
        return keyCode >= 0 && keyCode < keys.length && keys[keyCode];
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int code = event.getKeyCode();
        if (code >= 0 && code < keys.length) keys[code] = true;
        if (code == KeyEvent.VK_BACK_SPACE) backspacePending = true;
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() >= 0 && event.getKeyCode() < keys.length) keys[event.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent event) {
        char character = event.getKeyChar();
        if (Character.isISOControl(character)) return;
        if (typedCharacters.size() < MAX_TYPED_QUEUE) typedCharacters.offer(character);
    }
}

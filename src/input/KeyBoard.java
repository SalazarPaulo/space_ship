package src.input;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyBoard implements KeyListener {

    private boolean[] keys = new boolean [256];
    public static boolean UP, RIGHT, LEFT, SHOOT;

    public KeyBoard () {
        UP = false;
        RIGHT = false;
        LEFT = false;
        SHOOT = false;
    }
    public void update () {
        UP = keys[KeyEvent.VK_W];
        RIGHT = keys[KeyEvent.VK_D];
        LEFT = keys[KeyEvent.VK_A];
        SHOOT = keys[KeyEvent.VK_SPACE];
    }
    @Override // Cuando se presione una tecla se guarda aqui, la info se guarda en el objeto e, esepcificamente en KeyCode
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        
    }
    @Override // Cuando se suelte una tecla este metodo es llamado
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        
    }
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}

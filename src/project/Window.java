package src.project;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferStrategy;

import src.graphics.*;
import src.input.KeyBoard;
import src.gameObjects.Constants;
// Clase Principal
public class Window extends JFrame implements Runnable {

    private Canvas canvas;
    private Thread thread; // Crear un hilo para no sobrecargar el programa
    private boolean running = false;

    private BufferStrategy bs;
    private Graphics g;

    private final int FPS = 60;
    private double TARGETTIME = 1000000000/FPS;
    private double delta = 0;
    private int AVERAGEFPS = FPS;

    private GameState gameState;
    private KeyBoard keyBoard;
    // constructor
    public Window () {
        ////////--------  WINDOW --------////////
        setTitle("SPACE SHIP GAME");
        setSize(Constants.WIDTH, Constants.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Para que la ventana se pueda cerrar
        setResizable(false); // Para que la ventana no se pueda redimensionar
        setLocationRelativeTo(null); // Para que la ventana se despliegue en el centro de la ventana cuando se inicie el programa
        
        ////////--------  CANVAS --------////////
        canvas = new Canvas();
        keyBoard = new KeyBoard();
        
        canvas.setPreferredSize(new Dimension (Constants.WIDTH, Constants.HEIGHT)); 
        canvas.setMaximumSize(new Dimension (Constants.WIDTH, Constants.HEIGHT));
        canvas.setMinimumSize(new Dimension (Constants.WIDTH, Constants.HEIGHT));
        canvas.setFocusable(true); // Permite recibir entradas por parte del teclado
        
        add(canvas); // Agregando el canvas a la ventana
        canvas.addKeyListener(keyBoard);
        setVisible(); // Para que se vea la ventana
    }
    
    // public static void main (String x[]) {
    //     new Window().start();
    // }
    // actualizar
    private void update() {
        keyBoard.update();
        gameState.update();
    }
    //dibujar
    private void draw() {
        Toolkit.getDefaultToolkit().sync();
        bs = canvas.getBufferStrategy();
        if ( bs == null ) {
            canvas.createBufferStrategy(2);
            return;
        }

        g = bs.getDrawGraphics();

        //-------------//
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
        gameState.draw(g);
        g.setColor(Color.WHITE);
        g.drawString("" + AVERAGEFPS, 100, 100);
        //-------------//
        g.dispose();
        bs.show();
        
    }
    private void init () {
        Assets.init();
        gameState = new GameState();
    }

    @Override
    public void run () { 

        long now = 0;
        long lastTime = System.nanoTime();
        int frames = 0;
        long time = 0;

        init();
        // Mantener el juego actualizado
        while(running) {
            now = System.nanoTime();
            delta += (now - lastTime)/TARGETTIME; 
            time += (now - lastTime);
            lastTime = now;

            if ( delta >=1 ) {
                update();;
                draw();
                delta --;
                frames ++;
                
            }
            if ( time >= 1000000000 ) {
                AVERAGEFPS = frames;
                frames = 0;
                time = 0;
            }
        }
        stop();
    }
    public void start () {
        thread = new Thread(this); // Iniciando el hilo, recibe como parametro contructor runnable
        thread.start(); // Manda a llamar el metodo run
        running = true;
    }
    private void stop () {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

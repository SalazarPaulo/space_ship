package src.project;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JFrame;

import src.gameObjects.Constants;
import src.graphics.Assets;
import src.input.KeyBoard;
import src.input.MouseInput;
import src.ui.UiRenderer;

/**
 * Ventana sin decoración nativa, con barra superior e iconos propios.
 * El juego se dibuja en una resolución lógica 1000x600 y se escala de forma proporcional.
 */
public class Window extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private static volatile Window activeWindow;

    public static final int TITLE_BAR_HEIGHT = 40;
    private static final int CONTROL_WIDTH = 46;
    private static final int RESIZE_GRIP = 8;
    private static final int MIN_WINDOW_WIDTH = 640;
    private static final int MIN_WINDOW_HEIGHT = 430;
    private static final long FRAME_NANOS = 1_000_000_000L / 60L;
    private static final float FIXED_DT_MILLIS = 1000f / 60f;
    private static volatile int averageFps = 60;

    private final Canvas canvas;
    private final KeyBoard keyBoard;
    private final MouseInput mouseInput;
    private Thread thread;
    private volatile boolean running;
    private volatile BufferStrategy bufferStrategy;

    private double scale = 1.0;
    private int viewportX;
    private int viewportY;
    private int viewportWidth = Constants.WIDTH;
    private int viewportHeight = Constants.HEIGHT;

    private final Rectangle minimizeBounds = new Rectangle();
    private final Rectangle maximizeBounds = new Rectangle();
    private final Rectangle closeBounds = new Rectangle();
    private Rectangle restoreBounds;

    public Window() {
        activeWindow = this;
        setTitle("Space Ship — Episode Phantom");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(true);
        setIconImage(UiRenderer.createAppIcon());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setBackground(Color.BLACK);
        canvas.setFocusable(true);
        canvas.setFocusTraversalKeysEnabled(false);
        canvas.setPreferredSize(initialWindowSize());

        keyBoard = new KeyBoard();
        mouseInput = new MouseInput();
        canvas.addKeyListener(keyBoard);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
        canvas.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                keyBoard.clear();
            }
        });
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent event) {
                EventQueue.invokeLater(() -> canvas.requestFocusInWindow());
            }

            @Override
            public void windowDeactivated(WindowEvent event) {
                keyBoard.clear();
            }
        });
        installCustomWindowInteractions();

        add(canvas, BorderLayout.CENTER);
        setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        setSize(initialWindowSize());
        centerInWorkArea();
        setVisible(true);
        EventQueue.invokeLater(() -> {
            canvas.requestFocusInWindow();
            canvas.requestFocus();
        });
    }

    public static int getAverageFps() { return averageFps; }

    /**
     * Solicita de nuevo el foco del Canvas desde el hilo de eventos de AWT.
     * Esto mantiene el KeyListener operativo tras una reaparición, cambio de
     * estado o interacción con la barra de ventana personalizada.
     */
    public static void requestGameInputFocus() {
        Window window = activeWindow;
        if (window == null) return;
        EventQueue.invokeLater(() -> {
            if (window.isDisplayable() && window.canvas.isDisplayable()) {
                window.canvas.setFocusable(true);
                window.canvas.requestFocusInWindow();
            }
        });
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "space-ship-loop");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        init();
        long nextFrame = System.nanoTime();
        long fpsClock = nextFrame;
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            long remaining = nextFrame - now;
            if (remaining > 0L) {
                LockSupport.parkNanos(remaining);
                continue;
            }

            update(FIXED_DT_MILLIS);
            draw();
            frames++;

            nextFrame += FRAME_NANOS;
            // Si el sistema se detuvo temporalmente (redimensionar, minimizar, depurador),
            // no ejecutamos cientos de actualizaciones atrasadas que harían saltar la nave.
            if (now - nextFrame > FRAME_NANOS * 5L) {
                nextFrame = now + FRAME_NANOS;
            }

            if (now - fpsClock >= 1_000_000_000L) {
                averageFps = frames;
                frames = 0;
                fpsClock = now;
            }
        }
    }

    private void init() {
        Thread loadingThread = new Thread(Assets::init, "asset-loader");
        State.changeState(new LoadingState(loadingThread));
    }

    private void update(float dt) {
        updateViewport();
        keyBoard.update();
        State state = State.getCurrentState();
        if (state != null) state.update(dt);
    }

    /**
     * Reconstituye el BufferStrategy cuando Windows recrea el peer del Canvas al
     * minimizar, maximizar, restaurar o redimensionar la ventana.
     */
    private void draw() {
        if (!canvas.isDisplayable() || !canvas.isShowing()) {
            bufferStrategy = null;
            return;
        }

        try {
            BufferStrategy strategy = canvas.getBufferStrategy();
            if (strategy == null) {
                canvas.createBufferStrategy(3);
                bufferStrategy = canvas.getBufferStrategy();
                return;
            }
            bufferStrategy = strategy;

            do {
                do {
                    Graphics2D raw = null;
                    try {
                        raw = (Graphics2D) strategy.getDrawGraphics();
                        renderFrame(raw);
                    } finally {
                        if (raw != null) raw.dispose();
                    }
                } while (strategy.contentsRestored());

                if (!canvas.isDisplayable() || !canvas.isShowing()) {
                    bufferStrategy = null;
                    return;
                }
                strategy.show();
                Toolkit.getDefaultToolkit().sync();
            } while (strategy.contentsLost());
        } catch (IllegalStateException ignored) {
            // El peer puede desaparecer brevemente durante una transición del JFrame.
            // En el siguiente frame se crea una estrategia nueva cuando Canvas vuelva a ser visible.
            bufferStrategy = null;
        }
    }

    private void renderFrame(Graphics2D raw) {
        raw.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        updateViewport();
        raw.setColor(new Color(3, 7, 18));
        raw.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        raw.setColor(Color.BLACK);
        raw.fillRect(viewportX, viewportY, viewportWidth, viewportHeight);
        Graphics2D gameGraphics = (Graphics2D) raw.create();
        try {
            gameGraphics.translate(viewportX, viewportY);
            gameGraphics.scale(scale, scale);
            State state = State.getCurrentState();
            if (state != null) state.draw(gameGraphics);
        } finally {
            gameGraphics.dispose();
        }

        UiRenderer.drawWindowChrome(raw, canvas.getWidth(), canvas.getHeight(), isMaximized(),
                MouseInput.rawX, MouseInput.rawY, TITLE_BAR_HEIGHT);
        drawResizeFrame(raw);
    }

    /** Escalado proporcional con letterboxing; actualiza además el ratón lógico. */
    private void updateViewport() {
        int canvasWidth = Math.max(1, canvas.getWidth());
        int canvasHeight = Math.max(TITLE_BAR_HEIGHT + 1, canvas.getHeight());
        int availableHeight = Math.max(1, canvasHeight - TITLE_BAR_HEIGHT);
        scale = Math.min(canvasWidth / (double) Constants.WIDTH, availableHeight / (double) Constants.HEIGHT);
        viewportWidth = Math.max(1, (int) Math.round(Constants.WIDTH * scale));
        viewportHeight = Math.max(1, (int) Math.round(Constants.HEIGHT * scale));
        viewportX = (canvasWidth - viewportWidth) / 2;
        viewportY = TITLE_BAR_HEIGHT + (availableHeight - viewportHeight) / 2;
        MouseInput.updateGameCoordinates(scale, viewportX, viewportY, Constants.WIDTH, Constants.HEIGHT);
        updateChromeBounds();
    }

    private void updateChromeBounds() {
        int width = Math.max(0, canvas.getWidth());
        minimizeBounds.setBounds(width - CONTROL_WIDTH * 3, 0, CONTROL_WIDTH, TITLE_BAR_HEIGHT);
        maximizeBounds.setBounds(width - CONTROL_WIDTH * 2, 0, CONTROL_WIDTH, TITLE_BAR_HEIGHT);
        closeBounds.setBounds(width - CONTROL_WIDTH, 0, CONTROL_WIDTH, TITLE_BAR_HEIGHT);
    }

    private Dimension initialWindowSize() {
        WorkArea workArea = getWorkArea();
        int horizontalMargin = Math.max(48, (int) Math.round(workArea.width * 0.07));
        int verticalMargin = Math.max(42, (int) Math.round(workArea.height * 0.08));
        double allowedWidth = Math.max(MIN_WINDOW_WIDTH, workArea.width - horizontalMargin * 2.0);
        double allowedHeight = Math.max(MIN_WINDOW_HEIGHT, workArea.height - verticalMargin * 2.0);
        double initialScale = Math.min(allowedWidth / Constants.WIDTH,
                (allowedHeight - TITLE_BAR_HEIGHT) / Constants.HEIGHT);
        initialScale = Math.max(0.55, initialScale);
        int width = (int) Math.round(Constants.WIDTH * initialScale);
        int height = TITLE_BAR_HEIGHT + (int) Math.round(Constants.HEIGHT * initialScale);
        return new Dimension(width, height);
    }

    private void centerInWorkArea() {
        WorkArea workArea = getWorkArea();
        int x = workArea.x + Math.max(0, (workArea.width - getWidth()) / 2);
        int y = workArea.y + Math.max(0, (workArea.height - getHeight()) / 2);
        setLocation(x, y);
    }

    private WorkArea getWorkArea() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        Rectangle screen = configuration.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);
        return new WorkArea(screen.x + insets.left, screen.y + insets.top,
                screen.width - insets.left - insets.right, screen.height - insets.top - insets.bottom);
    }

    private void installCustomWindowInteractions() {
        WindowDragResizeController controller = new WindowDragResizeController();
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
    }

    private boolean isMaximized() {
        return (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    private void toggleMaximize() {
        bufferStrategy = null;
        if (isMaximized()) {
            setExtendedState(Frame.NORMAL);
            if (restoreBounds != null) setBounds(restoreBounds);
        } else {
            restoreBounds = getBounds();
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        MouseInput.discardPendingClick();
    }

    private void requestShutdown() {
        running = false;
        bufferStrategy = null;
        EventQueue.invokeLater(() -> {
            if (isDisplayable()) dispose();
        });
    }

    private void drawResizeFrame(Graphics2D graphics) {
        if (isMaximized()) return;
        graphics.setColor(new Color(124, 198, 255, 100));
        graphics.drawRect(0, 0, Math.max(0, canvas.getWidth() - 1), Math.max(0, canvas.getHeight() - 1));
    }

    private final class WindowDragResizeController extends MouseAdapter {
        private static final int LEFT = 1;
        private static final int RIGHT = 2;
        private static final int TOP = 4;
        private static final int BOTTOM = 8;

        private boolean moving;
        private int resizeEdges;
        private Point pressScreen;
        private Rectangle initialBounds;
        private boolean chromeGesture;

        @Override
        public void mousePressed(MouseEvent event) {
            canvas.requestFocusInWindow();
            updateViewport();
            chromeGesture = false;
            Point screen = event.getLocationOnScreen();

            if (closeBounds.contains(event.getPoint())) {
                chromeGesture = true;
                MouseInput.discardPendingClick();
                requestShutdown();
                return;
            }
            if (minimizeBounds.contains(event.getPoint())) {
                chromeGesture = true;
                MouseInput.discardPendingClick();
                bufferStrategy = null;
                setState(Frame.ICONIFIED);
                return;
            }
            if (maximizeBounds.contains(event.getPoint())) {
                chromeGesture = true;
                toggleMaximize();
                return;
            }

            int edge = isMaximized() ? 0 : getResizeEdges(event.getX(), event.getY());
            if (edge != 0) {
                chromeGesture = true;
                resizeEdges = edge;
                pressScreen = screen;
                initialBounds = getBounds();
                MouseInput.discardPendingClick();
                return;
            }

            if (event.getY() < TITLE_BAR_HEIGHT) {
                chromeGesture = true;
                moving = true;
                pressScreen = screen;
                initialBounds = getBounds();
                MouseInput.discardPendingClick();
            }
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            if ((!moving && resizeEdges == 0) || pressScreen == null || initialBounds == null) return;
            Point screen = event.getLocationOnScreen();
            int dx = screen.x - pressScreen.x;
            int dy = screen.y - pressScreen.y;

            if (moving) {
                setLocation(initialBounds.x + dx, initialBounds.y + dy);
                return;
            }

            int x = initialBounds.x;
            int y = initialBounds.y;
            int width = initialBounds.width;
            int height = initialBounds.height;
            if ((resizeEdges & LEFT) != 0) { x += dx; width -= dx; }
            if ((resizeEdges & RIGHT) != 0) width += dx;
            if ((resizeEdges & TOP) != 0) { y += dy; height -= dy; }
            if ((resizeEdges & BOTTOM) != 0) height += dy;

            if (width < MIN_WINDOW_WIDTH) {
                if ((resizeEdges & LEFT) != 0) x = initialBounds.x + initialBounds.width - MIN_WINDOW_WIDTH;
                width = MIN_WINDOW_WIDTH;
            }
            if (height < MIN_WINDOW_HEIGHT) {
                if ((resizeEdges & TOP) != 0) y = initialBounds.y + initialBounds.height - MIN_WINDOW_HEIGHT;
                height = MIN_WINDOW_HEIGHT;
            }
            bufferStrategy = null;
            setBounds(x, y, width, height);
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            if (moving || resizeEdges != 0 || chromeGesture) MouseInput.discardPendingClick();
            moving = false;
            resizeEdges = 0;
            pressScreen = null;
            initialBounds = null;
            chromeGesture = false;
            updateCursor(event.getX(), event.getY());
        }

        @Override
        public void mouseMoved(MouseEvent event) { updateCursor(event.getX(), event.getY()); }

        private void updateCursor(int x, int y) {
            if (isMaximized() || minimizeBounds.contains(x, y) || maximizeBounds.contains(x, y) || closeBounds.contains(x, y)) {
                canvas.setCursor(Cursor.getDefaultCursor());
                return;
            }
            int edges = getResizeEdges(x, y);
            int cursor = Cursor.DEFAULT_CURSOR;
            if ((edges & LEFT) != 0 && (edges & TOP) != 0) cursor = Cursor.NW_RESIZE_CURSOR;
            else if ((edges & RIGHT) != 0 && (edges & TOP) != 0) cursor = Cursor.NE_RESIZE_CURSOR;
            else if ((edges & LEFT) != 0 && (edges & BOTTOM) != 0) cursor = Cursor.SW_RESIZE_CURSOR;
            else if ((edges & RIGHT) != 0 && (edges & BOTTOM) != 0) cursor = Cursor.SE_RESIZE_CURSOR;
            else if ((edges & LEFT) != 0 || (edges & RIGHT) != 0) cursor = Cursor.E_RESIZE_CURSOR;
            else if ((edges & TOP) != 0 || (edges & BOTTOM) != 0) cursor = Cursor.N_RESIZE_CURSOR;
            canvas.setCursor(Cursor.getPredefinedCursor(cursor));
        }

        private int getResizeEdges(int x, int y) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int edges = 0;
            if (x <= RESIZE_GRIP) edges |= LEFT;
            if (x >= width - RESIZE_GRIP) edges |= RIGHT;
            if (y <= RESIZE_GRIP) edges |= TOP;
            if (y >= height - RESIZE_GRIP) edges |= BOTTOM;
            return edges;
        }
    }

    private static final class WorkArea {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private WorkArea(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}

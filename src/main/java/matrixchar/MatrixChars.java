package matrixchar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.List;

public class MatrixChars {
    private final static int NUM_BUFFERS = 2;
    private final static int ROW_MARGIN = 5;

    private final static int BASE_FONT_SIZE = 12;
    private final static int FONT_SIZE_VARIANCE = 8;
    private final static int LEFT_MARGIN = 5;
    private final static int UPDATE_DELAY_MS = 90;


    private final List<SymbolLine> lines;
    private final Frame mainFrame;
    private final GraphicsDevice graphicsDevice;

    private boolean escKeyPressed = false;
    private boolean pauseKeyPressed = false;

    private MatrixChars(GraphicsDevice graphicsDevice) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::dispatchKeyEvent);

        this.graphicsDevice = graphicsDevice;

        GraphicsConfiguration gc = graphicsDevice.getDefaultConfiguration();
        mainFrame = new Frame(gc);
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);

        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int numLines = (int) (screenWidth / (BASE_FONT_SIZE + ROW_MARGIN));
        Random rnd = new Random();
        lines = IntStream.iterate(0, i -> i + 1)
                .limit(numLines)
                .mapToObj(i -> {
                    int lineX = i * (BASE_FONT_SIZE + ROW_MARGIN) + LEFT_MARGIN;
                    int fontSize = BASE_FONT_SIZE + rnd.nextInt(FONT_SIZE_VARIANCE);
                    return SymbolLine.generate(lineX, fontSize);
                }).toList();
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(UPDATE_DELAY_MS);
    }

    public void start() throws InterruptedException {
        graphicsDevice.setFullScreenWindow(mainFrame);
        mainFrame.createBufferStrategy(NUM_BUFFERS);

        BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
        Rectangle bounds = mainFrame.getBounds();
        try {
            while (!escKeyPressed) {
                if (!pauseKeyPressed) {
                    Graphics g = bufferStrategy.getDrawGraphics();
                    g.setColor(Color.black);
                    g.fillRect(0, 0, bounds.width, bounds.height);
                    lines.forEach(line -> line.updateAndDraw(g));
                    bufferStrategy.show();
                    g.dispose();
                }
                sleep();
            }
        } finally {
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    public static MatrixChars create() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        return new MatrixChars(device);
    }

    private boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getID()) {
            case KeyEvent.KEY_RELEASED:
                if (event.getKeyCode() == KeyEvent.VK_PAUSE) {
                    pauseKeyPressed = !pauseKeyPressed;
                    return true;
                }
                escKeyPressed = true;
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            MatrixChars.create().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}

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
    
    private volatile boolean escKeyPressed = false;
    private final List<SymbolLine> lines;
    private final Frame mainFrame;
    private final GraphicsDevice device;

    private MatrixChars(GraphicsDevice device) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::dispatchKeyEvent);

        this.device = device;

        GraphicsConfiguration gc = device.getDefaultConfiguration();
        mainFrame = new Frame(gc);
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);

        int baseFontSize = 12;

        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int numLines = (int) (screenWidth / (baseFontSize + ROW_MARGIN));
        System.out.println("Number of lines: " + numLines);
        Random rnd = new Random();
        lines = IntStream.iterate(0, i -> i + 1)
                .limit(numLines)
                .mapToObj(i -> {
                    int lineX = 5 + i * (baseFontSize + ROW_MARGIN);
                    int fontSize = baseFontSize + rnd.nextInt(8);
                    return SymbolLine.generate(lineX, fontSize);
                }).toList();
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(90);
    }

    public void start() throws InterruptedException {
        device.setFullScreenWindow(mainFrame);
        mainFrame.createBufferStrategy(NUM_BUFFERS);

        BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
        Rectangle bounds = mainFrame.getBounds();
        try {
            while (!escKeyPressed) {
                Graphics g = bufferStrategy.getDrawGraphics();
                g.setColor(Color.black);
                g.fillRect(0, 0, bounds.width, bounds.height);
                lines.forEach(line -> line.updateAndDraw(g));
                bufferStrategy.show();
                g.dispose();
                sleep();
            }
        } finally {
            device.setFullScreenWindow(null);
        }
    }

    public static MatrixChars create() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        return new MatrixChars(device);
    }

    private boolean dispatchKeyEvent(KeyEvent event) {
        /*switch (event.getID()) {
            case KeyEvent.KEY_RELEASED:
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    escKeyPressed = true;
                    return true;
                }
        }*/
        escKeyPressed = true;
        return true;
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

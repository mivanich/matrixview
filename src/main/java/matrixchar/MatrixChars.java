package matrixchar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.List;

public class MatrixChars {
    private volatile boolean escKeyPressed = false;
    private volatile Color bgColor = Color.black;

    public MatrixChars(int numBuffers, GraphicsDevice device) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::dispatchKeyEvent);

        try {
            GraphicsConfiguration gc = device.getDefaultConfiguration();
            Frame mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);
            device.setFullScreenWindow(mainFrame);

            Rectangle bounds = mainFrame.getBounds();
            mainFrame.createBufferStrategy(numBuffers);
            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();

            int x = 100;
            int y = -40;
            Color prevColor = bgColor;

            int baseFontSize = 25;
            int numLines = bounds.width / baseFontSize;
            System.out.println("Number of lines: " + numLines);
            Random rnd = new Random();
            List<SymbolLine> lines = IntStream.iterate(0, i -> i + 1)
                    .limit(numLines)
                    .mapToObj(i -> {
                        int lineX = 5 + i * (baseFontSize); // + (i + 1) * baseFontSize; //(i * x) + (rnd.nextInt(x - 10) + 10);
                        int lineY = -rnd.nextInt(40) * baseFontSize;
                        int fontSize = baseFontSize + rnd.nextInt(8);
                        double speed = rnd.nextDouble() + 0.1d;
                        return SymbolLine.generate(lineX, lineY, fontSize, bounds, speed);
                    }).toList();

            while (!escKeyPressed) {
                Graphics g = bufferStrategy.getDrawGraphics();
                g.setColor(Color.black);
                g.fillRect(0,0,bounds.width, bounds.height);
                lines.forEach(line -> line.updateAndDraw(g));
                bufferStrategy.show();
                g.dispose();

                Thread.sleep(90);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            device.setFullScreenWindow(null);
        }
    }

    private boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getID()) {
            case KeyEvent.KEY_RELEASED:
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    escKeyPressed = true;
                    return true;
                }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            int numBuffers = 2;
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            MatrixChars test = new MatrixChars(numBuffers, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

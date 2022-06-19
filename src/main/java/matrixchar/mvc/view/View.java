package matrixchar.mvc.view;

import matrixchar.mvc.model.LineModel;
import matrixchar.mvc.model.Model;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class View implements Closeable {
    private final static int NUM_BUFFERS = 2;

    private final GraphicsDevice graphicsDevice;

    private final BufferStrategy bufferStrategy;
    private final Rectangle bounds;

    private final List<LineView> lines;

    public View(Model model) {
        List<LineModel> modelLines = model.getSnapshot();
        lines = new ArrayList<>(modelLines.size());
        for (int i = 0; i < modelLines.size(); i++) {
            LineModel line = modelLines.get(i);
            lines.add(new LineView(line, i));
        }

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.graphicsDevice = env.getDefaultScreenDevice();

        GraphicsConfiguration gc = graphicsDevice.getDefaultConfiguration();
        Frame mainFrame = new Frame(gc);
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);

        graphicsDevice.setFullScreenWindow(mainFrame);
        mainFrame.createBufferStrategy(NUM_BUFFERS);

        bufferStrategy = mainFrame.getBufferStrategy();
        bounds = mainFrame.getBounds();
    }

    public void draw() {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, bounds.width, bounds.height);
        lines.stream().peek(LineView::update).forEach(line -> line.draw(g));
        bufferStrategy.show();
        g.dispose();
    }

    public void close() {
        graphicsDevice.setFullScreenWindow(null);
    }

    public static int getMaxLinesCount() {
        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        return (int) (screenWidth / (LineView.BASE_FONT_SIZE + LineView.ROW_MARGIN));
    }
}
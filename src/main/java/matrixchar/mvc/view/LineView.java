package matrixchar.mvc.view;

import matrixchar.mvc.model.LineModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.Random;


public class LineView {
    private static final String FONT_NAME = "TimesRoman";
    private static final int VERTICAL_POS_START_VARIANCE = 240;
    private static final long DELAY_SYMBOL_UPDATE_INTERVAL_MS = 300;

    private static final int FONT_SIZE_VARIANCE = 8;
    private static final int LEFT_MARGIN = 5;

    static final int ROW_MARGIN = 10;
    static final int BASE_FONT_SIZE = 12;

    private static final Color HEAD_COLOR = new Color(174, 255, 174);
    private static final Color BODY_COLOR = new Color(44, 128, 35);
    private static final Color BRIGHT_BODY_COLOR = new Color(0, 255, 0);

    private static final Random rnd = new Random();

    private final double screenHeight = getScreenHeight();
    private final LineModel line;
    private final int fontSize;
    private final int x;
    private double y;

    private final Font font;

    private long lastUpdateTime;

    public LineView(LineModel line, int modelPosX) {
        this.line = line;
        this.fontSize = BASE_FONT_SIZE + rnd.nextInt(FONT_SIZE_VARIANCE);
        this.font = new Font(FONT_NAME, Font.PLAIN, fontSize);
        this.x = modelPosX * (BASE_FONT_SIZE + ROW_MARGIN) + LEFT_MARGIN;
        this.y = genRandomVerticalShift(fontSize);
    }

    public void update() {
        this.y += fontSize;
        double lineLength = fontSize * line.getSymbolCount();
        boolean outOfScreen = y - lineLength > screenHeight;
        if (outOfScreen) {
            this.y = genRandomVerticalShift(fontSize);
        }
    }

    public void draw(Graphics g) {
        changeRandomSymbolIfTime();
        drawHead(g);
        drawBody(g);
        drawTail(g);
    }

    private void drawHead(Graphics g) {
        g.setColor(HEAD_COLOR);
        g.setFont(font);
        String sym = line.getBodyCellAt(0);
        g.drawString(sym, x, (int) (y));
    }

    private void drawBody(Graphics g) {
        g.setColor(BODY_COLOR);
        g.setFont(font);
        for (int i = 1;; i++) {
            String sym = line.getBodyCellAt(i);
            if (sym == null) {
                break;
            }
            int yPos = toScreenCoord(i);
            g.drawString(sym, x, yPos);
        }

        g.setColor(BRIGHT_BODY_COLOR);
        String ch = line.getBodyCellAt(1);
        if (ch != null) {
            int yPos = toScreenCoord(1);
            g.drawString(ch, x, yPos);
        }
    }

    private void drawTail(Graphics g) {
        g.setFont(font);
        int bodyLen = line.getSymbolCount();
        int tailLen = bodyLen * 2 / 3;

        double startGreen = 128.0;
        double endGreen = 8.0;
        double step = (startGreen - endGreen) / tailLen;

        double green = startGreen;
        for (int i = bodyLen - tailLen; i < bodyLen; i++) {
            Color symbolColor = new Color(0, (int) (green), 0);
            green -= step;
            g.setColor(symbolColor);

            String ch = line.getBodyCellAt(i);
            if (ch != null) {
                int posY = toScreenCoord(i);
                g.drawString(ch, x, posY);
            }
        }
    }

    private int toScreenCoord(int modelY) {
        return (int) y - modelY * fontSize;
    }

    private void changeRandomSymbolIfTime() {
        long time = System.currentTimeMillis();
        if (time - lastUpdateTime > DELAY_SYMBOL_UPDATE_INTERVAL_MS) {
            int ind = rnd.nextInt(line.getSymbolCount());
            line.updateOneSymbol(ind);
            lastUpdateTime = time;
        }
    }

    public static int genRandomVerticalShift(int fontSize) {
        return -rnd.nextInt(VERTICAL_POS_START_VARIANCE) * fontSize;
    }

    private static double getScreenHeight() {
        return Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }
}
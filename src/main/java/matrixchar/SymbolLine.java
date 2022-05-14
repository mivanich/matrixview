package matrixchar;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

public class SymbolLine {
    private static final Random rnd = new Random();

    private final String[] symbols;
    private final int x;
    private final int startY;
    private final int fontSize;
    private final Font font;
    private final Rectangle screenBounds;
    private double speed;
    private int y;
    private double pos;
    private int symbolPointer = 0;

    private final CharInRow[] positionedChars = new CharInRow[20];

    private SymbolLine(String[] symbols, int startX, int startY, int fontSize, Rectangle screenBounds, double speed) {
        this.symbols = symbols;
        this.x = startX;
        this.startY = startY;
        this.pos = startY;
        this.fontSize = fontSize;
        this.font = new Font("TimesRoman", Font.PLAIN, fontSize);
//        this.font = new Font("TimesRoman", Font.PLAIN, 15);
        this.screenBounds = screenBounds;
        this.speed = new Random().nextDouble() + 0.1d;
    }

    public static SymbolLine generate(int startX, int startY, int fontSize, Rectangle screenBounds, double speed) {
        Random rnd = new Random();
        String[] symbols = IntStream
                .iterate(0, i -> i + 1)
                .limit(20)
//                .mapToObj(ignore -> "S")
                .mapToObj(ignore -> genSymbol(rnd))
                .toArray(String[]::new);
        return new SymbolLine(symbols, startX, startY, fontSize, screenBounds, speed);
    }

    private static String genSymbol(Random rnd) {
        int code = rnd.nextInt(512) + 33;
        if (code >= 127 && code <= 161) code += 34;
        char ch = (char) code;
//        return ch + "=" + code;
        return String.valueOf(ch);
    }

    private void drawHead(Graphics g) {
//        Color symbolColor = new Color(0, 250, 0);
        Color symbolColor = new Color(174, 255, 174);
        g.setColor(symbolColor);
        String sym = genSymbol(rnd);

        g.drawString(sym, x, (int) (pos));
        positionedChars[symbolPointer] = new CharInRow(x, (int) pos, sym);
    }

    private void drawBody(Graphics g) {
        /*for (int i = symbolPointer - 1; i >= 0; i--) {
            Color symbolColor = new Color(44, 100, 35);
            g.setColor(symbolColor);
            g.drawString(symbols[i], x, (int) (pos - i * fontSize * 1.1));
        }*/

        Color symbolColor = new Color(44, 128, 35);
        g.setColor(symbolColor);
        for (int i = 0; i < positionedChars.length; i++) {
            if (positionedChars[i] != null && i != symbolPointer) {
                CharInRow ch = positionedChars[i];
                g.drawString(ch.s, ch.x, ch.y);
            }
        }

        symbolColor = new Color(0, 255, 0);
        g.setColor(symbolColor);
        int ind = (positionedChars.length + symbolPointer - 1) % positionedChars.length;
        if (positionedChars[ind] != null) {
            CharInRow ch = positionedChars[ind];
            g.drawString(ch.s, ch.x, ch.y);
        }
    }

    private void drawTail(Graphics g) {
        Color symbolColor = new Color(0, 64, 0);
//        Color symbolColor = new Color(128, 0, 0);
        g.setColor(symbolColor);

        int numTailChars = 5;
//        int startPosition = (symbolPointer + positionedChars.length - numTailChars) % positionedChars.length;
        int startPosition = symbolPointer % positionedChars.length;
        for (int k = 6; k < 9; k++) {
            int ind = (startPosition + k + 1) % positionedChars.length;
            if (positionedChars[ind] != null && ind != symbolPointer) {
                CharInRow ch = positionedChars[ind];
                g.drawString(ch.s, ch.x, ch.y);
            }
        }

        symbolColor = new Color(0, 32, 0);
        g.setColor(symbolColor);
        for (int k = 3; k < 6; k++) {
            int ind = (startPosition + k + 1) % positionedChars.length;
            if (positionedChars[ind] != null && ind != symbolPointer) {
                CharInRow ch = positionedChars[ind];
                g.drawString(ch.s, ch.x, ch.y);
            }
        }

        symbolColor = new Color(0, 16, 0);
        g.setColor(symbolColor);
        for (int k = 0; k < 3; k++) {
            int ind = (startPosition + k + 1) % positionedChars.length;
            if (positionedChars[ind] != null && ind != symbolPointer) {
                CharInRow ch = positionedChars[ind];
                g.drawString(ch.s, ch.x, ch.y);
            }
        }

        symbolColor = new Color(0, 8, 0);
        g.setColor(symbolColor);
        int ind = (startPosition + 1) % positionedChars.length;
        if (positionedChars[ind] != null && ind != symbolPointer) {
            CharInRow ch = positionedChars[ind];
            g.drawString(ch.s, ch.x, ch.y);
        }
    }

    public void updateAndDraw(Graphics g) {
        g.setFont(font);
        /*
            for (int i = 0; i < symbols.length; i++) {
                Color symbolColor = new Color(44, 100, 35);
                g.setColor(symbolColor);
                g.drawString(symbols[i], x, (int) (pos - i * fontSize * 1.1));
                this.pos += fontSize;
            }
        */
//        Color symbolColor = new Color(0, 200, 0);
//        g.setColor(symbolColor);
//        String sym = symbols[symbolPointer]; //String.valueOf((char) (rnd.nextInt(512) + 1));
//        g.drawString(sym, x, (int) (pos));

        drawHead(g);
        drawBody(g);
        drawTail(g);
        this.pos += fontSize;
        this.symbolPointer = (symbolPointer + 1) % symbols.length;

        boolean outOfScreen = pos - fontSize * 1.1 > screenBounds.height;
        if (outOfScreen) {
            this.pos = startY;
            this.speed = new Random().nextDouble() + 0.1d;
        }
    }

    public void updateAndDraw0(Graphics g) {
        g.setFont(font);
        {
            Color symbolColor = new Color(0, 220, 0);
            g.setColor(symbolColor);
            g.drawString(symbols[0], x, (int) (pos));
        }

        for (int i = 1; i < symbols.length; i++) {
//                    Color symbolColor = new Color(255 - i * 2, 220, 255 - i * 3 * 2);
//            Color symbolColor = new Color(240 - i * 4 * 2, 220, 255 - i * 8);
            Color symbolColor = new Color(44, 100, 35);
            g.setColor(symbolColor);
            g.drawString(symbols[i], x, (int) (pos - i * fontSize * 1.1));
        }
        this.pos += this.speed;

//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        double width = screenSize.getWidth();
//        double scrHeight = screenSize.getHeight();
//        System.out.println("the bounds=" + screenBounds + ", screenSize=" + screenSize);
        boolean outOfScreen = pos - fontSize * 1.1 * (symbols.length + 1) > screenBounds.height;
        if (outOfScreen) {
            this.pos = startY;
            this.speed = new Random().nextDouble() + 0.1d;
        }
    }
}

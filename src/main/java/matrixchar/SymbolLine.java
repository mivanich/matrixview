package matrixchar;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

public class SymbolLine {
    private static final int NUM_SYMBOLS_IN_ROW = 40;

    private static final Random rnd = new Random();

    private final String[] symbols;
    private final int x;
    private double y;
    private final int fontSize;
    private final Font font;
    private int headPosition = 0;

    private final long updateIntervalMs;
    private long lastUpdateTime;

    private final CharInRow[] positionedChars = new CharInRow[NUM_SYMBOLS_IN_ROW];

    private SymbolLine(String[] symbols, int startX, int fontSize) {
        this.symbols = symbols;
        this.x = startX;
        this.y = genRandomVerticalShift(fontSize);
        this.fontSize = fontSize;
        this.font = new Font("TimesRoman", Font.PLAIN, fontSize);
        this.lastUpdateTime = System.currentTimeMillis();
        this.updateIntervalMs = 100;
    }

    public static SymbolLine generate(int startX, int fontSize) {
        String[] symbols = IntStream
                .iterate(0, i -> i + 1)
                .limit(NUM_SYMBOLS_IN_ROW)
                .mapToObj(ignore -> genSymbol())
                .toArray(String[]::new);
        return new SymbolLine(symbols, startX, fontSize);
    }

    /*
    Block                                   Range       Comment
        CJK Unified Ideographs                  4E00-9FFF   Common
        CJK Unified Ideographs Extension A      3400-4DBF   Rare
        CJK Unified Ideographs Extension B      20000-2A6DF Rare, historic
        CJK Unified Ideographs Extension C      2A700–2B73F Rare, historic
        CJK Unified Ideographs Extension D      2B740–2B81F Uncommon, some in current use
?        CJK Unified Ideographs Extension E      2B820–2CEAF Rare, historic
        CJK Compatibility Ideographs            F900-FAFF   Duplicates, unifiable variants, corporate characters
        CJK Compatibility Ideographs Supplement 2F800-2FA1F Unifiable variants
     */
    private static String genSymbol() {
        /*int code = rnd.nextInt(512) + 33;
        if (code >= 127 && code <= 161) code += 34;
        char ch = (char) code;
//        return ch + "=" + code;
        return String.valueOf(ch);*/

        int rangeStart = 0x2B820;
        int rangeEnd = 0x2CEAF;
        int code = rnd.nextInt(rangeEnd - rangeStart) + rangeStart;
        char ch = (char) code;
        return String.valueOf(ch);
    }

    private void drawHead(Graphics g) {
        Color symbolColor = new Color(174, 255, 174);
        g.setColor(symbolColor);
        String sym = genSymbol();

        g.drawString(sym, x, (int) (y));
        positionedChars[headPosition] = new CharInRow(x, (int) y, sym);
    }

    private void drawBody(Graphics g) {
        Color symbolColor = new Color(44, 128, 35);
        g.setColor(symbolColor);
        for (int i = 0; i < positionedChars.length; i++) {
            if (positionedChars[i] != null && i != headPosition) {
                CharInRow ch = positionedChars[i];
                g.drawString(ch.s(), ch.x(), ch.y());
            }
        }

        symbolColor = new Color(0, 255, 0);
        g.setColor(symbolColor);
        int ind = (positionedChars.length + headPosition - 1) % positionedChars.length;
        if (positionedChars[ind] != null) {
            CharInRow ch = positionedChars[ind];
            g.drawString(ch.s(), ch.x(), ch.y());
        }
    }

    private void drawTail(Graphics g) {
        int bodyLen = positionedChars.length;
        int tailLen = bodyLen * 2 / 3;
        int tailStartPos = (headPosition + tailLen) % bodyLen + 1;

        double startGreen = 128.0;
        double endGreen = 8.0;
        double step = (startGreen - endGreen) / tailLen;

        double green = startGreen;
        for (int i = bodyLen; i >= (bodyLen - tailLen); i--) {
            Color symbolColor = new Color(0, (int) (green), 0);
            green -= step;
            g.setColor(symbolColor);

            int tailIndex = (tailStartPos + i) % bodyLen;
            CharInRow ch = positionedChars[tailIndex];
            if (ch != null) {
                g.drawString(ch.s(), ch.x(), ch.y());
            }
        }
    }

    private void updateVisibleSymbolIfTime() {
        long time = System.currentTimeMillis();
        if (time - lastUpdateTime > updateIntervalMs) {
            int ind = rnd.nextInt(positionedChars.length);
            var prev = positionedChars[ind];
            if (prev != null) {
                var newChar = new CharInRow(prev.x(), prev.y(), genSymbol());
                positionedChars[ind] = newChar;
            }
            lastUpdateTime = time;
        }
    }

    public void updateAndDraw(Graphics g) {
        g.setFont(font);

        updateVisibleSymbolIfTime();
        drawHead(g);
        drawBody(g);
        drawTail(g);

        this.y += fontSize;
        this.headPosition = (headPosition + 1) % symbols.length;

        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        boolean outOfScreen = y - fontSize * 1.1 > screenHeight;
        if (outOfScreen) {
            this.y = genRandomVerticalShift(fontSize);
        }
    }

    public static int genRandomVerticalShift(int fontSize) {
        return -rnd.nextInt(240) * fontSize;
    }
}

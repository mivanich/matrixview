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
    private double pos;
    private int symbolPointer = 0;

    private final CharInRow[] positionedChars = new CharInRow[20];

    private SymbolLine(String[] symbols, int startX, int startY, int fontSize) {
        this.symbols = symbols;
        this.x = startX;
        this.startY = startY;
        this.pos = startY;
        this.fontSize = fontSize;
        this.font = new Font("TimesRoman", Font.PLAIN, fontSize);
    }

    public static SymbolLine generate(int startX, int startY, int fontSize) {
        Random rnd = new Random();
        String[] symbols = IntStream
                .iterate(0, i -> i + 1)
                .limit(20)
//                .mapToObj(ignore -> "S")
                .mapToObj(ignore -> genSymbol(rnd))
                .toArray(String[]::new);
        return new SymbolLine(symbols, startX, startY, fontSize);
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
    private static String genSymbol(Random rnd) {
        /*int code = rnd.nextInt(512) + 33;
        if (code >= 127 && code <= 161) code += 34;
        char ch = (char) code;
//        return ch + "=" + code;
        return String.valueOf(ch);*/
        int rangeStart = 0x2B820;
        int rangeEnd = 0x2CEAF;

//        int code = rnd.nextInt(0x9FFF - 0x4E00) + 0x4E00;
        int code = rnd.nextInt(rangeEnd - rangeStart) + rangeStart;
        char ch = (char) code;
        return String.valueOf(ch);
//    0x4E00-0x9FFFh
    }

    private void drawHead(Graphics g) {
        Color symbolColor = new Color(174, 255, 174);
        g.setColor(symbolColor);
        String sym = genSymbol(rnd);

        g.drawString(sym, x, (int) (pos));
        positionedChars[symbolPointer] = new CharInRow(x, (int) pos, sym);
    }

    private void drawBody(Graphics g) {
        Color symbolColor = new Color(44, 128, 35);
        g.setColor(symbolColor);
        for (int i = 0; i < positionedChars.length; i++) {
            if (positionedChars[i] != null && i != symbolPointer) {
                CharInRow ch = positionedChars[i];
                g.drawString(ch.s(), ch.x(), ch.y());
            }
        }

        symbolColor = new Color(0, 255, 0);
        g.setColor(symbolColor);
        int ind = (positionedChars.length + symbolPointer - 1) % positionedChars.length;
        if (positionedChars[ind] != null) {
            CharInRow ch = positionedChars[ind];
            g.drawString(ch.s(), ch.x(), ch.y());
        }
    }

    private void drawTail(Graphics g) {
        Color symbolColor = new Color(0, 64, 0);
        g.setColor(symbolColor);

        int startPosition = symbolPointer % positionedChars.length;
        for (int k = 6; k < 9; k++) {
            int ind = (startPosition + k + 1) % positionedChars.length;
            if (positionedChars[ind] != null && ind != symbolPointer) {
                CharInRow ch = positionedChars[ind];
                g.drawString(ch.s(), ch.x(), ch.y());
            }
        }

        symbolColor = new Color(0, 32, 0);
        g.setColor(symbolColor);
        for (int k = 3; k < 6; k++) {
            int ind = (startPosition + k + 1) % positionedChars.length;
            if (positionedChars[ind] != null && ind != symbolPointer) {
                CharInRow ch = positionedChars[ind];
                g.drawString(ch.s(), ch.x(), ch.y());
            }
        }

        symbolColor = new Color(0, 16, 0);
        g.setColor(symbolColor);
        for (int k = 0; k < 3; k++) {
            int ind = (startPosition + k + 1) % positionedChars.length;
            if (positionedChars[ind] != null && ind != symbolPointer) {
                CharInRow ch = positionedChars[ind];
                g.drawString(ch.s(), ch.x(), ch.y());
            }
        }

        symbolColor = new Color(0, 8, 0);
        g.setColor(symbolColor);
        int ind = (startPosition + 1) % positionedChars.length;
        if (positionedChars[ind] != null && ind != symbolPointer) {
            CharInRow ch = positionedChars[ind];
            g.drawString(ch.s(), ch.x(), ch.y());
        }
    }

    public void updateAndDraw(Graphics g) {
        g.setFont(font);

        drawHead(g);
        drawBody(g);
        drawTail(g);

        this.pos += fontSize;
        this.symbolPointer = (symbolPointer + 1) % symbols.length;

        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        boolean outOfScreen = pos - fontSize * 1.1 > screenHeight;
        if (outOfScreen) {
            this.pos = startY;
//            this.speed = new Random().nextDouble() + 0.1d;
        }
    }
}

package matrixchar.mvc;

import matrixchar.mvc.controller.Controller;
import matrixchar.mvc.model.Model;
import matrixchar.mvc.view.View;

import java.util.Random;

public class Main {
    private static final int NUM_SYMBOLS_IN_ROW = 40;

    private final static Random rnd = new Random();

    public static void main(String ... args) {
        int numLines = View.getMaxLinesCount();
        Model model = new Model(numLines, NUM_SYMBOLS_IN_ROW, Main::symbolGenerator);

        View view = new View(model);
        Controller c = new Controller(model, view);
        c.start();
    }

    /** some unicode hieroglyph ranges
        Block                                   Range       Comment
        CJK Unified Ideographs                  4E00-9FFF   Common
        CJK Unified Ideographs Extension A      3400-4DBF   Rare
        CJK Unified Ideographs Extension B      20000-2A6DF Rare, historic
        CJK Unified Ideographs Extension C      2A700–2B73F Rare, historic
        CJK Unified Ideographs Extension D      2B740–2B81F Uncommon, some in current use
        CJK Unified Ideographs Extension E      2B820–2CEAF Rare, historic
        CJK Compatibility Ideographs            F900-FAFF   Duplicates, unifiable variants, corporate characters
        CJK Compatibility Ideographs Supplement 2F800-2FA1F Unifiable variants
    */
    private static char symbolGenerator() {
        int startChar = 0x4E00;
        int endChar = 0x9FFF;
        return (char) (rnd.nextInt(endChar - startChar) + startChar);
    }
}
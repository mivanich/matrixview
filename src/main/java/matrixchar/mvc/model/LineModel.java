package matrixchar.mvc.model;

import java.util.function.Supplier;

public class LineModel {
    private final String[] symbolsCyclicBuffer;
    private final Supplier<Character> symbolProvider;

    private int headIndex = 0;

    private LineModel(int numSymbols, Supplier<Character> symbolProvider) {
        this.symbolProvider = symbolProvider;
        this.symbolsCyclicBuffer = new String[numSymbols];
    }

    public static LineModel create(int numSymbols, Supplier<Character> symbolSupplier) {
        return new LineModel(numSymbols, symbolSupplier);
    }

    public void update() {
        symbolsCyclicBuffer[headIndex] = String.valueOf(symbolProvider.get());
        int len = symbolsCyclicBuffer.length;
        headIndex = (headIndex + 1) % len;
    }

    public String getBodyCellAt(int pos) {
        if (pos >= symbolsCyclicBuffer.length) {
            return null;
        }
        int ind = linearIndexToRelative(pos);
        return symbolsCyclicBuffer[ind];
    }

    public int getSymbolCount() {
        return symbolsCyclicBuffer.length;
    }

    public void updateOneSymbol(int position) {
        int ind = linearIndexToRelative(position);
        if (symbolsCyclicBuffer[ind] != null) {
            symbolsCyclicBuffer[ind] = String.valueOf(symbolProvider.get());
        }
    }

    private int linearIndexToRelative(int linearPos) {
        return (headIndex - linearPos + (symbolsCyclicBuffer.length - 1)) % symbolsCyclicBuffer.length;
    }
}
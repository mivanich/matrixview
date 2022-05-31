package matrixchar.mvc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Model {
    private final List<LineModel> lines;

    public Model(int numLines, int numSymbolsInRow, Supplier<Character> symbolProvider) {
        lines = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            lines.add(LineModel.create(numSymbolsInRow, symbolProvider));
        }
    }

    public void update() {
        lines.forEach(LineModel::update);
    }

    public List<LineModel> getSnapshot() {
        return lines;
    }
}
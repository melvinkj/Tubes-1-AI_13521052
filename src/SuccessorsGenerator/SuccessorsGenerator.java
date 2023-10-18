package SuccessorsGenerator;

import Main.GameState;

import java.util.List;

public interface SuccessorsGenerator {

    abstract public int calculateMaxDepth(GameState gameState, int maxLeafGenerated);
    abstract public List<int[]> generateSuccessors(GameState gameState);
}

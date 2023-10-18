package SuccessorsGenerator;

import Main.GameState;
import java.util.List;

public class DefaultSuccessorsGenerator implements SuccessorsGenerator {
    @Override
    public int calculateMaxDepth(GameState gameState, int maxLeafGenerated) {
        int countTree = 1;
        int depth = 0;
        int successorsCount = generateSuccessors(gameState).size();
        for (;;) {
            countTree *= (successorsCount - depth);
            if (countTree > maxLeafGenerated || successorsCount - depth <= 0) break;
            depth++;
        }
        return depth;
    }

    @Override
    public List<int[]> generateSuccessors(GameState gameState) {
        return gameState.getWhiteSpots();
    }
}

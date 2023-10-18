package GameStateEvaluator;

import Main.GameState;

public class VolatileNonVolatileGameStateEvaluator implements GameStateEvaluator {
    @Override
    public int evaluate(GameState gameState) {
        int countVolatileX = 0;
        int countVolatileO = 0;
        int countFixedX = 0;
        int countFixedO = 0;
        for (int i = 0; i < GameState.ROW; i++) {
            for (int j = 0; j < GameState.COL; j++) {
                if (gameState.node[i][j] == "X") {
                    if (gameState.isVolatile(i, j)) {
                        countFixedX++;
                    } else {
                        countVolatileX++;
                    }
                } else if (gameState.node[i][j] == "O") {
                    if (gameState.isVolatile(i, j)) {
                        countFixedO++;
                    } else {
                        countVolatileO++;
                    }
                }
            }
        }
        return 4 * (countFixedO - countFixedX) + 1 * (countVolatileO - countVolatileX);
    }
}

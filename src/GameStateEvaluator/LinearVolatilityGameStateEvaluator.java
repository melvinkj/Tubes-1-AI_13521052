package GameStateEvaluator;

import Main.GameState;

public class LinearVolatilityGameStateEvaluator implements GameStateEvaluator {

    @Override
    public int evaluate(GameState gameState) {
        int volatilityScoreX = 0;
        int volatilityScoreO = 0;
        for (int i = 0; i < GameState.ROW; i++) {
            for (int j = 0; j < GameState.COL; j++) {
                if (gameState.node[i][j] == "X") {
                    volatilityScoreX += gameState.volatilityScore(i, j);
                } else if (gameState.node[i][j] == "O") {
                    volatilityScoreO += gameState.volatilityScore(i, j);
                }
            }
        }
        return volatilityScoreO - volatilityScoreX;
    }
}

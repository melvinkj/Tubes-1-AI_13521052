package Evaluator;

import Evaluator.Evaluator;
import Main.GameState;

import java.util.HashMap;
import java.util.Map;

public class GoodBadPointEvaluator implements Evaluator {
    @Override
    public int evaluate(GameState gameState) {
        int countX = 0;
        int countO = 0;
        int countBadX = 0;
        int countBadO = 0;
        Map<int[], Boolean> cache = new HashMap<>();
        int[][] badVector = new int[][] {{-2, 0}, {2, 0}, {0, -2}, {0, 2}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int i = 0; i < GameState.ROW; i++) {
            for (int j = 0; j < GameState.COL; j++) {
                if (gameState.node[i][j].equals("X")) {
                    countX++;
                    for (int[] vector: badVector) {
                        int _i = i + vector[0];
                        int _j = j + vector[1];
                        int[] coor = new int[]{_i, _j};
                        if (!GameState.isNodeValid(_i, _j) || cache.containsKey(coor)) {
                            continue;
                        };
                        cache.put(coor, true);
                        if (gameState.node[_i][_j].equals("X")) {
                            countBadX ++;
                        }
                    }
                } else if (gameState.node[i][j].equals("O")) {
                    countO++;
                    for (int[] vector: badVector) {
                        int _i = i + vector[0];
                        int _j = j + vector[1];
                        int[] coor = new int[]{_i, _j};
                        if (!GameState.isNodeValid(_i, _j) || cache.containsKey(coor)) {
                            continue;
                        };
                        cache.put(coor, true);
                        if (gameState.node[_i][_j].equals("O")) {
                            countBadO ++;
                        }
                    }
                }
            }
        }
        int w1 = 2;
        int w2 = 1;
        int f1 = countX - countO;
        int f2 = countBadO - countBadX;
        int evaluationScore = w1 * f1 + w2 * f2;
        return evaluationScore;
    }
}

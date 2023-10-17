import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MinimaxBot extends Bot {
    private static int ROW = 8;
    private static int COL = 8;
    public static int MAX_PLAYER = 1;
    public static int MIN_PLAYER = 0;

    private static int INF = 999999999;

    public MinimaxBot(OutputFrameController gameBoard) {
        super(gameBoard);
    }

    public int[] move() {
        this.getCurrentState().printBoard();
        System.out.println();
        return bestMoveMinimax();
    }

    public int[] bestMoveMinimax() {
        int defaultMaxTreeGenerated = 1000000;
        return bestMoveMinimax(defaultMaxTreeGenerated, MinimaxBot::defaultNeighboursGenerator);
    }

    public int[] bestMoveMinimax(int maxTreeGenerated, Function<GameState, List<int[]>> neighboursGenerator) {
        List<int[]> possibleMoves = neighboursGenerator.apply(this.getCurrentState());
        int[] bestMove = possibleMoves.get(0);
        int bestMoveValue = -INF;
        int countNeighbours = possibleMoves.size();
        int maxDepth = calculateMaxDepthMinimax(countNeighbours, maxTreeGenerated);
        int alpha = -INF;
        int beta = INF;
        for (int[] possibleMove : possibleMoves) {
            GameState projectedState = new GameState(currentState);
            projectedState.putO(possibleMove[0], possibleMove[1]);
            int projectedStateValue = minimax(MAX_PLAYER, projectedState, alpha, beta, maxDepth, neighboursGenerator);
            if (projectedStateValue > bestMoveValue) {
                bestMove = possibleMove;
                bestMoveValue = projectedStateValue;
            }
        }
        return bestMove;
    }

    public int minimax(int player, GameState gameState, int alpha, int beta, int depth, Function<GameState, List<int[]>> neighboursGenerator) {
        List<int[]> neighbours = neighboursGenerator.apply(gameState);

        boolean isTerminate = neighbours.isEmpty();
        if (isTerminate) {
            System.out.println("UTIL" + gameState.utility());
            return gameState.utility();
        } else if (depth <= 0) {
            System.out.println("EVAL"  + gameState.evaluate());
            return gameState.evaluate();
        }
        if (player == MAX_PLAYER) {
            int maxNeighbourValue = -INF;
            for (int[] neighbour : neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putO(neighbour[0], neighbour[1]);
                int neighbourValue = minimax(MIN_PLAYER, projectedState, alpha, beta, depth - 1, neighboursGenerator);
                maxNeighbourValue = Math.max(neighbourValue, maxNeighbourValue);
                alpha = Math.max(neighbourValue, alpha);
                if (beta <= alpha) break;
            }
            return maxNeighbourValue;
        } else {
            int minNeighbourValue = INF;
            for (int[] neighbour : neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putX(neighbour[0], neighbour[1]);
                int neighbourValue = minimax(MAX_PLAYER, projectedState, alpha, beta, depth - 1, neighboursGenerator);
                minNeighbourValue = Math.min(neighbourValue, minNeighbourValue);
                beta = Math.min(neighbourValue, beta);
                if (beta <= alpha) break;
            }
            return minNeighbourValue;
        }
    }

    public int calculateMaxDepthMinimax(int neighboursCount, int maxTreeGenerated) {
        int countTree = 1;
        int depth = 0;
        for (;;) {
            countTree *= (neighboursCount - depth);
            if (countTree > maxTreeGenerated || neighboursCount - depth <= 0) break;
            depth++;
        }
        return depth;
    }

    private static List<int[]> defaultNeighboursGenerator(GameState gameState) {
        return gameState.getWhiteSpots();
    }

}

import java.util.ArrayList;
import java.util.List;

public class MinimaxBot extends Bot {
    private static int ROW = 8;
    private static int COL = 8;
    public static int MAX_PLAYER = 1;
    public static int MIN_PLAYER = 0;

    public MinimaxBot(OutputFrameController gameBoard) {
        super(gameBoard);
    }

    public int[] move() {
        this.getCurrentState().printBoard();
        System.out.println();
        return bestMoveMinimax();
    }

    public int[] bestMoveMinimax() {
        int MAX_TREE_GENERATED = 100000;

        List<int[]> possibleMoves = currentState.getWhiteSpots();
        int[] bestMove = possibleMoves.get(0);
        int bestMoveValue = -999;
        int countNeighbours = possibleMoves.size();
        int maxDepth = calculateMaxDepthMinimax(countNeighbours, MAX_TREE_GENERATED);
        int alpha = -999;
        int beta = 999;
        for (int[] possibleMove : possibleMoves) {
            GameState projectedState = new GameState(currentState);
            projectedState.putO(possibleMove[0], possibleMove[1]);
            int projectedStateValue = minimax(MAX_PLAYER, projectedState, alpha, beta, maxDepth, true);
            if (projectedStateValue > bestMoveValue) {
                bestMove = possibleMove;
                bestMoveValue = projectedStateValue;
                System.out.println(bestMoveValue);
            }
        }
        return bestMove;
    }

    public int minimax(int player, GameState gameState, int alpha, int beta, int depth, boolean withCutoff) {
        List<int[]> neighbours = gameState.getWhiteSpots();
        boolean isTerminate = neighbours.isEmpty() || (withCutoff && depth <= 0);
        if (isTerminate) {
            if (withCutoff) {
                return gameState.evaluate();
            } else {
                return gameState.utility();
            }
        }
        if (player == MAX_PLAYER) {
            int maxNeighbourValue = -999;
            for (int[] neighbour : neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putO(neighbour[0], neighbour[1]);
                int neighbourValue = minimax(MIN_PLAYER, projectedState, alpha, beta, withCutoff ? depth - 1 : depth, withCutoff);
                maxNeighbourValue = Math.max(neighbourValue, maxNeighbourValue);
                alpha = Math.max(neighbourValue, alpha);
                if (beta <= alpha) break;
            }
            return maxNeighbourValue;
        } else {
            int minNeighbourValue = 999;
            for (int[] neighbour : neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putX(neighbour[0], neighbour[1]);
                int neighbourValue = minimax(MAX_PLAYER, projectedState, alpha, beta, withCutoff ? depth - 1 : depth, withCutoff);
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
}

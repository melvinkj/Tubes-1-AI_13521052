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
        return bestMoveMinimaxWithCutoff();
    }

    public int[] bestMoveMinimax() {
        List<int[]> possibleMoves = getCurrentState().getWhiteSpots();
        int[] bestMove = possibleMoves.get(0);
        int bestMoveValue = -999;
        for (int[] possibleMove: possibleMoves) {
            GameState projectedState = new GameState(getCurrentState());
            projectedState.putO(possibleMove[0], possibleMove[1]);
            int projectedStateValue = minimax(MAX_PLAYER, projectedState, -999, 999);
            if (projectedStateValue > bestMoveValue) {
                bestMove = possibleMove;
                bestMoveValue = projectedStateValue;
                System.out.println(bestMoveValue);
            }
        }
        return bestMove;
    }
    public int minimax(int player, GameState gameState, int alpha, int beta) {
        List<int[]> neighbours = getCurrentState().getWhiteSpots();

        boolean isTerminate = neighbours.isEmpty();
        if (isTerminate) {
            return gameState.utility();
        }
        if (player == MAX_PLAYER) {
            int maxNeighbourValue = -999;
            for (int[] neighbour: neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putO(neighbour[0], neighbour[1]);
                int neighbourValue = minimax(MIN_PLAYER, projectedState, alpha, beta);
                maxNeighbourValue = neighbourValue > maxNeighbourValue ? neighbourValue : maxNeighbourValue;
                alpha = neighbourValue > alpha ? neighbourValue: alpha;
                if (beta <= alpha) break;
            }
            return maxNeighbourValue;
        } else {
            int minNeighbourValue = 999;
            for (int[] neighbour: neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putX(neighbour[0], neighbour[1]);
                int neighbourValue = minimax(MAX_PLAYER, projectedState, alpha, beta);
                minNeighbourValue = neighbourValue < minNeighbourValue ? neighbourValue : minNeighbourValue;
                beta = neighbourValue < beta ? neighbourValue: beta;
                if (beta <= alpha) break;
            }
            return minNeighbourValue;
        }
    }

    public int[] bestMoveMinimaxWithCutoff() {
        int MAX_TREE_GENERATED = 20000;

        List<int[]> possibleMoves = getCurrentState().getWhiteSpots();
        int[] bestMove = possibleMoves.get(0);
        int bestMoveValue = -999;
        int countNeighbours = possibleMoves.size();
        int maxDepth = calculateMaxDepthMinimax(countNeighbours, MAX_TREE_GENERATED);
        int alpha = -999;
        int beta = 999;
        for (int[] possibleMove: possibleMoves) {
            GameState projectedState = new GameState(getCurrentState());
            projectedState.putO(possibleMove[0], possibleMove[1]);
            int projectedStateValue = minimaxWithCutoff(MAX_PLAYER, projectedState, alpha, beta, maxDepth);
            if (projectedStateValue > bestMoveValue) {
                bestMove = possibleMove;
                bestMoveValue = projectedStateValue;
                System.out.println(bestMoveValue);
            }
        }
        return bestMove;
    }


    public int minimaxWithCutoff(int player, GameState gameState, int alpha, int beta, int depth) {
        List<int[]> neighbours = gameState.getWhiteSpots();
        boolean isTerminate = neighbours.isEmpty();
        if (isTerminate || depth <= 0) {
            return gameState.evaluate();
        }
        if (player == MAX_PLAYER) {
            int maxNeighbourValue = -999;
            for (int[] neighbour: neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putO(neighbour[0], neighbour[1]);
                int neighbourValue = minimaxWithCutoff(MIN_PLAYER, projectedState, alpha, beta, depth-1);
                maxNeighbourValue = neighbourValue > maxNeighbourValue ? neighbourValue : maxNeighbourValue;
                alpha = neighbourValue > alpha ? neighbourValue: alpha;
                if (beta <= alpha) break;
            }
            return maxNeighbourValue;
        } else {
            int minNeighbourValue = 999;
            for (int[] neighbour: neighbours) {
                GameState projectedState = new GameState(gameState);
                projectedState.putX(neighbour[0], neighbour[1]);
                int neighbourValue = minimaxWithCutoff(MAX_PLAYER, projectedState, alpha, beta, depth-1);
                minNeighbourValue = neighbourValue < minNeighbourValue ? neighbourValue : minNeighbourValue;
                beta = neighbourValue < beta ? neighbourValue: beta;
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

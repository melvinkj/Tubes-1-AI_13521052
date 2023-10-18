package Bot;

import GameStateEvaluator.GameStateEvaluator;
import Main.GameState;
import Main.OutputFrameController;
import SuccessorsGenerator.SuccessorsGenerator;

import java.util.List;
import java.util.function.Function;

public class MinimaxBot extends Bot {
    public static int MAX_PLAYER = 1;
    public static int MIN_PLAYER = 0;
    private static int INF = 999999999;
    private GameStateEvaluator evaluator;
    private SuccessorsGenerator generator;
    private int maxLeafGenerated;

    public MinimaxBot(OutputFrameController gameBoard, GameStateEvaluator evaluator, SuccessorsGenerator generator, int maxLeafGenerated) {
        super(gameBoard);
        this.evaluator = evaluator;
        this.generator = generator;
        this.maxLeafGenerated = maxLeafGenerated;
    }

    @Override
    public int[] getBestMove() {
        System.out.println("===================================================================");
        getCurrentState().printBoard();
        System.out.println("SUCC COUNT = " + generator.generateSuccessors(getCurrentState()).size());
        System.out.println("MAX DEPTH = " + generator.calculateMaxDepth(getCurrentState(), maxLeafGenerated));
        System.out.println("===================================================================");
        return bestMoveMinimax();
    }

    public int[] bestMoveMinimax() {
        int maxDepth = generator.calculateMaxDepth(getCurrentState(), maxLeafGenerated);
        return bestMoveMinimax(maxDepth);
    }

    public int[] bestMoveMinimax(int maxDepth) {
        GameState currentState = getCurrentState();
        List<int[]> possibleMoves = generator.generateSuccessors(currentState);
        int[] bestMove = possibleMoves.get(0);
        int bestMoveValue = -INF;
        int alpha = -INF;
        int beta = INF;
        for (int[] possibleMove : possibleMoves) {
            GameState projectedState = new GameState(currentState);
            projectedState.putO(possibleMove[0], possibleMove[1]);
            int projectedStateValue = minimax(MAX_PLAYER, projectedState, alpha, beta, maxDepth);
            if (projectedStateValue > bestMoveValue) {
                bestMove = possibleMove;
                bestMoveValue = projectedStateValue;
            }
        }
        return bestMove;
    }

    public int minimax(int player, GameState gameState, int alpha, int beta, int depth) {
        List<int[]> successors = generator.generateSuccessors(gameState);

        boolean isTerminate = successors.isEmpty();
        if (isTerminate) {
            return gameState.utility();
        } else if (depth <= 0) {
            return evaluator.evaluate(gameState);
        }
        if (player == MAX_PLAYER) {
            int maxSuccessorValue = -INF;
            for (int[] successor : successors) {
                GameState projectedState = new GameState(gameState);
                projectedState.putO(successor[0], successor[1]);
                int successorValue = minimax(MIN_PLAYER, projectedState, alpha, beta, depth - 1);
                maxSuccessorValue = Math.max(successorValue, maxSuccessorValue);
                alpha = Math.max(successorValue, alpha);
                if (beta <= alpha) break;
            }
            return maxSuccessorValue;
        } else {
            int minSuccessorValue = INF;
            for (int[] successor : successors) {
                GameState projectedState = new GameState(gameState);
                projectedState.putX(successor[0], successor[1]);
                int successorValue = minimax(MAX_PLAYER, projectedState, alpha, beta, depth - 1);
                minSuccessorValue = Math.min(successorValue, minSuccessorValue);
                beta = Math.min(successorValue, beta);
                if (beta <= alpha) break;
            }
            return minSuccessorValue;
        }
    }
}

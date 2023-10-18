package Bot;

import GameStateEvaluator.GameStateEvaluator;
import Main.GameState;
import Main.OutputFrameController;
import SuccessorsGenerator.SuccessorsGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

public class MinimaxBot extends Bot {
    public static int MAX_PLAYER = 1;
    public static int MIN_PLAYER = 0;
    private static int INF = 999999999;
    private static int DEF_LEAF_GENERATED = 1000000;
    private static int EXECUTOR_NUM = 8;


    private GameStateEvaluator evaluator;
    private SuccessorsGenerator generator;
    private ExecutorService executorService;
    private ExecutorService idsExecutor;



    public MinimaxBot(OutputFrameController gameBoard, GameStateEvaluator evaluator, SuccessorsGenerator generator) {
        super(gameBoard);
        this.evaluator = evaluator;
        this.generator = generator;
        this.executorService = Executors.newFixedThreadPool(EXECUTOR_NUM);
        this.idsExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public int[] getBestMove() throws Exception {
        System.out.println("===================================================================");
        getCurrentState().printBoard();
        System.out.println("SUCC COUNT = " + generator.generateSuccessors(getCurrentState()).size());
        System.out.println("INITIAL DEPTH = " + generator.calculateMaxDepth(getCurrentState(), DEF_LEAF_GENERATED));

        IDSRunner idsRunner = new IDSRunner(this);
        Future<?> future = idsExecutor.submit(idsRunner);
        try {
            future.get(4700, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            System.out.println("IDS DEPTH = " + idsRunner.getDepth());
            return idsRunner.getResult();
        } finally {
            idsExecutor.shutdownNow();
            idsExecutor = Executors.newSingleThreadExecutor();
        }
        System.out.println("IDS DEPTH = " + idsRunner.getDepth());
        return idsRunner.getResult();
    }

    public int[] bestMoveMinimax() {
        int maxDepth = generator.calculateMaxDepth(getCurrentState(), DEF_LEAF_GENERATED);
        return bestMoveMinimaxWOThreading(maxDepth);
    }

    public int[] bestMoveMinimax(int maxDepth) {
        GameState currentState = getCurrentState();
        List<int[]> possibleMoves = generator.generateSuccessors(currentState);
        int[] bestMove = possibleMoves.get(0);
        int bestMoveValue = -INF;
        int alpha = -INF;
        int beta = INF;
        List<Future<Integer>> futures = new ArrayList<>();

        for (int[] possibleMove : possibleMoves) {
            Future<Integer> future = executorService.submit(() -> {
                GameState projectedState = new GameState(currentState);
                projectedState.putO(possibleMove[0], possibleMove[1]);
                return minimax(MAX_PLAYER, projectedState, alpha, beta, maxDepth);
            });
            futures.add(future);
        }

        for (int i = 0; i < possibleMoves.size(); i++) {
            try {
                int projectedStateValue = futures.get(i).get();
                if (projectedStateValue > bestMoveValue) {
                    bestMove = possibleMoves.get(i);
                    bestMoveValue = projectedStateValue;
                }
            } catch (InterruptedException | ExecutionException e) {
                executorService.shutdownNow();
                executorService = Executors.newFixedThreadPool(EXECUTOR_NUM);
            }
        }
        return bestMove;
    }

    public int[] bestMoveMinimaxWOThreading(int maxDepth) {
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

    public SuccessorsGenerator getGenerator() {
        return this.generator;
    }

    public GameStateEvaluator getEvaluator() {
        return this.evaluator;
    }

}

class IDSRunner implements Runnable {
    volatile int[] result;
    volatile int depth;
    MinimaxBot bot;

    public IDSRunner(MinimaxBot bot) {
        this.bot = bot;
    }

    public int[] getResult() {
        return this.result;
    }

    public int getDepth() {
        return this.depth;
    }

    @Override
    public void run() {
        GameState currentState = bot.getCurrentState();
        int finalDepth = Math.min(bot.getGenerator().generateSuccessors(currentState).size(), currentState.getRoundsLeft());
        int maxDepth = bot.getGenerator().calculateMaxDepth(currentState, 1000000);
        while (!Thread.currentThread().isInterrupted() && maxDepth <= finalDepth) {
            this.result = bot.bestMoveMinimax(maxDepth);
            this.depth = maxDepth;
            maxDepth++;
        }
    }
}
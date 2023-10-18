package Bot;

import GameStateEvaluator.GameStateEvaluator;
import GameStateEvaluator.VolatileNonVolatileGameStateEvaluator;
import Main.GameState;
import Main.OutputFrameController;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public abstract class Bot implements Callable<int[]> {

    public String symbol = "O";
    public GameState currentState;
    private GameStateEvaluator defaultEvaluator;

    public Bot(OutputFrameController gameBoard) {
        this.defaultEvaluator = new VolatileNonVolatileGameStateEvaluator();
        this.currentState = new GameState(gameBoard);
    }

    public Bot(GameState currentState) {
        this.currentState = currentState;
    }

    public Bot(OutputFrameController gameBoard, String symbol){
        this.currentState = new GameState(gameBoard);
        this.symbol = symbol;
    }

    @Override
    public int[] call() throws Exception {
        return getBestMove();
    }

    public GameState getCurrentState() {
        this.currentState.copyFromGameboard();
        return this.currentState;
    }

    public String getSymbol() {
        return symbol;
    }

    public abstract int[] getBestMove() throws Exception;

    public int[] getDefaultMove() {
        int bestValue = -999;
        int[] bestMove = null;

        GameState currentState = getCurrentState();
        List<int[]> successors = currentState.getWhiteSpots();
        for (int[] successor : successors) {
            GameState projectedState = new GameState(currentState);
            projectedState.putO(successor[0], successor[1]);
            int successorValue = defaultEvaluator.evaluate(projectedState);
            if (successorValue > bestValue) {
                bestValue = successorValue;
                bestMove = successor;
            }
        }
        return bestMove;
    }

    public int[] move() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<int[]> future = executorService.submit(this);
        try {
            int[] result = future.get(4900, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new Exception();
            }
            return result;
        } catch (Exception e) {
            System.out.println("Implementator failed to give result on time, default move executed.");
            return getDefaultMove();
        } finally {
            executorService.shutdown();
        }
    }

}
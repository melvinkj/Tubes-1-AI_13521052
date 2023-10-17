public abstract class Bot {
    public GameState currentState;

    public Bot(OutputFrameController gameBoard) {
        this.currentState = new GameState(gameBoard);
    }
    public Bot(GameState currentState) {
        this.currentState = currentState;
    }

    public GameState getCurrentState() {
        this.currentState.copyFromGameboard();
        return this.currentState;
    }

    public abstract int[] move();
}
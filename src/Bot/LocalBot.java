package Bot;

import Main.GameState;
import Main.OutputFrameController;

public class LocalBot extends Bot {

    private static int ROW = 8;
    private static int COL = 8;


    private static final double COOLING_RATE = 0.95;

    private static double temperature = 1;

    public LocalBot(OutputFrameController gameBoard) {
        super(gameBoard);
    }
    public LocalBot(OutputFrameController gameBoard, String symbol){
        super(gameBoard, symbol);
    }

    public int evaluate(GameState state) {
        int botPoint = 0;
        int enemyPoint = 0;
        String enemySymbol = "";
        if(symbol.equals("O")){
            enemySymbol = "X";
        }else{
            enemySymbol = "O";
        }

        int[][] surroundCheck = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (state.node[i][j].equals(symbol)) {
                    botPoint++;
                    boolean check = true;
                    for (int[] vector : surroundCheck) {
                        int _i = i + vector[0];
                        int _j = j + vector[1];
                        if (!state.isNodeValid(_i, _j) || !state.node[_i][_j].equals(symbol)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        botPoint += 3;
                    }
                } else if (state.node[i][j].equals(enemySymbol)) {
                    enemyPoint++;
                    boolean check = true;
                    for (int[] vector : surroundCheck) {
                        int _i = i + vector[0];
                        int _j = j + vector[1];
                        if (!state.isNodeValid(_i, _j) || !state.node[_i][_j].equals(enemySymbol)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        enemyPoint += 3;
                    }
                }
            }
        }
            return (botPoint - enemyPoint);
        }

        @Override
        public int[] getBestMove() {
            boolean accept = false;
            double threshold = 0.9;
            int neighbourValue = 0;
            int iteration = 0;
            int[] move = {};

            while (!accept && iteration < 100) {
                iteration++;
                boolean validMove = false;
                GameState temp = new GameState(getCurrentState());

                // langkah bot
                while (!validMove) {
                    int i = (int) (Math.random() * 8);
                    int j = (int) (Math.random() * 8);
                    if (temp.node[i][j].equals("")) {
                        validMove = true;
                        if(symbol.equals("O")){
                            temp.putO(i, j);
                        }else{
                            temp.putX(i,j);
                        }
                        move = new int[]{i, j};
                    }
                }

                // langkah lawan (anggap lawan melakukan greedy)
                int[] bestMove = {0, 0};
                int bestValue = 0;
                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        if (temp.node[i][j].equals("")) {
                            int _i = i;
                            int _j = j;
                            GameState temp2 = new GameState(getCurrentState());
                            if(symbol.equals("O")){
                                temp2.putX(i, j);
                            }else{
                                temp2.putO(i,j);
                            }
                            int value = -1 * evaluate(temp2);
                            if (value > bestValue) {
                                bestValue = value;
                                bestMove = new int[]{_i, _j};
                            }

                        }
                    }
                }
                if(symbol.equals("O")){
                    temp.putX(bestMove[0], bestMove[1]);
                }else{
                    temp.putO(bestMove[0], bestMove[1]);
                }

                neighbourValue = evaluate(temp);
                int currentValue = evaluate(getCurrentState());

                if (neighbourValue > currentValue) {
                    accept = true;
                } else {
                    int diff = neighbourValue - currentValue;
                    double prob = Math.exp(diff / temperature);
                    temperature = temperature * COOLING_RATE;
                    if (prob > threshold) {
                        accept = true;
                    }
                }
            }

            return move;
        }
    }

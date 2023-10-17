import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    public static int ROW = 8;
    public static int COL = 8;

    OutputFrameController gameBoard;

    String[][] node;

    public GameState(OutputFrameController gameBoard) {
        this.gameBoard = gameBoard;

        copyFromGameboard();
    }

    public GameState() {
        this.gameBoard = new OutputFrameController();

        this.node = new String[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                this.node[i][j] = "";
            }
        }
    }

    public GameState(GameState other) {
        this.gameBoard = other.gameBoard;

        this.node = new String[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                this.node[i][j] = other.node[i][j];
            }
        }
    }

    public void copyFromGameboard() {
        this.node = new String[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                this.node[i][j] = this.gameBoard.getButtons()[i][j].getText();
            }
        }
    }


    public int evaluate() {
        int countX = 0;
        int countO = 0;
        int countBadX = 0;
        int countBadO = 0;
        Map <int[], Boolean> cache = new HashMap<>();
        int[][] badVector = new int[][] {{-2, 0}, {2, 0}, {0, -2}, {0, 2}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (node[i][j].equals("X")) {
                    countX++;
                    for (int[] vector: badVector) {
                        int _i = i + vector[0];
                        int _j = j + vector[1];
                        int[] coor = new int[]{_i, _j};
                        if (!isNodeValid(_i, _j) || cache.containsKey(coor)) {
                          continue;
                        };
                        cache.put(coor, true);
                        if (node[_i][_j].equals("X")) {
                            countBadX ++;
                        }
                    }
                } else if (node[i][j].equals("O")) {
                    countO++;
                    for (int[] vector: badVector) {
                        int _i = i + vector[0];
                        int _j = j + vector[1];
                        int[] coor = new int[]{_i, _j};
                        if (!isNodeValid(_i, _j) || cache.containsKey(coor)) {
                            continue;
                        };
                        cache.put(coor, true);
                        if (node[_i][_j].equals("O")) {
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
    };

    public int utility() {
        int countX = 0;
        int countO = 0;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (node[i][j].equals("X")) {
                    countX++;
                } else if (node[i][j].equals("O")) {
                    countO++;
                }
            }
        }

        if (countX > countO) {
            return -1;
        } else if (countX < countO) {
            return 1;
        } else {
            return 0;
        }
    }

    public List<int[]> getWhiteSpots() {
        List<int[]> whiteSpots = new ArrayList<>();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (node[i][j].equals("")) {
                    whiteSpots.add(new int[]{i, j});
                }
            }
        }
        return whiteSpots;
    }

    public void putO(int i, int j) {
        node[i][j] = "O";
        int[][] valVector = new int[][]{{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
        for (int[] vector: valVector) {
            int _i = i - vector[0];
            int _j = j - vector[1];
            if (isNodeValid(_i, _j) && node[_i][_j].equals("X")) {
                node[_i][_j] = "O";
            }
        }
    }

    public boolean isNodeValid(int i, int j) {
        return (i >= 0 && i < ROW && j >= 0 && j < ROW);
    }
    public void putX(int i, int j) {
        node[i][j] = "X";
        int[][] valVector = new int[][]{{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
        for (int[] vector: valVector) {
            int _i = i - vector[0];
            int _j = j - vector[1];
            if (isNodeValid(_i, _j) && node[_i][_j].equals("O")) {
                node[_i][_j] = "X";
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < ROW; i++) {
            System.out.print("|");
            for (int j = 0; j < COL; j++) {
                if (node[i][j] == "") {
                    System.out.print(" ");
                } else {
                    System.out.print(node[i][j]);
                }
                System.out.print("|");
            }
            System.out.println();

        }
    }

    public String[][] getNode() {
        return this.node;
    }
}

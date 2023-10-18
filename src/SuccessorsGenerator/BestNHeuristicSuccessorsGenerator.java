package SuccessorsGenerator;

import Main.GameState;

import javax.swing.*;
import java.util.List;

public class BestNHeuristicSuccessorsGenerator implements SuccessorsGenerator {

    @Override
    public int calculateMaxDepth(GameState gameState, int maxLeafGenerated) {
        return 0;
    }

    @Override
    public List<int[]> generateSuccessors(GameState gameState) {
        return null;
    }
}

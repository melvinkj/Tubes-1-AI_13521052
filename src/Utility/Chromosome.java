package Utility;

import Main.GameState;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Chromosome {
    public List<int[]> genes;

    public int size;
    public int fitnessValue ;

    public String pawn;

    public Chromosome(List<int[]> genes, Boolean isX){
        this.genes = genes;
        this.fitnessValue = 0;
        if (isX) {
            this.pawn = "X";
        } else {
            this.pawn = "O";
        }
        this.size = genes.size();
    }

    public List<int[]> getGenes(){
        return genes;
    }

    public int getFitnessValue() {
        return fitnessValue;
    }

    public int getSize(){
        return size;
    }

    public int[] getGeneAt(int index){
        return genes.get(index);
    }

    public void setGeneAt(int index, int[] gene){
        this.genes.set(index, gene);
    }

    public boolean containsGene(int[] geneTarget) {
        for (int[] gene : this.genes) {
            if (gene != null && gene[0] == geneTarget[0] && gene[1] == geneTarget[1]){
                return true;
            }
        }
        return false;
    }

    public int fitnessFunction(GameState initialState){
        GameState finalState = simulate(initialState);

        int value = 0;
        for (int i = 0; i < finalState.ROW; i++){
            for (int j = 0; j < finalState.COL; j++){
                if (finalState.node[i][j].equals(this.pawn)){
                    value++;
                }
            }
        }
        this.fitnessValue = value;
        return value;
    }

    public void mutation(){
        if (size <=1) {
            return;
        }
        Random random = new Random();
        int randomValue = random.nextInt(size - 1);
        int[] temp = this.getGeneAt(randomValue);
        this.setGeneAt(randomValue, this.getGeneAt(randomValue + 1));
        this.setGeneAt(randomValue+1, temp);
    }

    public void randomizeGeneSequence(){
        Collections.shuffle(genes);
    }
    public GameState simulate(GameState currentState){
        System.out.println(genes);


        GameState state = new GameState(currentState);

        boolean botTurn = true;
        for (int i = 0; i < genes.size(); i++){
            int[] coordinate = genes.get(i);
            System.out.println("Ini pion: " + this.pawn);
            if (botTurn) {
                if (pawn.equals("X")) {
                    state.putX(coordinate[0], coordinate[1]);
                } else {
                    state.putO(coordinate[0], coordinate[1]);
                }
                botTurn = !botTurn;
                continue;
            }
            if (pawn.equals("X")) {
                state.putO(coordinate[0], coordinate[1]);
            } else {
                state.putX(coordinate[0], coordinate[1]);
            }
            botTurn = !botTurn;
        }

        return state;
    }

}

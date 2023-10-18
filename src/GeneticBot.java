import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Random;

public class GeneticBot extends Bot {
    String pawn;

    public GeneticBot (OutputFrameController gameBoard, Boolean isX) {
        super(gameBoard);
        if (isX) {
            this.pawn = "X";
        } else {
            this.pawn = "O";
        }
    }
    public int[] move(){
        List<int[]> finalChromosome = geneticAlgorithm();
        if (finalChromosome.size() == 0) {
            return null ;
        }
        int [] coordinate = finalChromosome.get(0);
        return coordinate;
    }

    public List<int[]> geneticAlgorithm(){
        List<List<int[]>> population = generatePopulation(this.getCurrentState().getWhiteSpots());
        List<Integer> fitnessValues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            population.addAll(generateOffsprings(population));
        }

        int max = 0;
        int indexMax = 0;
        for (int j = 0; j < population.size(); j++) {
            int value = fitnessFunction(this.getCurrentState(), population.get(j));
            fitnessValues.add(value);
            if (value > max) {
                max = value;
                indexMax = j;
            }
        }

        List<int[]> finalChromosome = new ArrayList<>(population.get(indexMax));

        return finalChromosome;
    }

    public static List<List<int[]>> orderedCrossover(List<int[]> parent1, List<int[]> parent2) {
        List<List<int[]>> children = new ArrayList<>();
        List<int[]> child1 = new ArrayList<>(Collections.nCopies(parent1.size(), null));
        List<int[]> child2 = new ArrayList<>(Collections.nCopies(parent1.size(), null));

        int start = parent1.size()/3 ;
        int end = parent1.size()*2/3;

        // Copy the selected range from parent1 to child1 and from parent2 to child2
        for (int i = start; i < end; i++) {
            int[] gene1 = parent1.get(i);
            int[] gene2 = parent2.get(i);
//            int[] gene1Part = Arrays.copyOfRange(gene1, start, end + 1);
//            int[] gene2Part = Arrays.copyOfRange(gene2, start, end + 1);
            child1.set(i, gene1);
            child2.set(i, gene2);
        }

        // Fill the rest of the genes from parent2 to child1 and from parent1 to child2 while avoiding duplicates
        for (int i = 0; i < parent1.size(); i++) {
            for (int j = 0; j < 2; j++) {
                int[] gene1 = parent2.get(i);
                int[] gene2 = parent1.get(i);
                if (!containsGene(child1, gene1)) {
                    int index = child1.indexOf(null);
                    child1.set(index, gene1);
                }
                if (!containsGene(child2, gene2)) {
                    int index = child2.indexOf(null);
                    child2.set(index, gene2);
                }

            }
        }
        children.add(child1);
        children.add(child2);

        return children;
    }

    public static boolean containsGene(List<int[]> chromosome, int[] geneTarget) {
        for (int[] gene : chromosome) {
            if (gene != null && gene[0] == geneTarget[0] && gene[1] == geneTarget[1]){
                return true;
            }
        }
        return false;
    }

    public void mutation(List<int[]> chromosome){
        if (chromosome.size() <=1) {
            return;
        }
        Random random = new Random();
        int randomValue = random.nextInt(chromosome.size()-1);
        int[] temp = chromosome.get(randomValue);
        chromosome.set(randomValue, chromosome.get(randomValue + 1));
        chromosome.set(randomValue+1, temp);
    }


    public List<List<int[]>> generatePopulation(List<int[]> whiteSpots){
        List<List<int[]>> population = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<int[]> randomizedList = new ArrayList<>(whiteSpots);
            Collections.shuffle(randomizedList);
            population.add(randomizedList);
        }
        return population;
    }

    public List<List<int[]>> generateOffsprings(List<List<int[]>> parentPopulation){
        List<List<int[]>> selectedParentChromosomes = selectParentChromosomes(parentPopulation);
        List<List<int[]>> offsprings = new ArrayList<>();
        int i = 0;
        while (i < parentPopulation.size()) {
            List<List<int[]>> children = orderedCrossover(selectedParentChromosomes.get(i), selectedParentChromosomes.get(i+1));
            Random random = new Random();

            int randomNumber = random.nextInt(10) + 1;
            if (randomNumber % 10 == 0) {
                mutation(children.get(0));
            }

            offsprings.add(children.get(0));
            offsprings.add(children.get(1));
            i += 2;
        }
        return offsprings;
    }

    public List<List<int[]>> selectParentChromosomes(List<List<int[]>> population){
        List<List<int[]>> selectedChromosomes = new ArrayList<>();
        List<Integer> fitnessValueList = new ArrayList<>();

        int sumValue = 0;
        for (int i = 0; i < population.size(); i++) {
            int value = fitnessFunction(this.getCurrentState(), population.get(i));
            fitnessValueList.add(value);
            sumValue += value;
        }

        for (int j = 0; j < population.size(); j++) {
            float currentValue = 0;
            int index = 0;
            Random random = new Random();
            float randomFloat = random.nextFloat();
            System.out.println("Float:" + randomFloat);

            while (randomFloat >= currentValue + (float)fitnessValueList.get(index)/sumValue) {
                currentValue += (float)fitnessValueList.get(index)/sumValue;
                System.out.print(currentValue + ",");
                index++;
            }
            selectedChromosomes.add(population.get(index));
        }

        return selectedChromosomes;
    }

    public int fitnessFunction(GameState initialState, List<int[]> chromosome){
        GameState finalState = simulate(initialState, chromosome);

        int value = 0;
        for (int i = 0; i < finalState.ROW; i++){
            for (int j = 0; j < finalState.COL; j++){
                if (finalState.node[i][j].equals(this.pawn)){
                    value++;
                }
            }
        }
        return value;
    }

    public GameState simulate(GameState currentState, List<int[]> chromosome){
        GameState state = new GameState(currentState);

        boolean botTurn = true;
        for (int i = 0; i < chromosome.size(); i++){
            int[] coordinate = chromosome.get(i);
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

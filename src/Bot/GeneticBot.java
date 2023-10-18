package Bot;

import Main.GameState;
import Main.OutputFrameController;
import Utility.Chromosome;

import java.util.*;

public class GeneticBot extends Bot {
    Boolean isX;

    public GeneticBot (OutputFrameController gameBoard, Boolean isX) {
        super(gameBoard);
        this.isX = isX;
    }
    public int[] getBestMove(){
        Chromosome finalChromosome = geneticAlgorithm();
        if (finalChromosome.getSize() == 0) {
            return null ;
        }
        int [] coordinate = finalChromosome.getGeneAt(0);
        return coordinate;
    }

    public Chromosome geneticAlgorithm(){
        List<Chromosome> population = generatePopulation(this.getCurrentState().getWhiteSpots());
        for (int i = 0; i < 200; i++) {
            population.addAll(generateOffsprings(population));
            // sorting
            Comparator<Chromosome> descendingComparator = (o1, o2) -> Integer.compare(o2.getFitnessValue(), o1.getFitnessValue());
            Collections.sort(population, descendingComparator);

            // pruning
            int elementsToRemove = population.size() / 2;

            for (int j = 0; j < elementsToRemove; j++) {
                int lastIndex = population.size() - 1;
                population.remove(lastIndex);
            }
        }

        int max = 0;
        int indexMax = 0;
        for (int j = 0; j < population.size(); j++) {
            int value = population.get(j).getFitnessValue();
            if (value > max) {
                max = value;
                indexMax = j;
            }
        }

        return population.get(indexMax);
    }


    public List<Chromosome> generatePopulation(List<int[]> whiteSpots){
        List<Chromosome> population = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Chromosome newChromosome = new Chromosome(new ArrayList<>(whiteSpots), isX);
            newChromosome.randomizeGeneSequence();
            population.add(newChromosome);
        }
        return population;
    }

    public List<Chromosome> generateOffsprings(List<Chromosome> parentPopulation){
        List<Chromosome> selectedParentChromosomes = selectParentChromosomes(parentPopulation);
        List<Chromosome> offsprings = new ArrayList<>();
        int i = 0;
        while (i < parentPopulation.size()) {
            List<Chromosome> children = orderedCrossover(selectedParentChromosomes.get(i), selectedParentChromosomes.get(i+1));
            Random random = new Random();

            int randomNumber = random.nextInt(10) + 1;
            if (randomNumber % 10 == 0) {
                children.get(0).mutation();
            }

            offsprings.add(children.get(0));
            offsprings.add(children.get(1));
            i += 2;
        }
        return offsprings;
    }

    public List<Chromosome> orderedCrossover(Chromosome parent1, Chromosome parent2) {
        List<Chromosome> children = new ArrayList<>();
        Chromosome child1 = new Chromosome(new ArrayList<>(Collections.nCopies(parent1.getSize(), null)), this.isX);
        Chromosome child2 = new Chromosome(new ArrayList<>(Collections.nCopies(parent1.getSize(), null)), this.isX);

        int start = parent1.getSize()/3 ;
        int end = parent1.getSize()*2/3;

        // Copy the selected range from parent1 to child1 and from parent2 to child2
        for (int i = start; i < end; i++) {
            int[] gene1 = parent1.getGeneAt(i);
            int[] gene2 = parent2.getGeneAt(i);

            child1.setGeneAt(i, gene1);
            child2.setGeneAt(i, gene2);
        }

        // Fill the rest of the genes from parent2 to child1 and from parent1 to child2 while avoiding duplicates
        for (int i = 0; i < parent1.getSize(); i++) {
            for (int j = 0; j < 2; j++) {
                int[] gene1 = parent2.getGeneAt(i);
                int[] gene2 = parent1.getGeneAt(i);
                if (!child1.containsGene(gene1)) {
                    int index = child1.getGenes().indexOf(null);
                    child1.setGeneAt(index, gene1);
                }
                if (!child2.containsGene(gene2)) {
                    int index = child2.getGenes().indexOf(null);
                    child2.setGeneAt(index, gene2);
                }

            }
        }
        children.add(child1);
        children.add(child2);

        return children;
    }

    public List<Chromosome> selectParentChromosomes(List<Chromosome> population){
        List<Chromosome> selectedChromosomes = new ArrayList<>();

        int sumValue = 0;
        for (int i = 0; i < population.size(); i++) {
            int value =  population.get(i).fitnessFunction(this.getCurrentState());
            sumValue += value;
        }

        for (int j = 0; j < population.size(); j++) {
            float currentValue = 0;
            int index = 0;
            Random random = new Random();
            float randomFloat = random.nextFloat();
            System.out.println("Float:" + randomFloat);
            System.out.println(population.get(index).getFitnessValue());
            while (randomFloat >= currentValue + (float)population.get(index).getFitnessValue()/sumValue) {
                currentValue += (float)population.get(index).getFitnessValue()/sumValue;
                System.out.print(currentValue + ",");
                index++;
            }
            selectedChromosomes.add(population.get(index));
        }

        return selectedChromosomes;
    }
}

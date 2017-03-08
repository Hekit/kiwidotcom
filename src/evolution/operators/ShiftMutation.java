package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;

/**
 * A mutation which swaps the values on different positions in a single individual.
 *
 * @author Martin Pilat
 */
public class ShiftMutation implements Operator {

    double mutationProbability;
    int lengthOfTrip;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probabilities
     * @param mutationProbability the probability of mutating an individual
     */

    public ShiftMutation(double mutationProbability, int lengthOfTrip) {
        this.mutationProbability = mutationProbability;
        this.lengthOfTrip = lengthOfTrip;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

            ArrayIndividual p1 = (ArrayIndividual) parents.get(i);
            ArrayIndividual o1 = (ArrayIndividual) p1.clone();

            if (rng.nextDouble() < mutationProbability) {
                int shift = rng.nextInt(lengthOfTrip);
                for (int j = 0; j < p1.length(); j++) {
                    Object v = p1.get(j);
                    o1.set((j + shift) % lengthOfTrip, v);
                }
            }
            offspring.add(o1);
        }
    }
}

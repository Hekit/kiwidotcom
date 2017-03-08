package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;

/**
 * Performs a one point crossover.
 *
 * A single point is selected randomly in the indivdiual and the tails of the parents (following the
 * selected point) are swapped between them.
 *
 * @author Martin Pilat
 */
public class Order1XOver implements Operator {

    double xOverProb = 0;

    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probability of crossover
     *
     * @param prob the probability of crossover
     */

    public Order1XOver(double prob) {
        xOverProb = prob;
    }

    private boolean contains(ArrayIndividual ind, int start, int end, int value) {
        for (int i = start; i < end; i++) {
            if ((Integer) ind.get(i) == value) {
                return true;
            }
        }
        return false;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();

        for (int i = 0; i < size / 2; i++) {
            ArrayIndividual p1 = (ArrayIndividual) parents.get(2 * i);
            ArrayIndividual p2 = (ArrayIndividual) parents.get(2 * i + 1);

            ArrayIndividual o1 = (ArrayIndividual) p1.clone();
            ArrayIndividual o2 = (ArrayIndividual) p2.clone();

            if (rng.nextDouble() < xOverProb) {

                int pt1 = rng.nextInt(p1.length());
                int pt2 = rng.nextInt(p1.length());
                int start = Math.min(pt1, pt2);
                int end = Math.max(pt1, pt2);

                int k = 0;
                for (int j = 0; j < start; j++) {
                    int candidate;
                    do {
                        candidate = (Integer) p2.get(k);
                        k++;
                    }
                    while (contains(p1, start, end, candidate));
                    o1.set(j, p2.get(k - 1));
                }
                for (int j = end; j < p1.length(); j++) {
                    int candidate;
                    do {
                        candidate = (Integer) p2.get(k);
                        k++;
                    }
                    while (contains(p1, start, end, candidate));
                    o1.set(j, p2.get(k - 1));
                }

                pt1 = rng.nextInt(p2.length());
                pt2 = rng.nextInt(p2.length());
                start = Math.min(pt1, pt2);
                end = Math.max(pt1, pt2);

                k = 0;
                for (int j = 0; j < start; j++) {
                    int candidate;
                    do {
                        candidate = (Integer) p1.get(k);
                        k++;
                    }
                    while (contains(p2, start, end, candidate));
                    o2.set(j, p1.get(k - 1));
                }
                for (int j = end; j < p2.length(); j++) {
                    int candidate;
                    do {
                        candidate = (Integer) p1.get(k);
                        k++;
                    }
                    while (contains(p2, start, end, candidate));
                    o2.set(j, p1.get(k - 1));
                }
            }

            offspring.add(o1);
            offspring.add(o2);
        }

    }
}
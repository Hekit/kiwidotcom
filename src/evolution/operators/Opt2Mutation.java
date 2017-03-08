package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;

/**
 * Created by David on 5. 3. 2017.
 */
public class Opt2Mutation implements Operator {

    double mutationProbability;
    Integer[][][] coords;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probabilities
     * @param mutationProbability the probability of mutating an individual
     */

    public Opt2Mutation(double mutationProbability, Integer[][][] coords) {
        this.mutationProbability = mutationProbability;
        this.coords = coords;
    }

    public void operate(Population parents, Population offspring) {
        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

            ArrayIndividual p1 = (ArrayIndividual) parents.get(i);
            ArrayIndividual o1 = (ArrayIndividual) p1.clone();

            int tripLength = p1.length();

            if (rng.nextDouble() < mutationProbability) {


                for (int j = 0; j < p1.length()-3; j++) {
                    Object v1 = o1.get(j);
                    Object v2 = o1.get((j+1));
                    Object v3 = o1.get((j+2));
                    Object v4 = o1.get((j+3));

                    if (      coords[(Integer) v1][(Integer) v2][j]
                            + coords[(Integer) v2][(Integer) v3][(j+1)]
                            + coords[(Integer) v3][(Integer) v4][(j+2)]
                            >
                              coords[(Integer) v1][(Integer) v3][j]
                            + coords[(Integer) v3][(Integer) v2][(j+1)]
                            + coords[(Integer) v2][(Integer) v4][(j+2)]
                            ) {
                        o1.set((j+1), v3);
                        o1.set((j+2), v2);
                    }
                }
            }
            offspring.add(o1);
        }
    }
}

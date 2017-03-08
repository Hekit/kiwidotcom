package evolution.operators;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.ArrayIndividual;

/**
 * Created by David on 5. 3. 2017.
 */
public class Opt3Mutation implements Operator {

    double mutationProbability;
    Integer[][][] coords;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probabilities
     * @param mutationProbability the probability of mutating an individual
     */

    public Opt3Mutation(double mutationProbability, Integer[][][] coords) {
        this.mutationProbability = mutationProbability;
        this.coords = coords;
    }

    public void operate(Population parents, Population offspring) {
        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

            ArrayIndividual p1 = (ArrayIndividual) parents.get(i);
            ArrayIndividual o1 = (ArrayIndividual) p1.clone();

            int tripLength = p1.length();

            //if (rng.nextDouble() < mutationProbability) {
            for (int j = 0; j < p1.length()-4; j++) {
                Object v1 = o1.get(j);
                Object v2 = o1.get((j + 1) % tripLength);
                Object v3 = o1.get((j + 2) % tripLength);
                Object v4 = o1.get((j + 3) % tripLength);
                Object v5 = o1.get((j + 4) % tripLength);

                int i1 = (int) v1;
                int i2 = (int) v2;
                int i3 = (int) v3;
                int i4 = (int) v4;
                int i5 = (int) v5;

                int[] vals = new int[6];

                int va234 = coords[i1][i2][j] + coords[i2][i3][(j + 1) % tripLength]
                        + coords[i3][i4][(j + 2) % tripLength] + coords[i4][i5][(j + 3) % tripLength];
                int va243 = coords[i1][i2][j] + coords[i2][i4][(j + 1) % tripLength]
                        + coords[i4][i3][(j + 2) % tripLength] + coords[i3][i5][(j + 3) % tripLength];
                int va342 = coords[i1][i3][j] + coords[i3][i4][(j + 1) % tripLength]
                        + coords[i4][i2][(j + 2) % tripLength] + coords[i2][i5][(j + 3) % tripLength];
                int va324 = coords[i1][i3][j] + coords[i3][i2][(j + 1) % tripLength]
                        + coords[i2][i4][(j + 2) % tripLength] + coords[i4][i5][(j + 3) % tripLength];
                int va423 = coords[i1][i4][j] + coords[i4][i2][(j + 1) % tripLength]
                        + coords[i2][i3][(j + 2) % tripLength] + coords[i3][i5][(j + 3) % tripLength];
                int va432 = coords[i1][i4][j] + coords[i4][i3][(j + 1) % tripLength]
                        + coords[i3][i2][(j + 2) % tripLength] + coords[i2][i5][(j + 3) % tripLength];
                vals[0] = va234;
                vals[1] = va243;
                vals[2] = va342;
                vals[3] = va324;
                vals[4] = va423;
                vals[5] = va432;

                int min = 0;
                for(int k = 1; k < vals.length; k++) {
                    if(vals[k] < vals[min]) {
                        min = k;
                    }
                }

                switch(min) {
                    case 0: // 2 3 4
                        break; // optional
                    case 1: // 2 4 3
                        o1.set((j + 1) % tripLength, v2);
                        o1.set((j + 2) % tripLength, v4);
                        o1.set((j + 3) % tripLength, v3);
                        break; // optional
                    case 2: // 3 4 2
                        o1.set((j + 1) % tripLength, v3);
                        o1.set((j + 2) % tripLength, v4);
                        o1.set((j + 3) % tripLength, v2);
                        break; // optional
                    case 3: // 3 2 4
                        o1.set((j + 1) % tripLength, v3);
                        o1.set((j + 2) % tripLength, v2);
                        o1.set((j + 3) % tripLength, v4);
                        break; // optional
                    case 4: // 4 2 3
                        o1.set((j + 1) % tripLength, v4);
                        o1.set((j + 2) % tripLength, v2);
                        o1.set((j + 3) % tripLength, v3);
                        break; // optional
                    case 5: // 4 3 2
                        o1.set((j + 1) % tripLength, v4);
                        o1.set((j + 1) % tripLength, v3);
                        o1.set((j + 2) % tripLength, v2);
                        break; // optional
                    default : // Optional
                        // Statements
                }
            }
            //}
            offspring.add(o1);
        }
    }
}

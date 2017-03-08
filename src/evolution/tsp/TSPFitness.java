package evolution.tsp;

import evolution.FitnessFunction;
import evolution.individuals.Individual;
import evolution.individuals.IntegerIndividual;

public class TSPFitness implements FitnessFunction {

    private static final long serialVersionUID = -1141681050507988075L;
    Integer[][][] coords;
    Integer[] from;
    Integer[] to;
    long flightSum;

    public TSPFitness(Integer[][][] coords, Integer[] from, Integer[] to, long flightSum) {
        this.coords = coords;
        this.from = from;
        this.to = to;
        this.flightSum = flightSum;
    }

    @Override
    public double evaluate(Individual ind) {

        IntegerIndividual aSubject = (IntegerIndividual) ind;

        double price = 0;

        for (int i = 0; i < aSubject.length() - 1; i++) {

            int start = (Integer) aSubject.get(i);
            int end = (Integer) aSubject.get(i + 1);

            price += coords[start][end][i+1];
        }

        //price += coords[(Integer) aSubject.get(aSubject.length() - 1)][(Integer) aSubject.get(0)][aSubject.length()-1];
        price += from[(Integer) aSubject.get(0)];
        price += to[(Integer) aSubject.get(aSubject.length()-1)];

        aSubject.setObjectiveValue(price);

        return 1.0 / price;
        //return flightSum / price;
    }
}

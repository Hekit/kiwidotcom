package evolution.tsp;

import evolution.*;
import evolution.individuals.Individual;
import evolution.individuals.IntegerIndividual;
import evolution.operators.*;
import evolution.selectors.RouletteWheelSelector;
import evolution.selectors.TournamentSelector;

import java.io.*;
import java.util.*;

public class TravellingSalesman {

    static int maxGen;
    static int popSize;
    static String logFilePrefix;
    static int repeats;
    static String bestPrefix;
    static double eliteSize;
    static double xoverProb;
    static double mutProb;
    static double mutProbPerBit;
    static String enableDetailsLog;
    static String outputDirectory;
    static String objectiveFilePrefix;
    static String objectiveStatsFile;
    static String fitnessFilePrefix;
    static String fitnessStatsFile;
    static String detailsLogPrefix;
    static Properties prop;


    static Integer[][][] coords;
    static HashMap<String, Integer> nameToInt;
    static String[] intToString;
    static int inputSize;
    static String startAirport;
    static Integer[] toStart;
    static Integer[] fromStart;
    static long flightSum;

    public static void main(String[] args) {

        prop = new Properties();
        try {
            InputStream propIn = new FileInputStream("properties/ga-tsp.properties");
            prop.load(propIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        maxGen = Integer.parseInt(prop.getProperty("ea.maxGenerations", "20"));
        popSize = Integer.parseInt(prop.getProperty("ea.popSize", "30"));
        xoverProb = Double.parseDouble(prop.getProperty("ea.xoverProb", "0.8"));
        mutProb = Double.parseDouble(prop.getProperty("ea.mutProb", "0.05"));
        mutProbPerBit = Double.parseDouble(prop.getProperty("ea.mutProbPerBit", "0.04"));
        eliteSize = Double.parseDouble(prop.getProperty("ea.eliteSize", "0.1"));

        inputSize = Integer.parseInt(prop.getProperty("prob.size"));
        String inputFile = "./resources/data_" + Integer.toString(inputSize) + ".txt";

        repeats = Integer.parseInt(prop.getProperty("xset.repeats", "10"));

        int[] arr = new int[12];
        arr[0] = 5; arr[1] = 10; arr[2] = 15; arr[3] = 20; arr[4] = 30; arr[5] = 40;
        arr[6] = 50; arr[7] = 60; arr[8] = 70; arr[9] = 100; arr[10] = 200; arr[11] = 300;

        //arr = new int[1]; arr[0] = 300;

        for (int si : arr) {
            long startTime = System.currentTimeMillis();
            System.out.println("Running for size: " + si);
            inputSize = si;
            inputFile = "./resources/data_" + si + ".txt";
            loadData(inputFile, inputSize - 1);
            long readInputTime = System.currentTimeMillis() - startTime;

            List<Individual> bestInds = new ArrayList<Individual>();

            for (int i = 0; i < repeats; i++) {
                Individual best = run(i);
                bestInds.add(best);
            }

            for (int i = 0; i < bestInds.size(); i++) {
                System.out.println("run " + i + ": best objective=" + bestInds.get(i).getObjectiveValue());
            }
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("Reading input: " + readInputTime);
            System.out.println("Execution time (average): " + (estimatedTime / repeats));
            System.out.println();
        }
    }

    private static Individual run(int number) {

        RandomNumberGenerator.getInstance().reseed(number);

        try {

            IntegerIndividual sampleIndividual = new IntegerIndividual(inputSize-1, 0, inputSize-1);

            Population pop = new Population();
            pop.setSampleIndividual(sampleIndividual);
            pop.setPopulationSize(popSize);

            EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm();
            ea.setFitnessFunction(new TSPFitness(coords, fromStart, toStart, flightSum));

            ea.addOperator(new SwappingMutationOperator(mutProb, mutProbPerBit));
            //ea.addOperator(new ShiftMutation(mutProb, inputSize));
            //ea.addOperator(new Opt2Mutation(mutProb, coords));
            //ea.addOperator(new Opt3Mutation(0.2, coords));

            ea.addOperator(new Order1XOver(xoverProb));

            //ea.addEnvironmentalSelector(new TournamentSelector());
            ea.addEnvironmentalSelector(new RouletteWheelSelector());

            ea.setElite(eliteSize);

            pop.createRandomInitialPopulation();

            //ensure all the individuals represent permutation
            for (int i = 0; i < pop.getPopulationSize(); i++) {

                ArrayList<Integer> rand = new ArrayList<Integer>();

                for (int j = 0; j < inputSize-1; j++) {
                    rand.add(j);
                }

                Collections.shuffle(rand, RandomNumberGenerator.getInstance().getRandom());
                IntegerIndividual tmp = (IntegerIndividual) pop.get(i);
                for (int j = 0; j < tmp.length(); j++) {
                    tmp.set(j, rand.get(j));
                }

            }

            for (int i = 0; i < maxGen; i++) {
                ea.evolve(pop);

                IntegerIndividual ch = (IntegerIndividual) pop.getSortedIndividuals().get(0);

                //check whether each city is used only once
                boolean[] used = new boolean[ch.length()];
                for (int g : ch.toIntArray()) {
                    if (used[g]) {
                        throw new RuntimeException("The city with id " + g + " found multiple times");
                    }
                    used[g] = true;
                }

                Double diff = pop.getSortedIndividuals().get(0).getObjectiveValue();
                System.out.println("Generation " + i + ": " + diff);

            }

            IntegerIndividual bestInd = (IntegerIndividual) pop.getSortedIndividuals().get(0);

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("./output/run_" + number + "_size_" + inputSize));

            out.write(Integer.toString((int) bestInd.getObjectiveValue()));
            out.write("\n");

            int idx = 0;
            //while((Integer) bestInd.get(idx) != 0) {idx++;}
            out.write(startAirport + " " + intToString[(Integer) bestInd.get(0)] + " " + 0 + " " + fromStart[(Integer) bestInd.get(0)]);
            out.write("\n");
            for (int i = 0; i < bestInd.length()-1; i++) {
                out.write(intToString[(Integer) bestInd.get((idx + i) % inputSize)] + " "
                        + intToString[(Integer) bestInd.get((idx + i + 1) % inputSize)] + " "
                        + (i+1) + " "
                        + coords[(Integer) bestInd.get((idx + i) % inputSize)][(Integer) bestInd.get((idx + i + 1) % inputSize)][i+1]);
                out.write("\n");
            }
            out.write(intToString[(Integer) bestInd.get(bestInd.length()-1)] + " " + startAirport  + " "
                    + (inputSize-1) + " " + toStart[(Integer) bestInd.get(bestInd.length()-1)]);
            out.close();

            return bestInd;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void loadData(String inputFile, int size) {
        coords = new Integer[size][size][size];
        flightSum = 0;
        for(int i=0; i < size; i++)
            for(int j=0; j < size; j++)
                Arrays.fill( coords[i][j], Integer.MAX_VALUE);
        nameToInt = new HashMap<>();
        intToString = new String[size];
        toStart = new Integer[size];
        fromStart = new Integer[size];
        Arrays.fill( toStart, Integer.MAX_VALUE);
        Arrays.fill( fromStart, Integer.MAX_VALUE);
        int idx = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(inputFile));
            startAirport = in.readLine();
            String line;
            nameToInt.put(startAirport, -1);
            while ((line = in.readLine()) != null) {
                int lastIdx = line.lastIndexOf(' ');
                String start = line.substring(0,3);
                String end = line.substring(4,7);
                int day = Integer.parseInt(line.substring(8, lastIdx));
                int price = Integer.parseInt(line.substring(lastIdx + 1));
                int c0;
                int c1;
                if (!nameToInt.containsKey(start)) {
                    nameToInt.put(start, idx);
                    intToString[idx] = start;
                    c0 = idx;
                    idx++;
                } else c0 = nameToInt.get(start);
                if (!nameToInt.containsKey(end)) {
                    nameToInt.put(end, idx);
                    intToString[idx] = end;
                    c1 = idx;
                    idx++;
                } else c1 = nameToInt.get(end);

                if (start.equals(startAirport)) {
                    if (day == 0) {
                        fromStart[c1] = price;
                    }
                }
                else if (end.equals(startAirport)) {
                    if (day == size) {
                        toStart[c0] = price;
                    }
                }
                else if (day != size)
                    coords[c0][c1][day] = price;
                flightSum += price;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

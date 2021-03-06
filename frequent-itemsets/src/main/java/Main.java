package main.java;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Main class.
 *
 * Test class used to output the results of the implemented apriori algoritm.
 *
 */
public class Main {


    /** entry point */
    public static void main(String[] args) {

        // default args
        String fileName = "T10I4D100K.dat";


        /** tresholds as percentages
         * beware that, for example:
         * 500 frequent singletons genrate 124750 candidate doubletons
         */
        double[] stepThresholds = {0.01}; // 1% (step1) and 1% (step n>1) of all the baskets

        // default minimum confidence
        double minConfidence = 0.5;

        // if wrong args, exit
        if(args.length>0 && args.length != 3) {
            System.err.println("Usage: <filename> <threshold> <min-confidence>");
            System.exit(-1);
        }

        // if right args
        if(args.length==3) {
            fileName = args[0];
            stepThresholds[0] = Double.parseDouble(args[1]);
            minConfidence = Double.parseDouble(args[2]);
        }

        // get the dataset
        File inputFile = new File("input/"+fileName);
        log("ANALYSING FILE", inputFile);

        // start a timer
        long startTime = System.currentTimeMillis();

        AprioriAlgorithm apriori = new AprioriAlgorithm(stepThresholds, minConfidence, inputFile);

        // subproblem 1: find the frequent itemsets
        List<int []> frequentItemsets = apriori.findFrequentItemsets();

        // stop the timer
        long endTime   = System.currentTimeMillis();
        double totalTime = (endTime - startTime)/ 1000;

        // print the frequent itemsets
        logList("FREQUENT ITEMSETS FOUND:", frequentItemsets);
        log("TIME (in sec): ", totalTime);

        // subproblem 2: find associations
        List associations = apriori.findAssociations(frequentItemsets);

    }


    /**
     * utility log method
     *
     * @param info
     * @param toLog
     */
    public static void log(String info, Object toLog) {
        System.out.println(info);
        System.out.println(toLog);
    }

    /**
     * utility logList method
     *
     * @param info
     * @param toLog
     */
    public static void logList(String info, List<int[]> toLog) {
        System.out.println(info);
        for (int[] item: toLog) {
            System.out.print(Arrays.toString(item));
        }
        System.out.println();
    }
}

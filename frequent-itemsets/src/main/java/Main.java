import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // fileName = "T10I4D100K.dat";

        /** tresholds as percentages
         * beware that, for example:
         * 500 frequent singletons genrate 124750 candidate doubletons
         */
        double[] stepThresholds = {0.03, 0.008}; // 3% (step1) and 0.8% (step n>1) of all the baskets
        // double[] stepThresholds = {0.02, 0.5};

        // if wrong args, exit
        if(args.length>0 && args.length != 2) {
            System.err.println("Usage: <filename> <threshold>");
            System.exit(-1);
        }

        // if right args
        if(args.length==2) {
            fileName = args[0];
            stepThresholds[0] = Double.parseDouble(args[1]);
        }

        // get the dataset
        File inputFile = new File("input/"+fileName);
        log("ANALYSING FILE", inputFile);

        // subproblem 1: find the frequent itemsets
        List<int []> frequentItemsets = new AprioriAlgorithm(stepThresholds, inputFile).findFrequentItemsets();

        // print the frequent itemsets
        logList("FREQUENT ITEMSETS FOUND:", frequentItemsets);

        // subproblem 2: find associations
        List associations = AprioriAlgorithm.findAssosiation(frequentItemsets);

        // print the associations
        log("ASSOCIATIONS FOUND:", associations);
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
     * utility log method
     *
     * @param info
     * @param toLog
     */
    public static void logList(String info, List<int[]> toLog) {
        System.out.println(info);
        for (int[] item: toLog) {
            String s = "";
            for(int i=0; i< item.length; i++) {
                s += " "+item[i];
            }
            System.out.print(s + " | ");
        }
        System.out.println();


    }
}

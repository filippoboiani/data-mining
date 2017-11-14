import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by filippoboiani on 13/11/2017.
 */
public class AprioriAlgorithm {


    /** the threshold to determine a frequent itemset */
    private int supportThreshold;
    private double[] stepThresholds;

    /** number of items in the dataset */
    private int numberOfItems;

    /** number of transactions in the dataset */
    private int numberOfBaskets;

    /** cardinality of itemsets */
    private int kItemsets;

    /**
     * the map of provided baskets and items:
     * - key: the basket id
     * - value: the list of items of that basket
     * */
    private List<int[]> dataset;

    /**
     * set of real frequent items and set of candidates
     * - key: int[] the itemset
     * - value: Integer the support
     * */
    private List<int[]> frequentItemsets, prevFrequentItemsets,candidateItemsets;


    /**
     * AprioriAlgorith constructor
     *
     * @param s
     * @param fileToAnalyse
     */
    public AprioriAlgorithm(double[] s, File fileToAnalyse) {

        // set attributes
        this.numberOfBaskets = 0;
        this.numberOfItems = 0;
        this.candidateItemsets = new ArrayList<>();
        this.frequentItemsets = new ArrayList<>();
        this.prevFrequentItemsets = new ArrayList<>();

        // prepare the file
        this.prepareFile(fileToAnalyse);

        // set the threshold
        this.supportThreshold = (int) (numberOfBaskets*s[0]);
        this.stepThresholds = s;

        // TODO: remove this
        log("Items: ", this.numberOfItems);
        log("Baskets: ", this.numberOfBaskets);
        log("Threshold: ", this.supportThreshold);

        // compute the first step: frequent singletons
        this.generateSingletons();
        this.computeFrequentSingletons();

        logList("Frequent singletons ("+this.frequentItemsets.size()+"): ",this.frequentItemsets);

    }


    /**
     * prepareFile function.
     *
     * The file has to be as follow:
     * - One line per basket
     * - the items in one basket are integers
     * - the items are separated by a space
     *
     * @param fileToAnalyse
     */
    private void prepareFile(File fileToAnalyse) {

        BufferedReader br = null;
        dataset = new ArrayList();

        try {
            br = new BufferedReader(new FileReader(fileToAnalyse));

            while(br.ready()) {

                // split the line to get the basket, and increase the counter
                int[] basket = Arrays
                        .asList(br.readLine().split("\\s"))
                        .stream()
                        .mapToInt(Integer::parseInt)
                        .toArray();

                numberOfBaskets++;
                dataset.add(basket);

                for(int item: basket) {

                    // set the number of items
                    if(numberOfItems<item+1) numberOfItems = item+1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the buffer
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateSingletons() {

        kItemsets = 1;
        // iterate over all the items
        for(int i=0; i<numberOfItems; i++) {
            int[] singleton = {i};
            // put the item in the list of items
            candidateItemsets.add(singleton);
        }
    }

    private void computeFrequentSingletons() {

        // only the support for the singleton will be the full set of items
        // i.e. candidate index == support vector index
        int[] support = new int[numberOfItems];

        Iterator<int[]> basketsIterator = dataset.iterator();

        while(basketsIterator.hasNext()) {

            int[] basket = basketsIterator.next();

            for(int item: basket) {
                support[item]++;
            }
        }

        for(int candidate=0; candidate<support.length; candidate++) {
            // if the support is greater than the threshold
            if (support[candidate] >= supportThreshold) {
                // put the itemset int the set of frequent itemset (the put will overwrite existing values)
                frequentItemsets.add(candidateItemsets.get(candidate));
            }
        }
    }

    public List<int[]> findFrequentItemsets() {

        // supports vector
        int[] support;

        for(kItemsets=2; frequentItemsets.size() != 0; kItemsets++) {

            // set a new threshold id the user has specified more than one
            if(this.stepThresholds.length > 1) {
                double t = stepThresholds[1];
                supportThreshold = (int) (numberOfBaskets*t);
            }

            // generate the set of candidates
            candidateItemsets = aprioriGen(this.frequentItemsets);

            // instanciate the supports vector
            support = new int[candidateItemsets.size()];

            // iterate over the dataset
            Iterator<int[]> basketsIterator = dataset.iterator();

            while(basketsIterator.hasNext()) {

                int[] basket = basketsIterator.next();

                // create a boolean array where the basket[item] = true
                // means that the item is included in the basket
                // TODO: improve this by creating a fixed matrix of booleans

                boolean[] basketItems = Combination.convertToBoolean(basket, numberOfItems);

                // check if any of the candidates is contained in the basket
                for(int c=0; c<candidateItemsets.size(); c++) {
                    int[] candidateItemset = candidateItemsets.get(c);
                    boolean contained = true;

                    for(int item: candidateItemset){
                        if(!basketItems[item]) {
                            contained = false;
                            break;
                        }
                    }

                    // if the itemset is contained in the basket increase its support
                    if(contained) support[c]++;
                }
            }

            prevFrequentItemsets = frequentItemsets;
            frequentItemsets = new ArrayList<>();

            // filter the candidates
            for(int candidate=0; candidate<support.length; candidate++) {
                // if the support is greater than the threshold
                if (support[candidate] >= supportThreshold) {
                    // put the itemset int the set of frequent itemset (the put will overwrite existing values)
                    frequentItemsets.add(candidateItemsets.get(candidate));
                }
            }

            logList("Frequent itemsets of size "+kItemsets+" ("+this.frequentItemsets.size()+"):" ,this.frequentItemsets);
        }

        return prevFrequentItemsets;
    }

    /**
     * @param oldFrequentItemsets
     * @return
     */
    private List<int[]> aprioriGen(List<int[]> oldFrequentItemsets) {

        // combine
        return Combination.combine(oldFrequentItemsets);

        // prune

    }

    public static List findAssosiation(List frequentItemsets) {
        return null;
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

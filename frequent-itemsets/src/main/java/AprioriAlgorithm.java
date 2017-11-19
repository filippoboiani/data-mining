package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Apriori class.
 *
 * This class represent the Apriori algorithm seen in the paper.
 * It offers two method:
 * - findFrequentItemsets (in the provided input file)
 * - findAssociations (within the frequent itemsets)
 */
public class AprioriAlgorithm {

    /** the threshold to determine a frequent itemset */
    private int supportThreshold;
    private double[] stepThresholds;

    /** the minimum confidence for association rules */
    private final double minConfidence;

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
    private List<int[]> kFrequentItemsets, totalFrequentItemsets, candidateItemsets;
    private Map<Integer, Integer> frequentItemsetSupports;

    /**
     * AprioriAlgorith constructor
     *
     * @param s
     * @param fileToAnalyse
     */
    public AprioriAlgorithm(double[] s, double minConfidence, File fileToAnalyse) {

        // set attributes
        this.numberOfBaskets = 0;
        this.numberOfItems = 0;
        this.candidateItemsets = new ArrayList<>();
        this.kFrequentItemsets = new ArrayList<>();
        this.totalFrequentItemsets = new ArrayList<>();
        this.frequentItemsetSupports = new HashMap<>();

        // prepare the file
        this.prepareFile(fileToAnalyse);

        // set the threshold
        this.supportThreshold = (int) (numberOfBaskets*s[0]);
        this.stepThresholds = s;
        this.minConfidence = minConfidence;

        // print out information
        log("Items: ", this.numberOfItems);
        log("Baskets: ", this.numberOfBaskets);
        log("Threshold: ", this.supportThreshold);
        log("Min Confidence: ", this.minConfidence);
        // compute the first step: frequent singletons
        this.generateSingletons();
        this.computeFrequentSingletons();

        logList("Frequent singletons ("+this.kFrequentItemsets.size()+"): ",this.kFrequentItemsets);

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

    /** generate a list of candidate singletons */
    private void generateSingletons() {

        kItemsets = 1;
        // iterate over all the items
        for(int i=0; i<numberOfItems; i++) {
            int[] singleton = {i};
            // put the item in the list of items
            candidateItemsets.add(singleton);
        }
    }

    /** get the frequent singletons */
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

        // iterate over the candidates to filter them
        filterCandidates(support);
    }

    /**
     * find the frequent itemsets.
     *
     * The function follows the algorithm provided in the paper.
     *
     * @return list of frequent itemsets
     */
    public List<int[]> findFrequentItemsets() {

        // supports vector (used to count the frequences)
        int[] support;

        for(this.kItemsets=2; this.kFrequentItemsets.size() != 0; this.kItemsets++) {

            // set a new threshold id (if the user has specified more than one)
            if(this.stepThresholds.length > 1) {
                double t = stepThresholds[1];
                this.supportThreshold = (int) (this.numberOfBaskets*t);
            }

            // generate the set of candidates
            this.candidateItemsets = aprioriGen(this.kFrequentItemsets);

            // instantiate the supports vector
            support = new int[this.candidateItemsets.size()];

            // iterate over the dataset
            Iterator<int[]> basketsIterator = this.dataset.iterator();

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

            totalFrequentItemsets.addAll(this.kFrequentItemsets);
            this.kFrequentItemsets = new ArrayList<>();

            // filter the candidates
            filterCandidates(support);

            logList("Frequent itemsets of size "+kItemsets+" ("+this.kFrequentItemsets.size()+"):" ,this.kFrequentItemsets);
        }
        return totalFrequentItemsets;
    }


    /**
     * receives an array of supports (each support corresopnds to the itemset with the same position
     * in the list of candidatesItemsets) and returns only the frequent itemsets. Therefore it represent
     * the filter step in the Apriori algorithm.
     *
     * @param support
     */
    private void filterCandidates(int[] support) {
        // iterate over the candidates to filter them
        for(int candidate=0; candidate<support.length; candidate++) {
            // if the support is greater than the threshold
            if (support[candidate] >= this.supportThreshold) {
                // put the itemset int the set of frequent itemsets (the put will overwrite existing values)
                int[] c = this.candidateItemsets.get(candidate);
                this.kFrequentItemsets.add(c);
                this.frequentItemsetSupports.put(Arrays.hashCode(c), support[candidate]);
            }
        }
    }

    /**
     * aprioriGen
     *
     * generates the candidates
     *
     * @param oldFrequentItemsets
     * @return list of candidates
     */
    private List<int[]> aprioriGen(List<int[]> oldFrequentItemsets) {

        // combine and prune
        return Combination.combine(oldFrequentItemsets);
    }

    /**
     * Find associations, implementation of the fast algorithm found in the paper
     *
     * @param frequentItemsets
     * @return
     */
    public List findAssociations(List<int[]> frequentItemsets) {
        for (int[] itemset: frequentItemsets) {

            if(itemset.length >= 2) {
                List<int[]> singleConsequents = Combination.combinations(itemset, 1);
                int itemsetSupport = this.frequentItemsetSupports.get(Arrays.hashCode(itemset));
                int diffSupport;
                double confidence;
                List<int[]> toRemove = new ArrayList<>();
                for(int[] consequent: singleConsequents) {
                    int[] diff = Combination.setDifference(consequent, itemset);

                    diffSupport = this.frequentItemsetSupports.get(Arrays.hashCode(diff));

                    confidence = new Double(itemsetSupport) / new Double(diffSupport);

                    if(confidence >= this.minConfidence) {
                        System.out.println("RULE: "+Arrays.toString(diff)+" => "+Arrays.toString(consequent)+" with conf = "+confidence+" and support= "+itemsetSupport);
                    } else {
                        // delete the rule from the set
                        toRemove.add(consequent);
                    }
                }
                // remove items
                for(int[] torem: toRemove) {
                    singleConsequents.remove(torem);
                }
                this.findAssociations(itemset, singleConsequents);
            }
        }
        return null;
    }


    /**
     * findAssociations
     *
     * inner recursive private method that takes advantenge of the combine function.
     * @param itemset
     * @param subset
     */
    private void findAssociations(int[] itemset, List<int[]> subset) {

        int itemsetSupport = this.frequentItemsetSupports.get(Arrays.hashCode(itemset));
        int diffSupport;
        double confidence;
        List<int[]> toRemove = new ArrayList<>();

        if(subset.size() > 0 && itemset.length > subset.get(0).length+1) {

            List<int[]> consequents = Combination.combine(subset);
            for(int[] consequent: consequents) {
                int[] diff = Combination.setDifference(consequent, itemset);

                diffSupport = this.frequentItemsetSupports.get(Arrays.hashCode(diff));

                confidence = new Double(itemsetSupport) / new Double(diffSupport);

                if(confidence >= this.minConfidence) {
                    System.out.println("RULE: "+Arrays.toString(diff)+" => "+Arrays.toString(consequent)+" with conf = "+confidence+" and support= "+itemsetSupport);
                } else {
                    // delete the rule from the set
                    toRemove.add(consequent);
                }
            }
            // remove items
            for(int[] torem: toRemove) {
                consequents.remove(torem);
            }
            findAssociations(itemset, consequents);
        }
    }


    /**
     * utility log method
     *
     * @param info
     * @param toLog
     */
    private static void log(String info, Object toLog) {
        System.out.println(info);
        System.out.println(toLog);
    }

    /**
     * utility log method
     *
     * @param info
     * @param toLog
     */
    private static void logList(String info, List<int[]> toLog) {
        System.out.println(info);
        for (int[] item: toLog) {
            System.out.print(Arrays.toString(item));
        }
        System.out.println();

    }
}

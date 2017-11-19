package main.java;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Combination
 *
 * This utility class computes the combination of item in a set. A combination is intended as a selection K of
 * items from a collection of lenght N (the inputSet)
 *
 *
 */
public final class Combination {

    public Combination() {}


    /**
     * Computation similar to cartesian products between sets.
     * This method receives a list of frequent k-itemsets and computes the
     * candidate (k+1)-itemsets.
     *
     * @param inputSet
     * @return
     */
    public static List<int[]> combine(List<int[]> inputSet) {

        List<int[]> result = new ArrayList<>();

        for(int i=0; i<inputSet.size(); i++) {
            int[] itemsetA = inputSet.get(i);
            int itemsetSize = itemsetA.length;

            for (int j=i+1; j<inputSet.size(); j++) {
                int[] itemsetB = inputSet.get(j);

                for (int a=0; a<itemsetSize; a++) {
                    if (a!=itemsetSize-1 && itemsetA[a] != itemsetB[a]) {
                        break;
                    } else {
                        if (a==itemsetSize-1 && itemsetA[a] != itemsetB[a]) {

                            int[] combination = Arrays.copyOf(itemsetA, itemsetSize+1);
                            combination[itemsetSize] = itemsetB[a];
                            result.add(combination);
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     *
     * Computes the differnce between 2 arrays
     * @param a
     * @param b
     * @return
     */
    public static int[] setDifference(int[] a, int[] b) {
        int[] setA; // the small one
        int[] setB; // the big one
        int[] diff;

        if (a.length > b.length) {
            setB = a;
            setA = b;
            diff = new int[a.length - b.length];
        } else {
            setB = b;
            setA = a;
            diff = new int[b.length - a.length];
        }

        int count;
        int index = 0;
        for (int i = 0; i < setB.length; i++) {
            count = 0;
            for (int j = 0; j < setA.length; j++) {
                if ((setB[i] != setA[j])) {
                    count++;

                }
                if (count == setA.length) {
                    diff[index] = setB[i];
                    index++;
                }
            }
        }

        return diff;
    }

    /**
     * generates the combination n over k of a given array of integers of size n.
     * @param inputSet
     * @param k
     * @return
     */
    public static List<int[]> combinations(int[] inputSet, int k) {

        List<int[]> subsets = new ArrayList<>();

        // here we'll keep indices
        int[] s = new int[k];

        // pointing to elements in input array

        if(k <=inputSet.length) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < k - 1; i++) ;
            subsets.add(getSubset(inputSet, s));
            for (; ; ) {
                int i;
                // find position of item that can be incremented
                for (i = k - 1; i >= 0 && s[i] == inputSet.length - k + i; i--) ;
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < k; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(inputSet, s));
            }
        }

        return subsets;
    }


    /**
     * generate actual subset by index sequence
     * @param input
     * @param subset
     * @return
     */
    private static int[] getSubset(int[] input, int[] subset) {
        int[] result = new int[subset.length];
        for (int i = 0; i < subset.length; i++)
            result[i] = input[subset[i]];
        return result;
    }


    /**
     * Converts a basket into an array of booleans where the true values corresponds to the
     * items the basket contains
     *
     * @param basket
     * @param numberOfItems
     * @return
     */
    public static boolean[] convertToBoolean(int[] basket, int numberOfItems) {
        boolean[] result = new boolean[numberOfItems];

        for(int item: basket){ result[item] = true; }

        return result;
    }
}

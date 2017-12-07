package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LSH {

    private Integer[][] signature = null;
    private Map<Integer, ArrayList<Integer>> buckets = new HashMap();


    /**
     * Constructor
     *
     * It receives a matrix of signatures: each column represent a document
     * @param minHashSignature
     */
    public LSH(Integer[][] minHashSignature) {
        this.signature = minHashSignature;
    }

    /**
     *
     * findCandidates method
     *
     * This method applies the algorithm seen during the lectures to create buckets and find similarities
     * @param treshold
     * @return
     */
    public ArrayList<String> findCandidates(double treshold) {

        // I assume I create 100 hashFunction and therefore 100 rows
        // I have 20 bands of 5 rows each

        int b = 20; // bands
        int r = 5; // rows per band
        int numberOfElements = signature[0].length;


        // For each band
        for (int i = 0; i < signature.length-r; i=i+r) {
            // For each column (document) inside the band
            for (int j = 0; j < numberOfElements; j++) {
                String columnString = "";
                // Create a string which is a concatenation of all the signature in that band
                for (int k = i; k < i+r; k++) {
                    columnString += signature[k][j];
                }
                // put the document in that bucket, remember to add 1 since we always saved them starting from 1
                int hashedColumn = columnString.hashCode();
                if (buckets.containsKey(hashedColumn)) {
                    ArrayList<Integer> documentsInThisBucket = buckets.get(hashedColumn);
                    if (!documentsInThisBucket.contains(j+1)) {
                        documentsInThisBucket.add(j+1);
                        buckets.replace(hashedColumn, documentsInThisBucket);
                    }
                } else {
                    ArrayList<Integer> documentsInThisBucket = new ArrayList<>();
                    documentsInThisBucket.add(j+1);
                    buckets.put(hashedColumn, documentsInThisBucket);
                }
            }
        }

        // look inside the buckets to check if some of the documents ended up in the same.
        ArrayList<String> candidatesCompare = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Integer>> bucket: buckets.entrySet()) {
            if(bucket.getValue().size()>1) {
                String compares = "";

                for (int i = 0; i < bucket.getValue().size(); i++) {
                    if (i != 0) {
                        compares += "-";
                    }
                    compares += bucket.getValue().get(i);
                }
                if (!candidatesCompare.contains(compares)) {
                    candidatesCompare.add(compares);
                }
            }
        }
        return candidatesCompare;
    }

    // utility method to get the lenght of the matrix
    public int getSignatureLenght() {
        return this.signature.length;
    }
}

package com.company;


import java.math.BigInteger;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class Shingling that
 *  - constructs k–shingles of a given length k (e.g. 10) from a given document,
 *  - computes a hash value for each unique shingle,
 *  - and represents the document in the form of an ordered set of its hashed k-shingles.
 */
public class Shingling {


    /**
     * byWord: boolean flag use to create shingles by word or by characters
     */
    private boolean byWord;

    /**
     *  k: the size of the shingles (in chars or words, depending on the byWord flag)
     *  numberOfDocuments: a counter of docuemnts that have been ingested by the class
     */
    private int k, numberOfDocuments;

    /**
     * shingles: a map of shingles
     *
     * key: shingle hash code
     * values: document ids having that shingle in their sets
     *
     */
    private Map<Integer, ArrayList<Integer>> shingles;


    /**
     * signatureMatrix: matrix of signatures that will be used by the LSH object
     */
    private Integer[][] signatureMatrix;


    /**
     * Shingling constructor
     *
     * @param k
     * @param byWord
     */
    public Shingling(Integer k, boolean byWord) {
        // Inizialize the shingle with a map
        // The map is shared by all the documents
        // Set the k that will be in common among all the documents
        this.k = k;
        this.byWord = byWord;
        this.shingles = new HashMap<>();
        this.numberOfDocuments = 0;
    }

    /**
     * shingleADocument function
     *
     * @param fullText
     * */
    public void shingleADocument(String fullText) {

        // Increase the number of documents
        this.numberOfDocuments++;

        // if the shingling has to be done by word
        if (this.byWord) {
            System.out.println("Shingling by words...");
            ArrayList<String> words = new ArrayList<String>();
            BreakIterator breakIterator = BreakIterator.getWordInstance();
            breakIterator.setText(fullText);
            int lastIndex = breakIterator.first();
            while (BreakIterator.DONE != lastIndex) {
                int firstIndex = lastIndex;
                lastIndex = breakIterator.next();
                if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(fullText.charAt(firstIndex))) {
                    words.add(fullText.substring(firstIndex, lastIndex));
                }
            }
            System.out.println("Word count: "+words.size());

            for (int i = 0; i < words.size()-this.k; i++) {

                String kWords = "";

                // get the next k words and concatenate them
                for(int j=i; j<i+this.k; j++) {
                    kWords += words.get(j);
                }

                // get the current shingle and hash it
                Integer currentShingle = kWords.hashCode();

                if(this.shingles.containsKey(currentShingle)) {
                    // if the shingle is already in the map, get the arrayList with all the documents that have that shingle
                    ArrayList<Integer> documentsWithThisShingle = (ArrayList<Integer>) this.shingles.get(currentShingle);

                    // if the current document that I am analysing does already have this single previously, don't do anything (is already here)
                    // Otherwise add it to the list
                    if(!documentsWithThisShingle.contains(this.numberOfDocuments)){
                        documentsWithThisShingle.add(this.numberOfDocuments);
                        this.shingles.replace(currentShingle, documentsWithThisShingle);
                    }
                } else {
                    // if this shingle is not yet in the map, add it and map it with the current document
                    ArrayList<Integer> documentsWithThisShingle = new ArrayList<>();
                    documentsWithThisShingle.add(this.numberOfDocuments);
                    this.shingles.put(currentShingle, documentsWithThisShingle);
                }
            }


        } else {
            // the shingling has to be done by character
            for (int i = 0; i <fullText.length()-k; i++) {

                // get the current shingle and hash it
                Integer currentShingle = fullText
                        .substring(i, i+k)
                        .toLowerCase()
                        .hashCode();

                if(this.shingles.containsKey(currentShingle)) {
                    // if the shingle is already in the map, get the arrayList with all the documents that have that shingle
                    ArrayList<Integer> documentsWithThisShingle = (ArrayList<Integer>) this.shingles.get(currentShingle);

                    // if the current document that I am analysing does already have this single previously, don't do anything (is already here)
                    // Otherwise add it to the list
                    if(!documentsWithThisShingle.contains(this.numberOfDocuments)){
                        documentsWithThisShingle.add(this.numberOfDocuments);
                        this.shingles.replace(currentShingle, documentsWithThisShingle);
                    }
                } else {
                    // if this shingle is not yet in the map, add it and map it with the current document
                    ArrayList<Integer> documentsWithThisShingle = new ArrayList<>();
                    documentsWithThisShingle.add(this.numberOfDocuments);
                    this.shingles.put(currentShingle, documentsWithThisShingle);
                }
            }
        }
    }


    /**
     * jaccardSimilarity function
     *
     * This function allows to compare two documents according to Jaccard Similarity
     *
     * @param set1
     * @param set2
     * @return
     */
    public float jaccardSimilarity(Integer set1, Integer set2) {

        int shinglesInCommon = 0; // Shingles intersection
        int shinglesInTotal = 0; // Shingles union

        for (Map.Entry<Integer, ArrayList<Integer>> shingle: this.shingles.entrySet()) {

            // check each shingle if it is present in both the documents, if yes I add it in the numerator
            if (shingle.getValue().contains(set1) && shingle.getValue().contains(set2)) {
                shinglesInCommon++;
            }

            // check each shingle, if it is present one of the two documents, add it to the denominator
            if (shingle.getValue().contains(set1) || shingle.getValue().contains(set2)) {
                shinglesInTotal++;
            }
        }

        System.out.println("Shingles in common:\t" + shinglesInCommon);
        System.out.println("Shingles in total:\t" + shinglesInTotal);
        // sets ratio
        return (float) shinglesInCommon/ (float) shinglesInTotal;
    }

    /**
     * minHashing function
     *
     * @param numberOfHashFunction
     * @return matrix of signatures
     */
    public Integer[][] minHashing(int numberOfHashFunction){

        // Signature matrix
        this.signatureMatrix = new Integer[numberOfHashFunction][this.numberOfDocuments];


        // creating a matrix that allows to create random hash functions and to reuse them during the process.
        // values can be something in between 1 and 100 [range is our arbitrary choice]
        int[][] hashFunctionParameters = new int[numberOfHashFunction][3];

        for (int i = 0; i < hashFunctionParameters.length; i++) {

            // for the third element I should get a prime number. Explore possibilities in this sense
            for (int j = 0; j < hashFunctionParameters[i].length; j++) {
                hashFunctionParameters[i][j] = 1 + (int)(Math.random() * ((1048576 - 1) + 1));
            }
        }

        // implement the minHashing algorithm according to slides
        for (Map.Entry<Integer, ArrayList<Integer>> row: this.shingles.entrySet()) {
            // compute the hashfunctions and save the result
            int[] hashFunctionsRowResults = new int[hashFunctionParameters.length];

            for (int i = 0; i < hashFunctionParameters.length; i++) {
                hashFunctionsRowResults[i] = randomHashFunction(
                        row.getKey(),
                        hashFunctionParameters[i][0],
                        hashFunctionParameters[i][1],
                        hashFunctionParameters[i][2]);
            }

            // compute each column (document) and save the result in the matrix
            for (int i = 0; i < this.numberOfDocuments; i++) {
                if(row.getValue().contains(i+1)) { // add 1 since I save 1 and not 0 as first document
                    for (int j = 0; j < hashFunctionsRowResults.length; j++) {

                        if (this.signatureMatrix[j][i] == null || hashFunctionsRowResults[j] < this.signatureMatrix[j][i]) {
                            this.signatureMatrix[j][i] = hashFunctionsRowResults[j];
                        }
                    }
                }
            }
        }
        // printMatrix(this.signatureMatritrix);
        return this.signatureMatrix;
    }

    /**
     * randomHashFunction
     *
     * @param shingle
     * @param a
     * @param b
     * @param mod
     *
     * @return
     */
    private int randomHashFunction(int shingle, int a, int b, int mod) {
        return ((BigInteger.valueOf(a).multiply(BigInteger.valueOf(shingle))).add(BigInteger.valueOf(b))).mod(BigInteger.valueOf(mod)).intValue();
    }


    /**
     * getSignatureMatrix getter
     *
     * @return signatureMatrix
     */
    public Integer[][] getSignatureMatrix() {
        return this.signatureMatrix;
    }


    /**
     * compareSignatures
     *
     * @param set1
     * @param set2
     * @return
     */
    public float compareSignatures(int set1, int set2) {

        int signaturesInCommon = 0; // Signatures that are in both the documents
        int signaturesInTotal = this.signatureMatrix.length; // number of hash functions in the signature

        for (int i = 0; i < this.signatureMatrix.length; i++) {
            if (signatureMatrix[i][set1-1] == signatureMatrix[i][set2-1]) {
                signaturesInCommon++;
            }
        }

        System.out.println("Signatures in common:\t" + signaturesInCommon);
        System.out.println("Signatures in total:\t" + signaturesInTotal);

        return (float) signaturesInCommon/ (float) signaturesInTotal;
    }

    /**
     * getShingles
     *
     * @return matrix of signatures
     */
    public Map<Integer, ArrayList<Integer>> getShingles() {
        return this.shingles;
    }

    /**
     * getNumberOfDocuments
     *
     * @return numberOfDocuments
     */
    public int getNumberOfDocuments() {
        return this.numberOfDocuments;
    }

    public String getInfo() {
        String info = "";
        info += "K: " + this.k;
        info += "\nBY WORD: " + this.byWord;
        info += "\nNUM OF DOCS: " + this.numberOfDocuments;
        info += "\nSIZE: " + this.shingles.size();

        return info;
    }
    /**
     * printMatrix
     *
     * Utility function to print the matrix.
     *
     */
    private void printMatrix(Integer[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}

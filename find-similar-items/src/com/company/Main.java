package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Main {

    // read files and compute the shingles for that file
    public static void computeShingle(Shingling shingling, File file) {

        console("FILE NAME:", file);

        String document = "";
        String line = "";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));

            while((line = br.readLine()) != null) {
                // beautify the string
                document += line
                        .replaceAll("\\s{2,}", " ")
                        //.replaceAll("\\s", "")
                        .replace("’", "")
                        .toLowerCase();

            }

            // create the k-shingle for the document, the shingles are created by word
            shingling.shingleADocument(document);

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

    // test the jaccard similarity
    private static void testJaccardSimilarity(Shingling shingling, int set1, int set2) {
        // Jaccard similarity of two sets
        System.out.println("== JACCARD SIMILARITY: ==");
        Float setsComparation = shingling.jaccardSimilarity(set1,set2);
        console("RATIO:", setsComparation);
    }

    // test the signature creation
    private static void testSignatures(Shingling shingling, int set1, int set2) {

        // compare signatures
        System.out.println("== SIGNATURES COMPARISON: ==");
        Float signatureComparison = shingling.compareSignatures(set1, set2);
        console("RATIO: ", signatureComparison);
    }

    // find candidates with LSH
    private static ArrayList<String> findCandidates(Shingling shingling) {
        System.out.println("== LSH: ==");

        LSH lsh = new LSH(shingling.getSignatureMatrix());
        ArrayList<String> candidateTuples = lsh.findCandidates(0.50);
        console("LSH CANDIDATES: ", candidateTuples);

        return candidateTuples;
    }

    // checks the candidates result of the LSH
    private static void checkCandidates(Shingling shingling, ArrayList<String> candidateTuple, double threshold) {

        for(String tuple: candidateTuple) {
            int[] candidates = Arrays
                                .asList(tuple.split("-"))
                                .stream()
                                .mapToInt(Integer::parseInt)
                                .toArray();

            System.out.println("\n== JACCARD SIMILARITY OF DOC "+candidates[0]+" AND "+candidates[1]+":");

            // Jaccard similarity
            Float setsComparation = shingling.jaccardSimilarity(candidates[0], candidates[1]);
            System.out.println("RATIO:\t\t" + setsComparation);
            System.out.println("THRESHOLD:\t" + threshold);
            System.out.println("SIMILAR?\t" + (setsComparation > threshold));
        }
    }

    // utily method that prints information at console
    public static void console(String info, Object toPrint) {
        System.out.print(info+" ");
        System.out.println(toPrint);
    }

    /**
     * Entry Point: Test find similar items algorithm
     *
     * Shingle each document by a factor k = 10 or 9 characters or by a factor of 3 words.
     *
     * @param args
     */
    public static void main(String[] args) {

        int k= 9;
        boolean byWord = false;

        if (args.length < 2) {
            System.err.println("Usage: <k> <by-word>");
            System.err.println("k: int from 1 to 15");
            System.err.println("by-word: true/false");
            System.exit(-1);
        } else {
            k = Integer.parseInt(args[0]);
            byWord = Boolean.valueOf(args[1]);

        }
        // create the shingling object: the shingles will be created by word
        Shingling shingling = new Shingling(k, byWord);

        // read the test files
        File folder = new File("input");
        File[] listOfFiles = folder.listFiles();

        System.out.println("READ FILES AND COMPUTE SHINGLES\n");
        // compute the shingles for each file
        for (File file: listOfFiles) {
            computeShingle(shingling, file);
        }

        System.out.println("\nGET SHINGLES INFO\n");
        // get the map of shingles and documents
        Map<Integer, ArrayList<Integer>> shingles = shingling.getShingles();
        //console("SHINGLES:", shingles);
        console("SHINGLES INFO:", "\n"+shingling.getInfo());

        // Test the jaccardSimilarity and signatures by comparing two texts characterised by plagiarism:
        // - "page-rank.txt": description of Page Rank as provided by wikipedia.com
        // - "page-rank-plagiarism.txt": description of Page Rank provided by a student who "took inspiration"
        //   from Wikipedia.
        int set1 = 0, set2 = 0;

        System.out.println("\nRUN A TEST ON 2 FILES: 'page-rank.txt' and 'page-rank-plagiarism.txt'\n");
        System.out.println("File list:");
        for (int i = 0; i <listOfFiles.length; i++) {

            System.out.println("[File "+(i+1)+"] - "+listOfFiles[i]);

            if (listOfFiles[i].getName().equals("page-rank.txt")) {
                set1 = i + 1;
            }
            if (listOfFiles[i].getName().equals("page-rank-plagiarism.txt")){
                set2 = i + 1;
            }
        }

        testJaccardSimilarity(shingling, set1, set2);
        // min hash: create the signatures for all the documents
        Integer[][] signature = shingling.minHashing(100);
        testSignatures(shingling, set1, set2);


        // ***** BONUS PART: Locality Sensitive Hashing ********

        System.out.println("\nPART 2: LOCALITY SENSITIVE HASHING\n");

        // Find the possible similar sets
        ArrayList<String> candidateTuples = findCandidates(shingling);

        // check whether they are actually similar
        checkCandidates(shingling, candidateTuples, 0.50);
    }
}

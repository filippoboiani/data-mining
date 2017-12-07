package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimeNumbers {

    private static boolean initialized = false;
    private static ArrayList<Integer> primeNumbers = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(PrimeNumbers.getPrimeNumber());
    }

    public static int getPrimeNumber() {
        if (!initialized) {
            boolean initialized = PrimeNumbers.initialize();
        }

        return primeNumbers.get((new Random()).nextInt(primeNumbers.size()));
    }

    private static boolean initialize() {
        // New Java 8 method - WOW!
        Path path = Paths.get("./src/com/company/primeNumbers.txt");

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] numbers = line.split("\\s");
                for (String number: numbers) {

                    try {
                        primeNumbers.add(new Integer(number));
                    }
                    catch (NumberFormatException nfe) {
                        // not a number
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        PrimeNumbers.initialized = true;
        return true;
    }
}

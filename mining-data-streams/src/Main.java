import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Entry point
 */
public class Main {

    public static void main(String[] args) {

        int[] elements;
        int sampleSize = 150000;
        File fileToAnalyse = new File("input/out.facebook-wosn-links");

        // if wrong args, exit
        if(args.length>0 && args.length != 2) {
            System.err.println("Usage: <filename> <threshold> <min-confidence>");
            System.exit(-1);
        }

        // if right args
        if(args.length==2) {
            fileToAnalyse = new File("input/"+args[0]);
            sampleSize = Integer.parseInt(args[1]);
        }



        // instantiate the algorithm
        TRIEST triestAlgorithm = new TRIEST(sampleSize);
        // TRIEST_IMPR triestAlgorithm = new TRIEST_IMPR(sampleSize);

        System.out.println("Starting to count...");

        // start timer
        long startTime = System.currentTimeMillis();

        // create a stream of the file
        readFile(fileToAnalyse, triestAlgorithm);

        // stop timer
        long endTime   = System.currentTimeMillis();
        double totalTime = (endTime - startTime)/ 1000;

        // when the input has finished you can read the values
        double globalCount = triestAlgorithm.getGlobalCounter();

        //System.out.println("Triangles found: "+globalCount);
        System.out.println("Process Finished after "+totalTime+" seconds.");

        System.out.println("Estimation " + triestAlgorithm.getEstimation());


    }

    /**
     * prepareFile function.
     *
     * The file has to be as follow:
     * - One line per basket
     * - the items in one basket are integers
     * - the items are separated by a space
     *
     */
    private static BufferedReader readFile(File fileToAnalyse, TRIEST triestAlgorithm) {

        BufferedReader br = null;
        int[] elements;
        int limit = 0;

        try {
            br = new BufferedReader(new FileReader(fileToAnalyse));
            br.readLine();

            while(br.ready()) {
                limit++;
                // read the new input
                elements =  convertLineToArray(br.readLine());

                // feed the algoritm with new input
                triestAlgorithm.analyze(true, Arrays.copyOfRange(elements, 0, 2));

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
            return null;
        }
    }

    /**
     * converst the line read from the input stream to an array of int
     *
     * @param line
     * @return
     */
    private static int[] convertLineToArray(String line) {
        return Arrays.asList(line.split("\\s")).stream().mapToInt(Integer::parseInt).toArray();
    }


}

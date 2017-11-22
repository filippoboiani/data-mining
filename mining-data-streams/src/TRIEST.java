import java.util.*;



/**
 * Implementation of the TREIST algorithm - Paper 2
 */
public class TRIEST implements TriestIface {

    /** size of the sample */
    protected final int maxSampleSize;

    /** the stream timestamp */
    protected int timestamp;

    /** the counter of global and local triangles */
    protected double globalCounter;
    protected HashMap<Integer, Integer> localCounter = null;

    /** the sample set */
    protected HashMap<Integer, int[]> sampleSet = null;

    /** adjacency list */
    protected HashMap<Integer, Set<Integer>> adjacencyList = null;

    /** biased coin */
    protected enum BiasedCoin {
        /** options */
        HEAD, TAIL;

        /** biased coin flip */
        static BiasedCoin flip(double bias) {
            return Math.random()<bias ? HEAD : TAIL;
        }
    }

    /** default constructor */
    public TRIEST() {
        // init counters
        this.globalCounter = 0;
        this.localCounter = new HashMap<>();

        // init sample
        this.maxSampleSize = 1000;
        this.sampleSet = new HashMap<>();
        this.adjacencyList = new HashMap<>();

        // init timestamp
        this.timestamp = 0;
    }

    public TRIEST(int maxSampleSize) {
        // init counters
        this.globalCounter = 0;
        this.localCounter = new HashMap<>();

        // init sample
        this.maxSampleSize = maxSampleSize;
        this.sampleSet = new HashMap<>();
        this.adjacencyList = new HashMap<>();

        // init timestamp
        this.timestamp = 0;
    }

    public boolean insert(int[] edge) {

        timestamp++;
        if(reservoirSampleEdge(edge, timestamp)) {
            // add to sample
            addToSample(edge);
            // update the counters
            updateCounters(true, edge);
            return true;
        }
        return false;
    }


    /**
     *
     * reservoir sampling part of the algorithm
     * @param edge
     * @param timestamp
     * @return
     */
    public boolean reservoirSampleEdge(int[] edge, int timestamp){

        // if the number of samples is lower than the max sample size
        if (timestamp < maxSampleSize) {
             return true;
        } else if (BiasedCoin.flip(maxSampleSize/timestamp) == BiasedCoin.HEAD) {

            // pick a random edge from the sample
            int[] randomEdge = pickRandomEdge();

            // remove the edge from the sample
            removeFromSample(randomEdge);

            // update counters
            updateCounters(false, edge);
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public int[] pickRandomEdge() {
        List<Integer> keysAsArray = new ArrayList<>(sampleSet.keySet());
        Random r = new Random();

        return sampleSet.get(keysAsArray.get( r.nextInt( keysAsArray.size())));
    }

    /**
     * update the triangle counters (both local and global)
     * @param addition boolean value: if true add, remove otherwise
     * @param edge a couple of node ids
     */
    public void updateCounters(boolean addition, int[] edge) {

        // get shared neighbourhood
        Set intersection = new HashSet<>(adjacencyList.get(edge[0]));
        intersection.retainAll(adjacencyList.get(edge[1]));

        // for all common neighbours
        Iterator intersectionIterator = intersection.iterator();
        while(intersectionIterator.hasNext()) {

            intersectionIterator.next();

            // update counters
            if (addition) {
                globalCounter++;
            } else {
                globalCounter--;

            }
        }
    }

    /**
     * @param edge
     */
    public void addToSample(int[] edge){

        // add the edge to the sample set
        sampleSet.put(Arrays.hashCode(edge), edge);

        // add the connection from the adjacency matrix

        Set<Integer> aList = adjacencyList.get(edge[0]);

        if(aList == null) {
            Set<Integer> set = new HashSet<>();
            set.add(edge[1]);
            adjacencyList.put(edge[0], set);
        } else {
            aList.add(edge[1]);
        }


        aList = adjacencyList.get(edge[1]);
        if(aList == null) {
            Set<Integer> set = new HashSet<>();
            set.add(edge[0]);
            adjacencyList.put(edge[1], set);
        } else {
            aList.add(edge[0]);
        }

    }

    /**
     * @param edge
     */
    public void removeFromSample(int[] edge) {

        // remove the edge from the sample set
        sampleSet.remove(Arrays.hashCode(edge));

        // remove the connection from the adjacency matrix
        Set<Integer> aList = adjacencyList.get(edge[0]);
        aList.remove(edge[1]);

        aList = adjacencyList.get(edge[1]);
        aList.remove(edge[0]);
    }

    public double getGlobalCounter() {
        return globalCounter;
    }
}

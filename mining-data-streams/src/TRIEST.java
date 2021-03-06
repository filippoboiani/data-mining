import java.util.*;


/**
 * TRIEST algorithm.
 *
 * Count triangles in a graph provided as stream of edges
 */
public class TRIEST {

    /** the sample size */
    double m = 100000;

    /** time */
    double timestamp = 0;

    /** global counter */
    double totalTriangles = 0;

    /** local counters */
    protected final Map<Integer, Double> triangles = new HashMap();

    /** adjacency lists */
    protected final Map<Integer, ArrayList<Integer>> graphReprensentation = new HashMap<Integer, ArrayList<Integer>>();

    /** random value */
    public static int random = 0;

    /** list of edges in the sample */
    protected final ArrayList<int[]> edgeList = new ArrayList<int[]>();

    /** biased coin */
    protected enum BiasedCoin {
        /** options */
        HEAD, TAIL;

        /** biased coin flip */
        static TRIEST.BiasedCoin flip(double bias) {
            return Math.random()<bias ? HEAD : TAIL;
        }
    }


    /**
     * constructor
     * @param m
     */
    public TRIEST(int m) {
        this.m = m;
    }


    /**
     * insert or delete edge from the graph (i.e. from the sample)
     * @param addition
     * @param edge
     */
    public void analyze(boolean addition, int[] edge) {
        timestamp++;
//        System.out.println(timestamp + ") Addition " + addition + "  -  Edge " + edge[0] + " " + edge[1]);

        if (sampleEdge(edge, timestamp)) {
            // add edge to graph representation
            addEdgeToGraph(edge);
            // update gloabal and local counters
            updateCounters(true, edge);
        }
    }


    /**
     * reservoir sampling implementation
     * @param edge
     * @param timestamp
     * @return
     */
    public boolean sampleEdge(int[] edge, double timestamp) {
        if(timestamp <= m) {
            return true;
        } else if (BiasedCoin.flip(((double)m)/timestamp) == BiasedCoin.HEAD) {
            random++;

            // pick a random edge
            int[] randomEdge = pickRandomEdge();

            // remove randomEdge from graphRepresentation
            removeEdge(randomEdge);

            // update the counters
            updateCounters(false, randomEdge);

            return true;
        }
        return false;
    }


    /**
     * pick a random edge from the sample
     * @return
     */
    public int[] pickRandomEdge() {
        return edgeList.get((int)(Math.random() * edgeList.size()));
    }


    /**
     * remove an edge from the sample
     * @param edge
     */
    public void removeEdge(int[] edge) {
        graphReprensentation.get(new Integer(edge[0])).remove(new Integer(edge[1]));
        graphReprensentation.get(new Integer(edge[1])).remove(new Integer(edge[0]));

        edgeList.remove(edge);
    }


    /**
     * add edge to the graph representation.
     * Update the adjacency lists
     *
     * @param edge
     */
    protected void addEdgeToGraph(int[] edge) {
        ArrayList<Integer>[] edgeNeighborhood = new ArrayList[2];

        // Get the neighborhood of the 2 vertex in the edge
        for (int i = 0; i < edgeNeighborhood.length; i++) {
            edgeNeighborhood[i] = getNeighborhood(edge[i]);
        }

        // add the opposite vertex to each neighborhood
        edgeNeighborhood[0].add(edge[1]);
        edgeNeighborhood[1].add(edge[0]);

        // update neighbors in graphRepresentation
        graphReprensentation.put(edge[0], edgeNeighborhood[0]);
        graphReprensentation.put(edge[1], edgeNeighborhood[1]);

        edgeList.add(edge);
    }


    /**
     * update local and global counters
     * @param addition
     * @param edge
     */
    public void updateCounters(boolean addition, int[] edge) {
        ArrayList<Integer>[] edgeNeighborhood = new ArrayList[2];

        // Get the neighborhood of the 2 vertex in the edge
        for (int i = 0; i < edgeNeighborhood.length; i++) {
            edgeNeighborhood[i] = getNeighborhood(edge[i]);
        }

        // EdgeNeighborhood becomes the "nodes in common" arrayList;
        ArrayList<Integer> nodesInCommon = new ArrayList<Integer>(edgeNeighborhood[1]);
        nodesInCommon.retainAll(edgeNeighborhood[0]);


        // for each neighbours update the counters
        for (Integer commonNode : nodesInCommon) {

            if (addition) {
                totalTriangles++;
                triangles.put(commonNode, triangles.get(commonNode) == null? 1 : triangles.get(commonNode) + 1);
                triangles.put(edge[0], triangles.get(edge[0]) == null ? 1 : triangles.get(edge[0])+ 1);
                triangles.put(edge[1], triangles.get(edge[1]) == null ? 1 : triangles.get(edge[1])+ 1);
            } else {
                totalTriangles--;
                triangles.put(commonNode, triangles.get(commonNode) - 1);
                triangles.put(edge[0], triangles.get(edge[0]) - 1);
                triangles.put(edge[1], triangles.get(edge[1]) - 1);
            }
        }
    }


    /**
     * get neighbours of a vertex
     * @param node
     * @return list of neighbours
     */
    protected ArrayList<Integer> getNeighborhood(int node) {
        return graphReprensentation.containsKey(node) ? graphReprensentation.get(node) : new ArrayList<Integer>();
    }


    /**
     * get the global counter
     * @return global counter
     */
    public int getGlobalCounter() {
        return (int) totalTriangles;
    }


    /**
     * get the estimated triangle count
     * @return numer of triangles
     */
    public int getEstimation() {
        double numerator = ( timestamp * (timestamp - 1 ) * (timestamp - 2));
        double denominator =  (m * (m - 1) * (m - 2));

        return (int) (totalTriangles * Math.max(1.00, numerator/denominator));
    }
}
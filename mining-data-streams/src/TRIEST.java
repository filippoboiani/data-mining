import java.util.*;


public class TRIEST {

    int m = 1000;
    int timestamp = 0;
    Integer totalTriangles = 0;
    Map<Integer, Integer> triangles = new HashMap();
    Map<Integer, ArrayList<Integer>> graphReprensentation = new HashMap<Integer, ArrayList<Integer>>();
    public static int random = 0;
    ArrayList<int[]> edgeList = new ArrayList<int[]>();

    /** biased coin */
    protected enum BiasedCoin {
        /** options */
        HEAD, TAIL;

        /** biased coin flip */
        static TRIEST.BiasedCoin flip(double bias) {
            return Math.random()<bias ? HEAD : TAIL;
        }
    }

    public TRIEST(int m) {
        this.m = m;
    }

    public void analyze(boolean addition, int[] edge) {
        timestamp++;
//        System.out.println(timestamp + ") Addition " + addition + "  -  Edge " + edge[0] + " " + edge[1]);

        if (sampleEdge(edge, timestamp)) {
            // add edge to graph representation
            addEdgeToGraph(edge);
            updateCounters(true, edge);
        }
    }

    public boolean sampleEdge(int[] edge, int timestamp) {
//        if(timestamp <= m) {
//            return true;
//        } else if (BiasedCoin.flip(((double)m)/timestamp) == BiasedCoin.HEAD) {
//            random++;
//
//            int[] randomEdge = pickRandomEdge();
//
////             remove randomEdge from graphRepresentation
//            removeEdge(randomEdge);
//            updateCounters(false, randomEdge);
//            return true;
//        }
//        return false;
        return true;
    }

    private void addEdgeToGraph(int[] edge) {
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

    public void updateCounters(boolean addition, int[] edge) {
        ArrayList<Integer>[] edgeNeighborhood = new ArrayList[2];

        // Get the neighborhood of the 2 vertex in the edge
        for (int i = 0; i < edgeNeighborhood.length; i++) {
            edgeNeighborhood[i] = getNeighborhood(edge[i]);
        }

        // EdgeNeighborhood becomes the "nodes in common" arrayList;
        ArrayList<Integer> nodesInCommon = new ArrayList<Integer>(edgeNeighborhood[1]);
        nodesInCommon.retainAll(edgeNeighborhood[0]);


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

    private ArrayList<Integer> getNeighborhood(int node) {
        return graphReprensentation.containsKey(node) ? graphReprensentation.get(node) : new ArrayList<Integer>();
    }

    public int getGlobalCounter() {
        return totalTriangles;
    }
}
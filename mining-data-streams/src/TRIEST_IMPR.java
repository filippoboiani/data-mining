import java.util.ArrayList;

public class TRIEST_IMPR extends TRIEST {

    public TRIEST_IMPR(int m) { super(m);}

    public void analyze(boolean addition, int[] edge) {
        timestamp++;
//        System.out.println(timestamp + ") Addition " + addition + "  -  Edge " + edge[0] + " " + edge[1]);

        updateCounters(true, edge);
        if (sampleEdge(edge, timestamp)) {
            // add edge to graph representation
            addEdgeToGraph(edge);
        }
    }

    public boolean sampleEdge(int[] edge, double timestamp) {
        if(timestamp <= m) {
            return true;
        } else if (BiasedCoin.flip(((double)m)/timestamp) == BiasedCoin.HEAD) {
            random++;

            int[] randomEdge = pickRandomEdge();

            // remove randomEdge from graphRepresentation
            removeEdge(randomEdge);
            // But not update the counter
            //updateCounters(false, randomEdge);


            return true;
        }
        return false;
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
                double numerator = ((timestamp - 1 ) * (timestamp - 2));
                double denominator =  (m * (m - 1));

                double weightedIncrease = Math.max(1.00, numerator/denominator);
                totalTriangles++;
                triangles.put(commonNode, triangles.get(commonNode) == null? weightedIncrease : triangles.get(commonNode) + weightedIncrease);
                triangles.put(edge[0], triangles.get(edge[0]) == null ? weightedIncrease : triangles.get(edge[0])+ weightedIncrease);
                triangles.put(edge[1], triangles.get(edge[1]) == null ? weightedIncrease : triangles.get(edge[1])+ weightedIncrease);
            } else {
                totalTriangles--;
                triangles.put(commonNode, triangles.get(commonNode) - 1);
                triangles.put(edge[0], triangles.get(edge[0]) - 1);
                triangles.put(edge[1], triangles.get(edge[1]) - 1);
            }
        }
    }

}
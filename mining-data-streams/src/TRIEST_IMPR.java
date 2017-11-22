import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TRIEST_IMPR extends TRIEST {

    public TRIEST_IMPR(int maxSampleSize) {
        super(maxSampleSize);
    }


    @Override
    public boolean insert(int[] edge) {

        timestamp++;

        // ----------- update the counters, moved before the if -----------
        updateCounters(true, edge);

        if(reservoirSampleEdge(edge, timestamp)) {
            // add to sample
            addToSample(edge);

            return true;
        }
        return false;
    }


    // Remove the update counters with subtraction of edge triangles
    public boolean reservoirSampleEdge(int[] edge, int timestamp){

        // if the number of samples is lower than the max sample size
        if (timestamp < maxSampleSize) {
            return true;
        } else if (BiasedCoin.flip(maxSampleSize/timestamp) == BiasedCoin.HEAD) {

            // pick a random edge from the sample
            int[] randomEdge = pickRandomEdge();

            // remove the edge from the sample
            removeFromSample(randomEdge);

            // ----------- NOT UPDATE COUNTERS -----------
            return true;
        }
        return false;
    }


    /**
     * Perform a wighted increase of the counters
     * @param addition boolean value: if true add, remove otherwise
     * @param edge a couple of node ids
     */
    public void updateCounters(boolean addition, int[] edge) {

        // get shared neighbourhood
        Set intersection = new HashSet<>(adjacencyList.get(edge[0]));
        intersection.retainAll(adjacencyList.get(edge[1]));

        // ----------- Weighted increase -----------
        double weightedIncrease = Math.max(1.00, ( ((timestamp - 1 ) * (timestamp - 2)) / ((maxSampleSize) * (maxSampleSize-1))));

        // for all common neighbours
        Iterator intersectionIterator = intersection.iterator();
        while(intersectionIterator.hasNext()) {

            intersectionIterator.next();

            // update counters
            if (addition) {
                globalCounter += weightedIncrease;
            } else {
                // This part will never be called again
                System.out.println("UNREACHABLE CODE, IF YOU SEE THIS, WE HAVE A PROBLEM");
                globalCounter--;

            }
        }
    }
}

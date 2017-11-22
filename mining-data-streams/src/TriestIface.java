public interface TriestIface {

    /**
     *
     * Insert the edge in the sample
     *
     * @param edge
     * @return if the edge has been inserted in the sample
     */
    boolean insert(int[] edge);

    /**
     *
     * @param edge
     * @param timestamp
     * @return if the edge needs to be inserted in the sample (it use reservoir random picking)
     */
    boolean reservoirSampleEdge(int[] edge, int timestamp);

    /**
     * Pick a random edge to remove from the sample
     * @return random edge within the sample
     */
    int[] pickRandomEdge();

    /**
     * Add the current edge to the sample
     * @param edge
     */
    void addToSample(int[] edge);

    /**
     * remove one edge fromm the sample
     * @param edge
     */
    void removeFromSample(int[] edge);

    /**
     * update the live counter of triangles
     * @param addition
     * @param edge
     */
    void updateCounters(boolean addition, int[] edge);

    /**
     * Return the number of current triangles
     * @return
     */
    double getGlobalCounter();
}

package edu.touro.mco152.bm.observer;



import edu.touro.mco152.bm.persist.DiskRun;
/**
 * Observer interface for objects that wish to be notified when a benchmark command completes.
 */
public interface Observer {
    /**
     * Called when a benchmark command finishes execution.
     *
     * @param run The result of the completed DiskRun
     */
    void update(DiskRun run);
}

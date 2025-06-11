package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * The Subject interface for the Observer pattern.
 * Classes implementing this interface can register, remove,
 * and notify observers about events (e.g., benchmark completion).
 */
public interface Subject {
    /**
     * Register an observer to be notified when an event occurs.
     *
     * @param //observer the observer to add
     */
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    /**
     * Notify all registered observers with the given benchmark result.
     *
     * @param run the DiskRun result to pass to observers
     */
    void notifyObservers(DiskRun run);
}


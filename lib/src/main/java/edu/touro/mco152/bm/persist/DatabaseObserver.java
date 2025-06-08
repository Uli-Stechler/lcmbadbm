package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.observer.Observer;

import jakarta.persistence.EntityManager;
/**
 * Observer that persists benchmark results to the database.
 * Implements the Observer interface and reacts to completed benchmarks.
 */
public class DatabaseObserver implements Observer {
    /**
     * Persists the benchmark result to the database.
     *
     * @param run the completed DiskRun benchmark to persist
     */
    @Override
    public void update(DiskRun run) {
        EntityManager em = EM.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(run);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("DatabaseObserver: Failed to persist DiskRun - " + e.getMessage());
        }
    }
}

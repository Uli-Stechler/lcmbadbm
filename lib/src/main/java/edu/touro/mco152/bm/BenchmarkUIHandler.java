package edu.touro.mco152.bm;


/**
 * Interface to abstract UI operations for benchmarking.
 */
public interface BenchmarkUIHandler {
    void updateProgress(int percent);
    void displayInfo(String message);
    void displayWarning(String message);
    boolean shouldDoReadBenchmark();
}

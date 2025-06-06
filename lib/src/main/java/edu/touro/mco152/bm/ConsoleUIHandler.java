package edu.touro.mco152.bm;

public class ConsoleUIHandler implements BenchmarkUIHandler {
    private int lastProgress = 0;
    @Override
    public void updateProgress(int percent) {
        System.out.println("Progress: " + percent + "% completed.");
        lastProgress = percent;
    }

    public int getLastProgress() {
        return lastProgress;
    }


    @Override
    public void displayInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void displayWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    @Override
    public boolean shouldDoReadBenchmark() {
        // For tests or command line, assume YES.
        return true;
    }
}

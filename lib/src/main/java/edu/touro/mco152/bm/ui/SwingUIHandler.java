package edu.touro.mco152.bm.ui;


import edu.touro.mco152.bm.BenchmarkUIHandler;
import javax.swing.*;

public class SwingUIHandler implements BenchmarkUIHandler {

    @Override
    public void updateProgress(int percent) {
        Gui.progressBar.setValue(percent);
    }

    @Override
    public void displayInfo(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public void displayWarning(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean shouldDoReadBenchmark() {
        int result = JOptionPane.showConfirmDialog(null,
                "Would you like to run the READ benchmark after the WRITE benchmark?",
                "Read Benchmark",
                JOptionPane.YES_NO_OPTION);
        return (result == JOptionPane.YES_OPTION);
    }
}

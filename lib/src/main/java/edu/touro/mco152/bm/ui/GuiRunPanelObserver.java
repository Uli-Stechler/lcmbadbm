package edu.touro.mco152.bm.ui;

import edu.touro.mco152.bm.observer.Observer;
import edu.touro.mco152.bm.persist.DiskRun;
/**
 * Observer that updates the GUI run panel when a benchmark completes.
 * This ensures that UI components reflect the latest benchmark results.
 */
public class GuiRunPanelObserver implements Observer {
    /**
     * Adds the benchmark run result to the application's run panel UI.
     *
     * @param run the completed DiskRun benchmark to display
     */
    @Override
    public void update(DiskRun run) {
        if (Gui.runPanel != null) {
            Gui.runPanel.addRun(run);
        }
    }
}

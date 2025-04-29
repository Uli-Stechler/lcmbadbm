package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class NonSwingDiskWorkerTest {

    /**
     * Mimics minimal App.init() logic for headless benchmarking.
     */
    private static void setupDefaultAsPerProperties() {
        Gui.mainFrame = new MainFrame();
        App.p = new java.util.Properties();
        App.loadConfig();

        Gui.progressBar = new JProgressBar();

        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        if (App.locationDir == null) {
            App.locationDir = new File(System.getProperty("user.home"));
        }

        App.dataDir = new File(App.locationDir.getAbsolutePath() + File.separator + App.DATADIRNAME);

        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        } else {
            App.dataDir.mkdirs();
        }

        // Optional: disable persistence during test
        App.testMode = true;
    }

    @BeforeEach
    void setup() {
        setupDefaultAsPerProperties();
    }

    @Test
    @DisplayName("DiskWorker completes successfully and produces valid results (non-Swing mode)")
    public void testDiskWorkerRunsWithoutSwing() {
        ConsoleUIHandler uiHandler = new ConsoleUIHandler();
        DiskWorker worker = new DiskWorker(uiHandler);

        try {
            Boolean result = worker.doInBackground();
            assertTrue(result, "DiskWorker did not complete successfully");

            // ✅ Verify progress reached at least 90%
            assertTrue(uiHandler.getLastProgress() >= 90, "Progress did not reach at least 90%");

            // ✅ Verify that average throughput was computed
            assertTrue(App.wAvg > 0, "Average write throughput (wAvg) was not recorded");

        } catch (Exception e) {
            fail("Exception occurred during DiskWorker execution: " + e.getMessage());
        }
    }
}

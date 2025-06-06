package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;

import jakarta.persistence.EntityManager;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.commands.ReadCommand;
import edu.touro.mco152.bm.commands.WriteCommand;
import edu.touro.mco152.bm.commands.SimpleExecutor;
import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;


/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Cooperates with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and declares that its final return (when
 * doInBackground() is finished) is of type Boolean, and declares that intermediate results are communicated to
 * Swing using an instance of the DiskMark class.
 */

public class DiskWorker extends SwingWorker<Boolean, DiskMark> {

    // Record any success or failure status returned from SwingWorker (might be us or super)
    Boolean lastStatus = null;  // so far unknown
    private final BenchmarkUIHandler uiHandler;

    public DiskWorker(BenchmarkUIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @Override
    protected Boolean doInBackground() {
        Logger.getLogger(App.class.getName()).log(Level.INFO, "*** New worker thread started ***");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);

        int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        if (App.autoReset) {
            App.resetTestData();
            uiHandler.displayInfo("Test data reset");
        }

        int startFileNum = App.nextMarkNumber;
        uiHandler.displayInfo("Legend updated");

        if (App.writeTest) {
            WriteCommand writeCommand = new WriteCommand(
                    App.numOfMarks,
                    App.numOfBlocks,
                    App.blockSizeKb,
                    App.blockSequence,
                    App.dataDir,
                    App.writeSyncEnable,
                    App.multiFile,
                    App.testFile,
                    App.MEGABYTE,
                    uiHandler
            );
            new SimpleExecutor().executeAll(List.of(writeCommand));
        }

        if (App.readTest && App.writeTest && !isCancelled() && uiHandler.shouldDoReadBenchmark()) {
            uiHandler.displayInfo("""
                    For valid READ measurements please clear the disk cache by
                    using the included RAMMap.exe or flushmem.exe utilities.
                    Removable drives can be disconnected and reconnected.
                    For system drives use the WRITE and READ operations 
                    independently by doing a cold reboot after the WRITE""");
        }

        if (App.readTest) {
            ReadCommand readCommand = new ReadCommand(
                    App.numOfMarks,
                    App.numOfBlocks,
                    App.blockSizeKb,
                    App.blockSequence,
                    App.dataDir,
                    App.multiFile,
                    App.testFile,
                    App.MEGABYTE,
                    uiHandler
            );
            new SimpleExecutor().executeAll(List.of(readCommand));
        }

        App.nextMarkNumber += App.numOfMarks;

        return true;
    }

    /**
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of DiskMark objects reflecting some completed benchmarks
     */
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }


    @Override
    protected void done() {
        // Obtain final status, might from doInBackground ret value, or SwingWorker error
        try {
            lastStatus = super.get();   // record for future access
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).warning("Problem obtaining final status: " + e.getMessage());
        }

        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        uiHandler.displayInfo("Benchmark finished.");
    }

    public Boolean getLastStatus() {
        return lastStatus;
    }
}

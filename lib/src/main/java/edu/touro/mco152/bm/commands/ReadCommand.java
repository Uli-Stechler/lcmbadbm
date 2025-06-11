package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.BenchmarkUIHandler;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.persist.DiskRun;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Logger;

public class ReadCommand implements Command {

    private final int numMarks;
    private final int numBlocks;
    private final int blockSizeKb;
    private final DiskRun.BlockSequence sequence;
    private final File dataDir; // replaced App.dataDir
    private final boolean multiFile; // replaced App.multiFile
    private final File testFile; // replaced App.testFile
    private final int megaByteSize; // replaced App.MEGABYTE
    private final BenchmarkUIHandler ui;

    public ReadCommand(int numMarks, int numBlocks, int blockSizeKb,
                       DiskRun.BlockSequence sequence,
                       File dataDir,
                       boolean multiFile,
                       File testFile,
                       int megaByteSize,
                       BenchmarkUIHandler ui) {
        this.numMarks = numMarks;
        this.numBlocks = numBlocks;
        this.blockSizeKb = blockSizeKb;
        this.sequence = sequence;
        this.dataDir = dataDir; // from App.dataDir
        this.multiFile = multiFile; // from App.multiFile
        this.testFile = testFile; // from App.testFile
        this.megaByteSize = megaByteSize; // from App.MEGABYTE
        this.ui = ui;
    }

    @Override
    public DiskRun execute() {
        int blockSize = blockSizeKb * 1024;
        byte[] blockArr = new byte[blockSize];
        int rUnitsComplete = 0;
        int unitsTotal = numMarks * numBlocks;
        float percentComplete;

        DiskRun run = new DiskRun(DiskRun.IOMode.READ, sequence);
        run.setNumMarks(numMarks);
        run.setNumBlocks(numBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize((long) blockSizeKb * numBlocks * numMarks);
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        ui.displayInfo("Disk Info: " + run.getDiskInfo());

        for (int m = 0; m < numMarks; m++) {
            File currentTestFile;
            if (multiFile) {
                currentTestFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata" + m + ".jdm"); // replaced App.dataDir
            } else {
                currentTestFile = testFile; // replaced App.testFile
            }

            DiskMark rMark = new DiskMark(DiskMark.MarkType.READ);
            rMark.setMarkNum(m + 1);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try (RandomAccessFile raf = new RandomAccessFile(currentTestFile, "r")) {
                for (int b = 0; b < numBlocks; b++) {
                    if (sequence == DiskRun.BlockSequence.RANDOM) {
                        int rLoc = Util.randInt(0, numBlocks - 1);
                        raf.seek((long) rLoc * blockSize);
                    } else {
                        raf.seek((long) b * blockSize);
                    }

                    raf.readFully(blockArr, 0, blockSize);
                    totalBytesReadInMark += blockSize;
                    rUnitsComplete++;
                    percentComplete = (float) rUnitsComplete / unitsTotal * 100f;
                    ui.updateProgress((int) percentComplete);
                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).severe("Read error: " + e.getMessage());
            }

            long endTime = System.nanoTime();
            double sec = (endTime - startTime) / 1_000_000_000.0;
            double mbRead = (double) totalBytesReadInMark / megaByteSize; // replaced App.MEGABYTE
            rMark.setBwMbSec(mbRead / sec);

            edu.touro.mco152.bm.App.updateMetrics(rMark); // OK to call static utility
            ui.displayInfo("m:" + m + " READ IO is " + rMark.getBwMbSecAsString() + " MB/s");

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
        }

        run.setEndTime(new Date());

        run.setRunMax(run.getRunAvg() * 1.05);

        return run;
    }
}

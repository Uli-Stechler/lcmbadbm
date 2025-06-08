package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.BenchmarkUIHandler;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.persist.DiskRun;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Logger;

public class WriteCommand implements Command {

    private final int numMarks;
    private final int numBlocks;
    private final int blockSizeKb;
    private final DiskRun.BlockSequence sequence;
    private final File dataDir; // replaced App.dataDir
    private final boolean writeSyncEnable; // replaced App.writeSyncEnable
    private final boolean multiFile; // replaced App.multiFile
    private final File testFile; // replaced App.testFile
    private final int megaByteSize; // replaced App.MEGABYTE
    private final BenchmarkUIHandler ui;

    public WriteCommand(int numMarks, int numBlocks, int blockSizeKb,
                        DiskRun.BlockSequence sequence,
                        File dataDir,
                        boolean writeSyncEnable,
                        boolean multiFile,
                        File testFile,
                        int megaByteSize,
                        BenchmarkUIHandler ui) {
        this.numMarks = numMarks;
        this.numBlocks = numBlocks;
        this.blockSizeKb = blockSizeKb;
        this.sequence = sequence;
        this.dataDir = dataDir; // from App.dataDir
        this.writeSyncEnable = writeSyncEnable; // from App.writeSyncEnable
        this.multiFile = multiFile; // from App.multiFile
        this.testFile = testFile; // from App.testFile
        this.megaByteSize = megaByteSize; // from App.MEGABYTE
        this.ui = ui;
    }

    @Override
    public DiskRun execute() {
        int blockSize = blockSizeKb * 1024;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) blockArr[b] = (byte) 0xFF;
        }

        int wUnitsComplete = 0;
        int unitsTotal = numBlocks * numMarks;
        float percentComplete;

        DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, sequence);
        run.setNumMarks(numMarks);
        run.setNumBlocks(numBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize((long) blockSizeKb * numBlocks * numMarks);
        run.setDiskInfo(Util.getDiskInfo(dataDir)); // replaced App.dataDir

        ui.displayInfo("Disk Info: " + run.getDiskInfo());

        for (int m = 0; m < numMarks; m++) {
            File currentTestFile;
            if (multiFile) {
                currentTestFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata" + m + ".jdm"); // replaced App.dataDir
            } else {
                currentTestFile = testFile; // replaced App.testFile
            }

            DiskMark wMark = new DiskMark(DiskMark.MarkType.WRITE);
            wMark.setMarkNum(m + 1);
            long startTime = System.nanoTime();
            long totalBytesWritten = 0;

            String mode = writeSyncEnable ? "rwd" : "rw"; // replaced App.writeSyncEnable

            try (RandomAccessFile raf = new RandomAccessFile(currentTestFile, mode)) {
                for (int b = 0; b < numBlocks; b++) {
                    if (sequence == DiskRun.BlockSequence.RANDOM) {
                        int rLoc = Util.randInt(0, numBlocks - 1);
                        raf.seek((long) rLoc * blockSize);
                    } else {
                        raf.seek((long) b * blockSize);
                    }
                    raf.write(blockArr, 0, blockSize);
                    totalBytesWritten += blockSize;

                    wUnitsComplete++;
                    percentComplete = (float) wUnitsComplete / unitsTotal * 100f;
                    ui.updateProgress((int) percentComplete);
                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).severe("Write error: " + e.getMessage());
            }

            long endTime = System.nanoTime();
            double sec = (endTime - startTime) / 1_000_000_000.0;
            double mbWritten = (double) totalBytesWritten / megaByteSize; // replaced App.MEGABYTE
            wMark.setBwMbSec(mbWritten / sec);

            edu.touro.mco152.bm.App.updateMetrics(wMark); // App.* is OK here since updateMetrics is a utility method
            ui.displayInfo("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s");

            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
        }

        run.setEndTime(new Date());
        return run;
    }
}

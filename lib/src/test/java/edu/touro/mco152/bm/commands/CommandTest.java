package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.BenchmarkUIHandler;
import edu.touro.mco152.bm.ConsoleUIHandler;
import edu.touro.mco152.bm.persist.DiskRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    // Benchmark parameters
    private final int numMarks = 25;
    private final int numBlocks = 128;
    private final int blockSizeKb = 2048;
    private final DiskRun.BlockSequence sequence = DiskRun.BlockSequence.SEQUENTIAL;
    private final File dataDir = new File(System.getProperty("java.io.tmpdir"));
    private final boolean writeSyncEnable = false;
    private final boolean multiFile = true;
    private final File testFile = new File(dataDir, "testdata.jdm");
    private final int megaByteSize = 1024 * 1024;

    private BenchmarkUIHandler ui;

    @BeforeEach
    void setUp() {
        ui = new ConsoleUIHandler();
    }

    @Test
    void testWriteCommand() {
        WriteCommand writeCommand = new WriteCommand(
                numMarks, numBlocks, blockSizeKb,
                sequence, dataDir, writeSyncEnable,
                multiFile, testFile, megaByteSize, ui
        );

        DiskRun run = writeCommand.execute();
        assertNotNull(run, "WriteCommand returned null run");
        assertEquals(DiskRun.IOMode.WRITE, run.getIoMode(), "Run mode should be WRITE");
    }

    @Test
    void testReadCommand() {
        ReadCommand readCommand = new ReadCommand(
                numMarks, numBlocks, blockSizeKb,
                sequence, dataDir, multiFile,
                testFile, megaByteSize, ui
        );

        DiskRun run = readCommand.execute();
        assertNotNull(run, "ReadCommand returned null run");
        assertEquals(DiskRun.IOMode.READ, run.getIoMode(), "Run mode should be READ");
    }
}

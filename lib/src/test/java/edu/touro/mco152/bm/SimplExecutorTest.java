package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commands.ReadCommand;
import edu.touro.mco152.bm.commands.SimpleExecutor;
import edu.touro.mco152.bm.observer.Observer;
import edu.touro.mco152.bm.persist.DiskRun;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * Unit tests for verifying the SimpleExecutor's observer behavior.
 */
public class SimplExecutorTest {
    /**
     * Verifies that an observer is correctly notified when a ReadCommand completes.
     * Uses TestObserver to confirm the update method is called.
     */
    @Test
    public void testObserverIsCalledAfterReadCommand() {
        // Arrange
        AtomicBoolean wasCalled = new AtomicBoolean(false);

        Observer testObserver = run -> wasCalled.set(true);
        SimpleExecutor executor = new SimpleExecutor();
        executor.registerObserver(testObserver);

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File testFile = new File(tmpDir, "testfile.jdm");

        ReadCommand readCommand = new ReadCommand(
                1,                          // numMarks
                1,                          // numBlocks
                512,                        // blockSizeKb
                DiskRun.BlockSequence.SEQUENTIAL,
                tmpDir,                    // dataDir
                false,                     // multiFile
                testFile,                  // testFile
                1024 * 1024,               // megaByteSize (1MB)
                new ConsoleUIHandler()     // ui
        );

        // Act
        executor.executeAll(List.of(readCommand));

        // Assert
        assertTrue(wasCalled.get(), "Observer should have been notified after benchmark execution");
    }
}

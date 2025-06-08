package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commands.*;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.testutil.TestObserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObserverPatternTest {

    @BeforeAll
    public static void setup() {
        TestObserver.reset();

        App.dataDir = new File(System.getProperty("java.io.tmpdir"));
        App.testFile = new File(App.dataDir, "testfile.jdm");
    }

    @Test
    public void testObserverIsCalled() {
        // Use the built-in console handler
        ReadCommand readCommand = new ReadCommand(
                1, 1, 4,
                DiskRun.BlockSequence.SEQUENTIAL,
                App.dataDir,
                false,  App.testFile,
                1024,
                new ConsoleUIHandler()
        );

        SimpleExecutor executor = new SimpleExecutor();
        executor.registerObserver(new TestObserver());

        executor.executeAll(List.of(readCommand));

        assertTrue(TestObserver.wasCalled, "Observer should have been called");
    }



}

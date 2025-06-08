package edu.touro.mco152.bm.testutil;

import edu.touro.mco152.bm.observer.Observer;
import edu.touro.mco152.bm.persist.DiskRun;

public class TestObserver implements Observer {
    public static boolean wasCalled = false;

    @Override
    public void update(DiskRun run) {
        wasCalled = true;
    }

    public static void reset() {
        wasCalled = false;
    }
}

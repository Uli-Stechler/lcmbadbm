package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Command interface for executing benchmark actions.
 */
public interface Command {
    DiskRun execute();
}

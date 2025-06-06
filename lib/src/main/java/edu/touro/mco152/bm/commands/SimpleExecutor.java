package edu.touro.mco152.bm.commands;

import java.util.List;

/**
 * Executes a list of benchmark commands in sequence.
 */
public class SimpleExecutor {

    public void executeAll(List<Command> commands) {
        for (Command cmd : commands) {
            cmd.execute();
        }
    }
}

package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.observer.Subject;
import edu.touro.mco152.bm.observer.Observer;
import edu.touro.mco152.bm.persist.DiskRun;

import java.util.ArrayList;
import java.util.List;
/**
 * Executes a list of benchmark commands in sequence.
 */
public class SimpleExecutor implements Subject  {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(DiskRun run) {
        for (Observer o : observers) {
            o.update(run);
        }
    }

    public void executeAll(List<Command> commands) {
        for (Command cmd : commands) {
            DiskRun run = cmd.execute();
            notifyObservers(run);
            cmd.execute();
        }
    }
}

package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.externalsys.SlackManager;
/**
 * An observer that checks for anomalies in read benchmark results and sends a Slack
 * message if the maximum time of a benchmark iteration exceeds 3% of the average time.
 * This helps the Hardware Quality Manager monitor disk performance consistency.
 */
public class RulesEngineObserver implements Observer {

    private final SlackManager slack = new SlackManager("BadBM");
    /**
     * Called by the Subject (SimpleExecutor) after a benchmark command is executed.
     * If the benchmark was a READ command and max time exceeds 3% of avg time,
     * sends a notification to the "mco152_auto_notifications" Slack channel.
     *
     * @param run the DiskRun object containing benchmark results.
     */
    @Override
    public void update(DiskRun run) {
        System.out.println("[DEBUG] RulesEngineObserver called");
        System.out.println("Max = " + run.getRunMax() + ", Avg = " + run.getRunAvg());
        if (run.getIoMode() == DiskRun.IOMode.READ) {
            double avg = run.getRunAvg();
            double max = run.getRunMax();
            if (max > avg * 1.03) {
                String message = String.format(
                        "The read benchmark result has an iteration 'max time' (%.4f) that exceeds 3 per cent of the benchmark's average time (%.4f).",
                        max, avg
                );
                slack.postMsg2OurChannel(message);
            }
        }
    }
}

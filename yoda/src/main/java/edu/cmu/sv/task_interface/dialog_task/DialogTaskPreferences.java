package edu.cmu.sv.task_interface.dialog_task;

/**
 * Created by David Cohen on 9/3/14.
 */
public class DialogTaskPreferences {
    // all the rewards and penalties are given as positive numbers,
    // penalties are converted to negative numbers for decision-making
    public double penaltyForDelay;
    public double rewardForCorrectExecution;
    public double penaltyForIncorrectExecution;

    public DialogTaskPreferences(double penaltyForDelay, double rewardForCorrectExecution, double penaltyForIncorrectExecution) {
        this.penaltyForDelay = penaltyForDelay;
        this.rewardForCorrectExecution = rewardForCorrectExecution;
        this.penaltyForIncorrectExecution = penaltyForIncorrectExecution;
    }
}

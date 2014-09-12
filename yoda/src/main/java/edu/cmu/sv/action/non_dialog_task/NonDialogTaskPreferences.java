package edu.cmu.sv.action.non_dialog_task;

import java.util.Set;

/**
 * Created by David Cohen on 8/27/14.
 */
public class NonDialogTaskPreferences {
    // if explicit confirmation is always required before the task is executed, set alwaysRequireConfirmation to true
    public boolean alwaysRequireConfirmation;

    // all the rewards and penalties are given as positive numbers,penalties are converted to negative numbers for
    // decision-making
    public double penaltyForDelay;
    public double rewardForCorrectExecution;
    public double penaltyForIncorrectExecution;

    // define the slots that are required to be defined by the user before execution is possible
    public Set<String> slotsRequired;

    public NonDialogTaskPreferences(boolean alwaysRequireConfirmation, double penaltyForDelay, double rewardForCorrectExecution, double penaltyForIncorrectExecution, Set<String> slotsRequired) {
        this.alwaysRequireConfirmation = alwaysRequireConfirmation;
        this.penaltyForDelay = penaltyForDelay;
        this.rewardForCorrectExecution = rewardForCorrectExecution;
        this.penaltyForIncorrectExecution = penaltyForIncorrectExecution;
        this.slotsRequired = slotsRequired;
    }
}

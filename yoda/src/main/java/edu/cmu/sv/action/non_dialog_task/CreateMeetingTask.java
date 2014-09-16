package edu.cmu.sv.action.non_dialog_task;

import edu.cmu.sv.semantics.SemanticsModel;

import java.util.*;

/**
 * Created by David Cohen on 8/28/14.
 */
public class CreateMeetingTask implements NonDialogTask {
    private static Integer instanceCounter = 0;
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences =
            new NonDialogTaskPreferences(false, 1, 20, 15,
                    new HashSet<>(Arrays.asList()));
    private SemanticsModel taskSpec = null;

    @Override
    public NonDialogTaskPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void setTaskSpec(SemanticsModel taskSpec) {
        this.taskSpec = taskSpec;
    }

    @Override
    public double assessExecutability() {
        if (taskSpec==null)
            return 0;
        if (taskSpec.getSlotPathFiller("action")!=null &&
                taskSpec.getSlotPathFiller("action").equals("Create") &&
                taskSpec.getSlotPathFiller("theme.class")!=null &&
                taskSpec.getSlotPathFiller("theme.class").equals("Meeting"))
            return 1.0;
        return 0;
    }

    @Override
    public SemanticsModel getTaskSpec() {
        return taskSpec;
    }

    @Override
    public String execute(SemanticsModel taskSpec) {
        System.out.println("Executing task: create meeting");
        System.out.println(taskSpec);
        String ans = "CreateMeetingTask:"+instanceCounter.toString();
        instanceCounter += 1;
        // There is no real implementation, so we just set the status to successfully completed
        executionStatus.put(ans, TaskStatus.SUCCESSFULLY_COMPLETED);
        System.out.println("taskID: "+ans);
        return ans;
    }

    @Override
    public TaskStatus status(String taskID) {
        return executionStatus.get(taskID);
    }
}

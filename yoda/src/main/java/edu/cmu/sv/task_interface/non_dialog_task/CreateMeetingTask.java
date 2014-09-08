package edu.cmu.sv.task_interface.non_dialog_task;

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
                    new HashSet<>(Arrays.asList("hasMeeting.hasTime", "hasMeeting.hasPlace", "hasMeeting.hasPerson")));

    @Override
    public NonDialogTaskPreferences getPreferences() {
        return preferences;
    }

    @Override
    public double assessExecutability(SemanticsModel taskSpec) {
        return 1.0;
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

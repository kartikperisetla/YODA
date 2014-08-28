package edu.cmu.sv.task_interface;

import edu.cmu.sv.semantics.SemanticsModel;

import java.util.*;

/**
 * Created by David Cohen on 8/28/14.
 */
public class SendEmailTask implements NonDialogTask {
    private static Integer instanceCounter = 0;
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences = new NonDialogTaskPreferences(true, 1, 20, 20,
            new HashSet<>(Arrays.asList("hasToPerson")));

    @Override
    public NonDialogTaskPreferences getPreferences() {
        return preferences;
    }

    @Override
    public double assessExecutability(SemanticsModel taskSpec) {
        // this works because assessExecutability is never called unless all the required slots are present
        return 1.0;
    }

    @Override
    public String execute(SemanticsModel taskSpec) {
        System.out.println("Executing task: send email");
        System.out.println(taskSpec);
        String ans = "SendEmailTask:"+instanceCounter.toString();
        instanceCounter += 1;
        // There is no real implementation, so we just set the status to successfully completed
        executionStatus.put(ans, TaskStatus.SUCCESSFULLY_COMPLETED);
        // In the envisioned implementation, a separate dictation program would run
        // while the other program is run, the dialog manager should block, as in the following commented lines
        // executionStatus.put(ans, TaskStatus.CURRENTLY_EXECUTING_BLOCKING);
        // dictationProgram.run();
        System.out.println("taskID: "+ans);
        return ans;
    }

    @Override
    public TaskStatus status(String taskID) {
        return executionStatus.get(taskID);
    }
}

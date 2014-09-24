package edu.cmu.sv.system_action.non_dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.semantics.SemanticsModel;

import java.util.*;

/**
 * Created by David Cohen on 8/28/14.
 */
public class SendEmailTask extends NonDialogTask {
    private static Integer instanceCounter = 0;
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences = new NonDialogTaskPreferences(true, 1, 20, 20,
            new HashSet<>(Arrays.asList()));

    public SendEmailTask(Database db) {
        this.db = db;
    }

    @Override
    public NonDialogTaskPreferences getPreferences() {
        return preferences;
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
    public double assessExecutability() {
        if (taskSpec==null)
            return 0;
        if (taskSpec.getSlotPathFiller("action")!=null &&
                taskSpec.getSlotPathFiller("action").equals("Send") &&
                taskSpec.getSlotPathFiller("theme.class")!=null &&
                taskSpec.getSlotPathFiller("theme.class").equals("Email") &&
                taskSpec.getSlotPathFiller("recipient")!=null)
            return 1.0;
        return 0;
    }

    @Override
    public TaskStatus status(String taskID) {
        return executionStatus.get(taskID);
    }
}

package edu.cmu.sv.system_action.non_dialog_task;

import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by David Cohen on 12/19/14.
 */
public class MakeReservationTask extends NonDialogTask {
    private static Integer instanceCounter = 0;
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences =
            new NonDialogTaskPreferences(false, 1, 20, 15,
                    new HashSet<>(Arrays.asList()));

    @Override
    public NonDialogTaskPreferences getPreferences() {
        return preferences;
    }


    @Override
    public double assessExecutability() {
        return 0.0;
    }

    @Override
    public JSONObject getTaskSpec() {
        return taskSpec;
    }

    @Override
    public TaskStatus status(String taskID) {
        return executionStatus.get(taskID);
    }
}

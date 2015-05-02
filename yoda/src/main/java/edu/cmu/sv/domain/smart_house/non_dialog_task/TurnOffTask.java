package edu.cmu.sv.domain.smart_house.non_dialog_task;

import edu.cmu.sv.domain.smart_house.GUI.GUIElectronic;
import edu.cmu.sv.domain.smart_house.GUI.GUIThing;
import edu.cmu.sv.domain.smart_house.GUI.Simulator;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by David Cohen on 12/19/14.
 */
public class TurnOffTask extends NonDialogTask {
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences =
            new NonDialogTaskPreferences(false, 1, 20, 15,
                    new HashSet<>(Arrays.asList()));


    @Override
    public void execute(YodaEnvironment yodaEnvironment) {
        super.execute(yodaEnvironment);
        String uri = (String) new SemanticsModel(taskSpec.toJSONString()).newGetSlotPathFiller("Component.HasURI");
        boolean itemFound = false;
        for(GUIThing thing : Simulator.getThings()) {
            if(thing.getCorrespondingURI().equals(uri)) {
                if (thing instanceof GUIElectronic) {
                    if (!((GUIElectronic) thing).getState()) {
                        // do nothing if the electronic piece is already off
                    } else {
                        ((GUIElectronic) thing).toggleSwitch();
                    }
                }
                itemFound = true;
                break;
            }
        }
        if(!itemFound) {
            System.err.println("TurnOffTask.execute(): ERROR: unknown URI:"+uri);
            System.exit(0);
        }
    }

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
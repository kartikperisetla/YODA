package edu.cmu.sv.domain.smart_house.non_dialog_task;

import edu.cmu.sv.domain.smart_house.GUI.GUIRoom;
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
 * Created by dan on 4/13/15.
 */
public class IncreaseTemperatureTask extends NonDialogTask {
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences =
            new NonDialogTaskPreferences(false, 1, 20, 15,
                    new HashSet<>(Arrays.asList()));


    @Override
    public void execute(YodaEnvironment yodaEnvironment) {
        super.execute(yodaEnvironment);
        String uri = (String) new SemanticsModel(taskSpec.toJSONString()).newGetSlotPathFiller("HasRoom.HasURI");
        boolean itemFound = false;
        for(GUIThing thing : Simulator.getThings()) {
            if(thing.getCorrespondingURI().equals(uri)) {
                itemFound = true;
                if (!(thing instanceof GUIRoom))
                    continue;
                ((GUIRoom) thing).setTemperature(86);
                break;
            }
        }
        if(!itemFound) {
            System.out.println("ERROR: unknown URI:"+uri);
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
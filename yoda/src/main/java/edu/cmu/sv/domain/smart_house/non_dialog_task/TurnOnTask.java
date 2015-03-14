package edu.cmu.sv.domain.smart_house.non_dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.domain.smart_house.GUI.Simulator;
import edu.cmu.sv.domain.smart_house.HouseSimulation;
import edu.cmu.sv.domain.smart_house.GUI.GUIThing;
import edu.cmu.sv.domain.smart_house.GUI.GUIElectronic;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by David Cohen on 12/19/14.
 */
public class TurnOnTask extends NonDialogTask {
    private static Integer instanceCounter = 0;
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences =
            new NonDialogTaskPreferences(false, 1, 20, 15,
                    new HashSet<>(Arrays.asList()));


    @Override
    public void execute(YodaEnvironment yodaEnvironment) {

        super.execute(yodaEnvironment);
        String uri = (String) new SemanticsModel(taskSpec.toJSONString()).newGetSlotPathFiller("Component.HasURI");
        for(GUIThing thing : Simulator.getThings()) {
            if(thing.getCorrespondingURI().equals(uri)) {
                ((GUIElectronic)thing).toggleSwitch();
            } else {
                System.out.println("ERROR: unknown URI:"+uri);
                System.exit(0);
            }
        }

//        if (uri.equals(Database.baseURI+"0000")){
//            HouseSimulation.POI_0000_powerState = "on";
//        } else if (uri.equals(Database.baseURI+"0001")){
//            HouseSimulation.POI_0001_powerState = "on";
//        } else {
//            System.out.println("ERROR: unknown URI:"+uri);
//            System.exit(0);
//        }
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

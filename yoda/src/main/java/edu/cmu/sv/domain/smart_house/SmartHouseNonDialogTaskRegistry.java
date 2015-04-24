package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.smart_house.non_dialog_task.CleanRoomTask;
import edu.cmu.sv.domain.smart_house.non_dialog_task.TurnOffTask;
import edu.cmu.sv.domain.smart_house.non_dialog_task.TurnOnTask;
import edu.cmu.sv.domain.smart_house.non_dialog_task.IncreaseTemperatureTask;
import edu.cmu.sv.domain.smart_house.non_dialog_task.DecreaseTemperatureTask;
import edu.cmu.sv.domain.smart_house.ontology.verb.CleanRoom;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOffAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.IncreaseTemperature;
import edu.cmu.sv.domain.smart_house.ontology.verb.DecreaseTemperature;
import edu.cmu.sv.system_action.GenericCommandSchema;

/**
 * Created by David Cohen on 3/4/15.
 */
public class SmartHouseNonDialogTaskRegistry extends NonDialogTaskRegistry {
    public SmartHouseNonDialogTaskRegistry() {
        nonDialogTasks.add(TurnOnTask.class);
        nonDialogTasks.add(TurnOffTask.class);
        nonDialogTasks.add(CleanRoomTask.class);
        nonDialogTasks.add(IncreaseTemperatureTask.class);
        nonDialogTasks.add(DecreaseTemperatureTask.class);

        actionSchemata.add(new GenericCommandSchema(TurnOnAppliance.class, TurnOnTask.class));
        actionSchemata.add(new GenericCommandSchema(TurnOffAppliance.class, TurnOffTask.class));
        actionSchemata.add(new GenericCommandSchema(CleanRoom.class, CleanRoomTask.class));
        actionSchemata.add(new GenericCommandSchema(IncreaseTemperature.class, IncreaseTemperatureTask.class));
        actionSchemata.add(new GenericCommandSchema(DecreaseTemperature.class, DecreaseTemperatureTask.class));

    }
}


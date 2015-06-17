package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.smart_house.non_dialog_task.*;
import edu.cmu.sv.system_action.GenericCommandSchema;

/**
 * Created by David Cohen on 3/4/15.
 */
public class SmartHouseNonDialogTaskRegistry extends NonDialogTaskRegistry {
    public SmartHouseNonDialogTaskRegistry() {
        nonDialogTasks.add(TurnOnTask.class);
        nonDialogTasks.add(TurnOffTask.class);
        nonDialogTasks.add(CleanRoomTask.class);

        actionSchemata.add(new GenericCommandSchema(SmartHouseOntologyRegistry.turnOnAppliance, TurnOnTask.class));
        actionSchemata.add(new GenericCommandSchema(SmartHouseOntologyRegistry.turnOffAppliance, TurnOffTask.class));
        actionSchemata.add(new GenericCommandSchema(SmartHouseOntologyRegistry.cleanRoom, CleanRoomTask.class));
    }
}


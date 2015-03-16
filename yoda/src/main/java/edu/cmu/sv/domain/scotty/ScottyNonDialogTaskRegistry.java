package edu.cmu.sv.domain.scotty;

import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.smart_house.non_dialog_task.TurnOnTask;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.domain.yelp_phoenix.non_dialog_task.GiveDirectionsTask;
import edu.cmu.sv.domain.yelp_phoenix.non_dialog_task.MakeReservationTask;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.GiveDirections;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.MakeReservation;
import edu.cmu.sv.system_action.GenericCommandSchema;

/**
 * Created by David Cohen on 3/4/15.
 */
public class ScottyNonDialogTaskRegistry extends NonDialogTaskRegistry {
    public ScottyNonDialogTaskRegistry() {
        nonDialogTasks.add(GiveDirectionsTask.class);
        actionSchemata.add(new GenericCommandSchema(GiveDirections.class, GiveDirectionsTask.class));
    }
}


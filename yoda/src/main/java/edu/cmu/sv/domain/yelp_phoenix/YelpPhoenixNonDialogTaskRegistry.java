package edu.cmu.sv.domain.yelp_phoenix;

import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.GiveDirections;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.MakeReservation;
import edu.cmu.sv.system_action.GenericCommandSchema;
import edu.cmu.sv.domain.yelp_phoenix.non_dialog_task.GiveDirectionsTask;
import edu.cmu.sv.domain.yelp_phoenix.non_dialog_task.MakeReservationTask;

/**
 * Created by David Cohen on 3/4/15.
 */
public class YelpPhoenixNonDialogTaskRegistry extends NonDialogTaskRegistry {
    public YelpPhoenixNonDialogTaskRegistry() {
        nonDialogTasks.add(GiveDirectionsTask.class);
        nonDialogTasks.add(MakeReservationTask.class);
        actionSchemata.add(new GenericCommandSchema(GiveDirections.class, GiveDirectionsTask.class));
        actionSchemata.add(new GenericCommandSchema(MakeReservation.class, MakeReservationTask.class));
    }
}


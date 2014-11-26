package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestVerbRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestVerb;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 9/3/14.
 *
 * A DialogTask defines a reference resolution and analysis procedure for different dialogAct/verb combinations
 */
public abstract class DialogTask{
    SemanticsModel taskSpec;

    public void setTaskSpec(SemanticsModel taskSpec){
        this.taskSpec = taskSpec;
    }
    public SemanticsModel getTaskSpec(){
        return taskSpec;
    }

    public abstract DiscourseUnit2.GroundedDiscourseUnitHypotheses ground(
            DiscourseUnit2.DiscourseUnitHypothesis hypothesis, YodaEnvironment yodaEnvironment);
    public abstract void analyse(
            DiscourseUnit2.GroundedDiscourseUnitHypotheses groundedHypothesis, YodaEnvironment yodaEnvironment);
}

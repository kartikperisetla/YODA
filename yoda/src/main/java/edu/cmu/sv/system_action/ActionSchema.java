package edu.cmu.sv.system_action;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 12/19/14.
 *
 * The ActionSchema class relates the content of a discourse unit to a system action task spec
 */
public abstract class ActionSchema {
    /*
    * Return weather or not this action schema matches with the provided grounded meaning
    * */
    public abstract boolean matchSchema(SemanticsModel resolvedMeaning);
    /*
    * Use this schema to map a discourse unit's grounded meaning to a task speck JSON object
    * */
    public abstract NonDialogTask applySchema(SemanticsModel resolvedMeaning);
}

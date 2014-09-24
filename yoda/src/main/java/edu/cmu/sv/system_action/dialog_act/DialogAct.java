package edu.cmu.sv.system_action.dialog_act;

import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;

import java.util.*;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Define the illocutionary acts for the YODA dialog system
 *
 * Define information to support decision-making by the dialog manager
 *
 * Possibly in the future:
 *   - include templates used for NLG and SLU
 *
 */
public interface DialogAct extends SystemAction {

    public Double reward(DiscourseUnit DU);
    public Double cost(DiscourseUnit DU);
    public Map<String, String> getParameters();
    public Map<String, String> getBindings();
    public DialogAct bindVariables(Map<String, String> bindings);

}

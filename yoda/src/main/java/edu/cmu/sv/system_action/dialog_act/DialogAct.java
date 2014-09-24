package edu.cmu.sv.system_action.dialog_act;

import edu.cmu.sv.database.Database;
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
public abstract class DialogAct extends SystemAction {
    public abstract Double reward(DiscourseUnit DU);
    public abstract Double cost(DiscourseUnit DU);
    public abstract Map<String, String> getParameters();
    public abstract Map<String, String> getBindings();
    public abstract DialogAct bindVariables(Map<String, String> bindings);

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+ "{" +
                "boundVariables=" + getBindings() +
                '}';
    }

}

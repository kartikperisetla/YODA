package edu.cmu.sv.system_action;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;

import java.util.HashSet;

/**
 * Created by David Cohen on 9/11/14.
 */
public abstract class SystemAction {
    protected Database db;
    /**
     * "this" is the ground truth correct action
     * evaluationMatch returns true if other is an acceptable match
     * for the purposes of some current evaluation
     */
    public boolean evaluationMatch(SystemAction other){
        if (!this.getClass().equals(other.getClass()))
            return false;
        if (this instanceof DialogAct) {
            DialogAct da1 = (DialogAct) this;
            DialogAct da2 = (DialogAct) other;
            for (String key : da1.getIndividualParameters().keySet()){
                if (!da1.getBoundIndividuals().get(key).equals(da2.getBoundIndividuals().get(key)))
                    return false;
            }
            for (String key : da1.getClassParameters().keySet()){
                if (!da1.getBoundClasses().get(key).equals(da2.getBoundClasses().get(key)))
                    return false;
            }
            for (String key : da1.getDescriptionParameters().keySet()){
                if (!da1.getBoundDescriptions().get(key).equals(da2.getBoundDescriptions().get(key)))
                    return false;
            }
            for (String key : da1.getPathParameters().keySet()){
                if (!da1.getBoundPaths().get(key).equals(da2.getBoundPaths().get(key)))
                    return false;
            }
            return true;
        }
        if (this instanceof NonDialogTask)
            return ((NonDialogTask) this).getTaskSpec().equals(((NonDialogTask) other).getTaskSpec());
        return false;
    }
}

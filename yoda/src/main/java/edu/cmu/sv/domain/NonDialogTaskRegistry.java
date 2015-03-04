package edu.cmu.sv.domain;

import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/4/15.
 */
public class NonDialogTaskRegistry {
    public static Set<Class< ? extends NonDialogTask>> nonDialogTasks = new HashSet<>();
    public static Set<ActionSchema> actionSchemata = new HashSet<>();

}

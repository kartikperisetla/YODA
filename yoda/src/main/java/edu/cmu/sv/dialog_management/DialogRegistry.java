package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.system_action.dialog_act.*;
import edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts.*;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Command;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.WHQuestion;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.system_action.dialog_task.DialogTask;
import edu.cmu.sv.system_action.dialog_task.RespondToWHQuestionTask;
import edu.cmu.sv.system_action.dialog_task.RespondToYNQuestionTask;
import edu.cmu.sv.system_action.non_dialog_task.CreateMeetingTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.SendEmailTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/8/14.
 *
 * This class specifies which dialog acts are available to the system,
 * and how they relate to dialog and non-dialog tasks.
 *
 */
public class DialogRegistry {
    // map from string identifier to dialog act
    public static Map<String, Class <? extends DialogAct>> dialogActNameMap = new HashMap<>();

    // the set of clarification dialog acts available to the system
    public static Set<Class <? extends DialogAct>> clarificationDialogActs = new HashSet<>();

    // the full set of discourse unit dialog act types handled by the system
    public static Set<Class <? extends DialogAct>> discourseUnitDialogActs = new HashSet<>();

    // map NDU to the classes that handle the corresponding dialog tasks
    public static Map<Class <? extends DialogAct>, Set<Class <? extends DialogTask>>>
            dialogTaskRegistry = new HashMap<>();

    // map dialog acts to the classes that handle the corresponding non-dialog tasks
    public static Map<Class <? extends DialogAct>, Set<Class <? extends NonDialogTask>>>
            nonDialogTaskRegistry = new HashMap<>();

    static{
        clarificationDialogActs.add(RequestConfirmRole.class);
        clarificationDialogActs.add(RequestConfirmValue.class);
        clarificationDialogActs.add(RequestDisambiguateRole.class);
        clarificationDialogActs.add(RequestDisambiguateValue.class);
        clarificationDialogActs.add(RequestRephrase.class);

        discourseUnitDialogActs.add(WHQuestion.class);
        discourseUnitDialogActs.add(YNQuestion.class);
        discourseUnitDialogActs.add(Command.class);

        dialogTaskRegistry.put(WHQuestion.class, new HashSet<>());
        dialogTaskRegistry.get(WHQuestion.class).add(RespondToWHQuestionTask.class);
        dialogTaskRegistry.put(YNQuestion.class, new HashSet<>());
        dialogTaskRegistry.get(YNQuestion.class).add(RespondToYNQuestionTask.class);

        nonDialogTaskRegistry.put(Command.class, new HashSet<>());
        nonDialogTaskRegistry.get(Command.class).add(CreateMeetingTask.class);
        nonDialogTaskRegistry.get(Command.class).add(SendEmailTask.class);

        for (Class<? extends DialogAct> cls : Iterables.concat(discourseUnitDialogActs,
                clarificationDialogActs)) {
            dialogActNameMap.put(cls.getSimpleName(), cls);
        }

    }

}

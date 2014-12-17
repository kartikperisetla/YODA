package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.system_action.dialog_act.*;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ClarificationDialogAct;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ConfirmValueSuggestion;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
import edu.cmu.sv.system_action.non_dialog_task.CreateMeetingTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.SendEmailTask;

import java.util.*;

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

    public static Set<Class <? extends ClarificationDialogAct>> clarificationDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> argumentationDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> slotFillingDialogActs = new HashSet<>();

    public static Set<Class <? extends DialogAct>> discourseUnitDialogActs = new HashSet<>();

    // map dialog acts to the classes that handle the corresponding non-dialog tasks
    public static Map<Class <? extends DialogAct>, Set<Class <? extends NonDialogTask>>>
            nonDialogTaskRegistry = new HashMap<>();

    static{
        clarificationDialogActs.add(ConfirmValueSuggestion.class);
        clarificationDialogActs.add(RequestConfirmValue.class);

        argumentationDialogActs.add(Accept.class);
        argumentationDialogActs.add(Reject.class);
        argumentationDialogActs.add(DontKnow.class);

        slotFillingDialogActs.add(RequestRole.class);

        discourseUnitDialogActs.add(WHQuestion.class);
        discourseUnitDialogActs.add(YNQuestion.class);
        discourseUnitDialogActs.add(Command.class);

        nonDialogTaskRegistry.put(Command.class, new HashSet<>());
        nonDialogTaskRegistry.get(Command.class).add(CreateMeetingTask.class);
        nonDialogTaskRegistry.get(Command.class).add(SendEmailTask.class);

        for (Class<? extends DialogAct> cls : Iterables.concat(discourseUnitDialogActs, argumentationDialogActs,
                clarificationDialogActs, Arrays.asList(Fragment.class))) {
            dialogActNameMap.put(cls.getSimpleName(), cls);
        }

    }

}

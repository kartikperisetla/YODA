package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.*;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ClarificationDialogAct;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ConfirmValueSuggestion;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestFixMisunderstanding;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRoleGivenRole;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;

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
    public static Map<String, Class <? extends SystemAction>> actionNameMap = new HashMap<>();

    public static Set<Class <? extends ClarificationDialogAct>> clarificationDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> userOnlyDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> argumentationDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> simpleDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> slotFillingDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> discourseUnitDialogActs = new HashSet<>();
    public static Set<Class <? extends DialogAct>> oocDialogActs= new HashSet<>();
    public static Set<Class <? extends DialogAct>> oocResponseDialogActs= new HashSet<>();
    public static Set<Class< ? extends NonDialogTask>> nonDialogTasks = new HashSet<>();

    public static Set<ActionSchema> actionSchemata = new HashSet<>();

    static{
        clarificationDialogActs.add(ConfirmValueSuggestion.class);
        clarificationDialogActs.add(RequestConfirmValue.class);

        argumentationDialogActs.add(DontKnow.class);
        argumentationDialogActs.add(Statement.class);
        argumentationDialogActs.add(SearchReturnedNothing.class);

        simpleDialogActs.add(RequestFixMisunderstanding.class);
        simpleDialogActs.add(InformDialogLost.class);

        slotFillingDialogActs.add(RequestRoleGivenRole.class);
        slotFillingDialogActs.add(RequestRole.class);

        discourseUnitDialogActs.add(WHQuestion.class);
        discourseUnitDialogActs.add(YNQuestion.class);
        discourseUnitDialogActs.add(Command.class);

        userOnlyDialogActs.add(Accept.class);
        userOnlyDialogActs.add(Reject.class);

        oocDialogActs.add(RequestListOptions.class);
        oocDialogActs.add(RequestSearchAlternative.class);

        oocResponseDialogActs.add(OOCRespondToRequestListOptions.class);
        oocResponseDialogActs.add(OOCRespondToRequestSearchAlternative.class);

        for (Class<? extends DialogAct> cls : Iterables.concat(discourseUnitDialogActs, argumentationDialogActs,
                userOnlyDialogActs, clarificationDialogActs, simpleDialogActs, oocDialogActs, oocResponseDialogActs,
                Arrays.asList(Fragment.class))) {
            dialogActNameMap.put(cls.getSimpleName(), cls);
        }

    }

    public static void registerNonDialogTasks(NonDialogTaskRegistry registry){
        nonDialogTasks.addAll(registry.nonDialogTasks);
        actionSchemata.addAll(registry.actionSchemata);
    }

    public static void finalizeDialogRegistry(){
        for (Class<? extends SystemAction> cls : Iterables.concat(discourseUnitDialogActs, argumentationDialogActs,
                userOnlyDialogActs, clarificationDialogActs, Arrays.asList(Fragment.class), nonDialogTasks)) {
            actionNameMap.put(cls.getSimpleName(), cls);
        }
    }
}

package edu.cmu.sv.dialog_management;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.*;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.utils.HypothesisSetManagement;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Contains a dialog state tracker and specification of interfaces, etc.
 * Contains functions for assessing potential dialog moves.
 * Contains a main method which is the dialog agent loop.
 *
 */
public class DialogManager {
    YodaEnvironment yodaEnvironment;

    public YodaEnvironment getYodaEnvironment() {
        return yodaEnvironment;
    }

    public void setYodaEnvironment(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public DialogManager(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    /*
    * Select the best dialog act given all the possible classes and bindings
    *
    * */
    public List<Pair<SystemAction, Double>> selectAction() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        DiscourseUnit2 DU = yodaEnvironment.dst.getDiscourseUnit();
        Map<SystemAction, Double> actionExpectedReward = new HashMap<>();

        //// Enumerate and evaluate sense clarification acts
        for (Class <? extends DialogAct> daClass : DialogRegistry.senseClarificationDialogActs){
//            System.out.println("Enumerating and evaluating actions of class: "+daClass.getSimpleName());
            Map<String, Set<Object>> possibleBindingsPerVariable = new HashMap<>();
            Map<String, Class<? extends Thing>> parameters = daClass.newInstance().getParameters();
            Map<String, DiscourseUnit2.DialogStateHypothesis> dialogStateHypothesisMap = DU.getHypotheses();

            // Collect matches
            for (String dialogStateHypothesisID : dialogStateHypothesisMap.keySet()){
                for (String parameter : parameters.keySet()){
                    if (!possibleBindingsPerVariable.containsKey(parameter))
                        possibleBindingsPerVariable.put(parameter, new HashSet<>());
                    SemanticsModel spokenByThem = dialogStateHypothesisMap.get(dialogStateHypothesisID).getSpokenByThem();
                    for (String path: spokenByThem.findAllPathsToClass(parameters.get(parameter).getSimpleName())){
                        possibleBindingsPerVariable.get(parameter).add(spokenByThem.newGetSlotPathFiller(path));
                    }
                }
            }

//            System.out.println("DialogManager: possibleBindingsPerVariable: "+possibleBindingsPerVariable);

            // create an action and evaluate reward for each possible binding
            for (Map<String, Object> binding : Combination.possibleBindings(possibleBindingsPerVariable)) {
                DialogAct dialogAct = daClass.newInstance();
                dialogAct.bindVariables(binding);
                Double expectedReward = dialogAct.reward(DU);
                Double expectedCost = dialogAct.cost(DU);
                actionExpectedReward.put(dialogAct, expectedReward - expectedCost);
            }

        }

        return HypothesisSetManagement.keepNBestBeam(actionExpectedReward, 10000);
    }



}

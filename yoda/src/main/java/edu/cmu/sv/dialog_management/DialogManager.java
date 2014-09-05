package edu.cmu.sv.dialog_management;

import edu.cmu.sv.task_interface.DialogTask;
import edu.cmu.sv.task_interface.DialogTaskPreferences;
import edu.cmu.sv.task_interface.WHQuestionTask;
import edu.cmu.sv.task_interface.YNQuestionTask;
import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.utils.NBest;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Contains a dialog state tracker and specification of interfaces, etc.
 * Contains functions for assessing potential dialog moves.
 * Contains a main method which is the dialog agent loop.
 *
 */
public class DialogManager {
    private DialogStateTracker tracker;
    // map dialog acts to the classes that handle the corresponding dialog tasks
    private static Map<String, DialogTask> dialogActTaskRegistry = new HashMap<>();

    static{
        dialogActTaskRegistry.put("YNQuestion", new YNQuestionTask());
        dialogActTaskRegistry.put("WHQuestion", new WHQuestionTask());
    }

    public DialogManager() {
        tracker = new DialogStateTracker();
    }

    public DialogStateTracker getTracker() {
        return tracker;
    }

    public List<Pair<Pair<DialogAct.DA_TYPE, Map<String, String>>, Double>> evaluateClarificationActions(){
        Set<DiscourseUnit> discourseUnits = tracker.getDiscourseUnits();

        for (DiscourseUnit DU : discourseUnits) {
            // 1) collect roles and values across this DU
            Map<String, Set<String>> roleValuePairs = DU.getAllSlotValuePairs();

            // 1-a) determine the set of values
            Set<String> values = new HashSet<>();
            for (String role : roleValuePairs.keySet()){
                values.addAll(roleValuePairs.get(role));
            }

            // 2) create a dialog act descriptor for each possible dialog act
            Set<Pair<DialogAct.DA_TYPE, Map<String, String>>> descriptors = new HashSet<>();
            for (DialogAct.DA_TYPE daType : DialogAct.dialogActContentSpec.keySet()){
//                System.out.println("daType:"+daType);
                Map<String, String> parameters = DialogAct.dialogActContentSpec.get(daType);
//                System.out.println("parameters:"+parameters);
                Map<String, Set<String>> updatedParameters = new HashMap<>();
                for (String key : parameters.keySet()){
                    if (parameters.get(key).equals("value"))
                        updatedParameters.put(key, values);
                    else if (parameters.get(key).equals("role"))
                        updatedParameters.put(key, roleValuePairs.keySet());
                    else
                        throw new Error("unsupported parameter type for dialog act descriptor");
                }
                descriptors.addAll(Combination.possibleBindings(updatedParameters).stream().
                        filter(x -> new HashSet<>(x.values()).size()==x.size()).
                        map(binding -> new ImmutablePair<>(daType, binding)).
                        collect(Collectors.toList()));
            }
//            System.out.println("DialogManager.evaluateClarificationActions: descriptors:\n"+descriptors);

            // 3) for each dialog act descriptor, evaluate expected reward
            Map<Pair<DialogAct.DA_TYPE, Map<String, String>>, Double> descriptorExpectedReward = new HashMap<>();
            StringDistribution dialogActDistribution = DU.marginalSlotPathDistribution("dialogAct");
            for (Pair<DialogAct.DA_TYPE, Map<String, String>> descriptor : descriptors){
                Double predictedConfidence = DU.predictJointConfidenceAfterClarification(descriptor);
                descriptorExpectedReward.put(descriptor,
                        dialogActTaskExpectedReward(predictedConfidence, dialogActDistribution, true));
            }

            Double doNothingReward = dialogActTaskExpectedReward(
                    DU.hypothesisDistribution.get(
                            DU.hypothesisDistribution.getTopHypothesis()),
                    dialogActDistribution, false);

//            System.out.println("-----");
//            System.out.println("do nothing reward: "+doNothingReward);
//            for (Pair<Pair<DialogAct.DA_TYPE, Map<String, String>>, Double> descriptorRewardPair :
//                    NBest.keepBeam(descriptorExpectedReward, 5)) {
//                System.out.println("-----");
//                System.out.println(descriptorRewardPair.getKey());
//                System.out.println(descriptorRewardPair.getValue());
//            }

            descriptorExpectedReward.put(null, doNothingReward);
            List<Pair<Pair<DialogAct.DA_TYPE, Map<String, String>>, Double>> ans =
                    NBest.keepBeam(descriptorExpectedReward, 5);
            return ans;
        }
        return null;
    }

    Double dialogActTaskExpectedReward(Double confidence, StringDistribution dialogActDistribution, boolean takeTurn){
        Double expectedReward = 0.0;
        for (String dialogAct : dialogActDistribution.keySet()){
            DialogTaskPreferences preferences = dialogActTaskRegistry.get(dialogAct).getPreferences();
            expectedReward += dialogActDistribution.get(dialogAct)*
                    ((preferences.rewardForCorrectExecution * confidence) -
                            (takeTurn ? (preferences.penaltyForDelay * confidence) : 0) -
                            (preferences.penaltyForIncorrectExecution * (1 - confidence)));
        }
        return expectedReward;
    }

}

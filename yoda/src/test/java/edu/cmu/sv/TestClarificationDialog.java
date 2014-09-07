package edu.cmu.sv;

import edu.cmu.sv.dialog_management.DialogAct;
import edu.cmu.sv.dialog_management.DialogManager;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.CollectionComparison;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by David Cohen on 9/4/14.
 */
public class TestClarificationDialog {

    private class EvaluationResult{
        boolean correctIsTopAction;
        boolean correctInTopNActions;
        int correctActionRank;
        double correctActionRelativeReward; // reward(correct action) / reward(top action)

        private EvaluationResult(boolean correctIsTopAction, boolean correctInTopNActions, int correctActionRank, double correctActionRelativeReward) {
            this.correctIsTopAction = correctIsTopAction;
            this.correctInTopNActions = correctInTopNActions;
            this.correctActionRank = correctActionRank;
            this.correctActionRelativeReward = correctActionRelativeReward;
        }

        @Override
        public String toString() {
            return "correct is top action:"+correctIsTopAction+", correct in top N actions:"+correctInTopNActions+
                    ", correct action rank:"+correctActionRank+", correct action relative reward:"+
                    correctActionRelativeReward;
        }
    }

    private class TestCase{
        Map<String, SemanticsModel> hypotheses;
        StringDistribution hypothesisDistribution;
        Pair<DialogAct.DA_TYPE, Map<String, String>> bestActionDescriptor;

        private TestCase(Map<String, SemanticsModel> hypotheses, StringDistribution hypothesisDistribution, Pair<DialogAct.DA_TYPE, Map<String, String>> bestActionDescriptor) {
            this.hypotheses = hypotheses;
            this.hypothesisDistribution = hypothesisDistribution;
            this.bestActionDescriptor = bestActionDescriptor;
        }

        private EvaluationResult evaluate(List<Pair<Pair<DialogAct.DA_TYPE, Map<String, String>>, Double>> nBestActions){
            boolean correctInTopNActions = false;
            boolean correctIsTopAction = false;
            double topConfidence = nBestActions.get(0).getRight();
            int correctActionRank = -1;
            double correctActionRelativeReward = 0.0;
            for (int i = 0; i < nBestActions.size(); i++) {
                Pair<Pair<DialogAct.DA_TYPE, Map<String, String>>, Double> candidateActionAndReward = nBestActions.get(i);
                if (candidateActionAndReward.getLeft()==null)
                    continue;
                // right now I don't distinguish between parameters
                if (candidateActionAndReward.getLeft().getLeft().equals(bestActionDescriptor.getLeft()) &&
                        new HashSet<>(candidateActionAndReward.getLeft().getRight().values()).
                                equals(new HashSet<>(bestActionDescriptor.getRight().values()))) {
                    if (i==0)
                        correctIsTopAction = true;
                    correctInTopNActions = true;
                    correctActionRank = i;
                    correctActionRelativeReward = candidateActionAndReward.getRight() / topConfidence;
                    break;
                }

            }
            nBestActions.forEach(System.out::println);
            return new EvaluationResult(correctIsTopAction, correctInTopNActions, correctActionRank, correctActionRelativeReward);
        }

    }

    @Test
    public void Test(){

        List<TestCase> testCases = basicClarificationTestSet();
        for (TestCase testCase : testCases) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.getTracker().updateDialogState(testCase.hypotheses, testCase.hypothesisDistribution, (float) 0);
            List<Pair<Pair<DialogAct.DA_TYPE, Map<String,String>>, Double>> topActions =
                    dialogManager.evaluateClarificationActions();
            System.out.println("Test case evaluation:");
            System.out.println(testCase.evaluate(topActions));
        }
    }

    List<TestCase> basicClarificationTestSet(){
        List<TestCase> ans = new LinkedList<>();
        Map<String, SemanticsModel> utterances;
        StringDistribution weights;
        TestCase testCase;
        Map<String, String> bestDialogActionParameters;
        Pair<DialogAct.DA_TYPE, Map<String, String>> bestDialogAction;

        SemanticsModel hyp1;
        SemanticsModel hyp2;


        // test case 0: value ambiguity, should respond with a disambiguate-value dialog act
        utterances = new HashMap<>();
        weights = new StringDistribution();
        bestDialogActionParameters = new HashMap<>();
        bestDialogActionParameters.put("v1", "y");
        bestDialogActionParameters.put("v2", "z");
        bestDialogAction = new ImmutablePair<>(DialogAct.DA_TYPE.DISAMBIGUATE_VALUE, bestDialogActionParameters);

        hyp1 = new SemanticsModel();
        hyp1.getSlots().put("dialogAct", "WHQuestion");
        hyp1.getSlots().put("slot1", "x");
        hyp1.getSlots().put("slot2", "y");
        utterances.put("hyp1", hyp1);
        weights.extend("hyp1", .6);

        hyp2 = new SemanticsModel();
        hyp2.getSlots().put("dialogAct", "WHQuestion");
        hyp2.getSlots().put("slot1", "x");
        hyp2.getSlots().put("slot2", "z");
        utterances.put("hyp2", hyp2);
        weights.extend("hyp2", .4);
//        System.out.println("weights:\n"+weights);

        testCase = new TestCase(utterances, weights, bestDialogAction);
        ans.add(testCase);


//        // test case 1: role ambiguity, should respond with a disambiguate-role dialog act
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogActionParameters = new HashMap<>();
//        bestDialogActionParameters.put("v1", "y");
//        bestDialogActionParameters.put("v2", "z");
//        bestDialogAction = new ImmutablePair<>(DialogAct.DA_TYPE.DISAMBIGUATE_VALUE, bestDialogActionParameters);
//
//        hyp1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "WHQuestion");
//        hyp1.getSlots().put("slot1", "x");
//        utterances.put("hyp1", hyp1);
//        weights.extend("hyp1", .6);
//
//        hyp2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "WHQuestion");
//        hyp2.getSlots().put("slot2", "x");
//        utterances.put("hyp2", hyp2);
//        weights.extend("hyp2", .4);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);




        return ans;
    }


}

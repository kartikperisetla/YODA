package edu.cmu.sv;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.*;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.EvaluationTools;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/4/14.
 */
public class TestActionSelection {

    private class EvaluationResult{
        boolean correctIsTopAction;
        boolean correctInTopNActions;
        int correctActionRank;
        double correctActionRewardDifference; // reward(correct action) / reward(top action)
        Pair<Class<? extends SystemAction>, Class<? extends SystemAction>> confusion;

        private EvaluationResult(boolean correctIsTopAction, boolean correctInTopNActions, int correctActionRank, double correctActionRewardDifference, Pair<Class<? extends SystemAction>, Class<? extends SystemAction>> confusion) {
            this.correctIsTopAction = correctIsTopAction;
            this.correctInTopNActions = correctInTopNActions;
            this.correctActionRank = correctActionRank;
            this.correctActionRewardDifference = correctActionRewardDifference;
            this.confusion = confusion;
        }

        public boolean isCorrectIsTopAction() {
            return correctIsTopAction;
        }

        public int getCorrectActionRank() {
            return correctActionRank;
        }

        public double getCorrectActionRewardDifference() {
            return correctActionRewardDifference;
        }

        public Pair<Class<? extends SystemAction>, Class<? extends SystemAction>> getConfusion() {
            return confusion;
        }

        public void printSummary(){
            System.out.println("correct action rank: "+correctActionRank);
            System.out.println("correct action reward difference: "+ correctActionRewardDifference);
        }

        @Override
        public String toString() {
            return "correct is top action:"+correctIsTopAction+", correct in top N actions:"+correctInTopNActions+
                    ", correct action rank:"+correctActionRank+", correct action relative reward:"+
                    correctActionRewardDifference + ", confusion:"+confusion;
        }
    }

    private class TestCase{
        DiscourseUnit2 du;
        SystemAction bestActionDescriptor;
        YodaEnvironment yodaEnvironment;

        private TestCase(DiscourseUnit2 du, SystemAction bestActionDescriptor, YodaEnvironment yodaEnvironment) {
            this.du = du;
            this.bestActionDescriptor = bestActionDescriptor;
            this.yodaEnvironment = yodaEnvironment;
        }

        private EvaluationResult evaluate(List<Pair<SystemAction, Double>> nBestActions){
            if (nBestActions.size()==0){
                return new EvaluationResult(false, false, -1, Double.NaN, null);
            }
            boolean correctInTopNActions = false;
            boolean correctIsTopAction = false;
            double topConfidence = nBestActions.get(0).getRight();
            int correctActionRank = -1;
            double correctActionRewardDifference = 0.0;
            for (int i = 0; i < nBestActions.size(); i++) {
                Pair<SystemAction, Double> candidateActionAndReward = nBestActions.get(i);
                if (candidateActionAndReward.getLeft()==null)
                    continue;
                // right now I don't distinguish between parameters
                if (candidateActionAndReward.getLeft().evaluationMatch(bestActionDescriptor)) {
                    if (i==0)
                        correctIsTopAction = true;
                    correctInTopNActions = true;
                    correctActionRank = i;
                    correctActionRewardDifference = topConfidence - candidateActionAndReward.getRight();
                    break;
                }
            }

            Pair<Class<? extends SystemAction>, Class<? extends SystemAction>> confusion = null;
            if (!bestActionDescriptor.getClass().equals(nBestActions.get(0).getLeft().getClass()))
                confusion = new ImmutablePair<>(bestActionDescriptor.getClass(),
                        nBestActions.get(0).getLeft().getClass());

            nBestActions.forEach(System.out::println);
            return new EvaluationResult(correctIsTopAction, correctInTopNActions, correctActionRank, correctActionRewardDifference, confusion);
        }

    }

    @Test
    public void Test() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<TestCase> testCases = basicClarificationTestSet();
        List<EvaluationResult> evaluationResults = new LinkedList<>();
        SummaryStatistics rewardStatistics = new SummaryStatistics();
        for (TestCase testCase : testCases) {
            testCase.yodaEnvironment.dst.setDiscourseUnit(testCase.du);
            List<Pair<SystemAction, Double>> topActions = testCase.yodaEnvironment.dm.selectAction();
            topActions.stream().map(Pair::getRight).forEach(rewardStatistics::addValue);
            System.out.println("\nTest case evaluation:");
            EvaluationResult result = testCase.evaluate(topActions);
            evaluationResults.add(result);
            result.printSummary();
        }

        // aggregate evaluation results
        EvaluationTools.ConfusionCounter confusionCounter = new EvaluationTools.ConfusionCounter<>(
                evaluationResults.stream().
                        map(EvaluationResult::getConfusion).
                        filter(x -> x != null).
                        map(x -> new ImmutablePair<>(x.getKey().getSimpleName(), x.getValue().getSimpleName())).
                        collect(Collectors.toList()));

        System.out.println("\nAggregate Evaluation Results:");

        System.out.println("Mean reward difference: "+evaluationResults.stream().
                map(EvaluationResult::getCorrectActionRewardDifference).mapToDouble(x -> x).
                average().getAsDouble());

        System.out.println("Mean normalized reward difference: "+evaluationResults.stream().
                map(EvaluationResult::getCorrectActionRewardDifference).mapToDouble(x -> x).
                average().getAsDouble() / rewardStatistics.getStandardDeviation());


        System.out.println("Mean correct action rank: "+evaluationResults.stream().
                map(EvaluationResult::getCorrectActionRank).mapToDouble(x->x).
                average().getAsDouble());

        System.out.println("Fraction correct: " + evaluationResults.stream().
                map(EvaluationResult::isCorrectIsTopAction).filter(x -> x).
                count()*1.0/evaluationResults.size());

        System.out.println("Confusion Counter:");
        System.out.println(confusionCounter);

    }

    private List<TestCase> basicClarificationTestSet(){
        List<TestCase> ans = new LinkedList<>();



        // test case 0: request confirm value
        {
            YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();
            DiscourseUnit2 du = new DiscourseUnit2();

            DiscourseUnit2.DialogStateHypothesis hyp1 = new DiscourseUnit2.DialogStateHypothesis();
            String jsonString1 = "{\"dialogAct\":\"Command\",\n" +
                    " \"verb\":{\"class\":\"Create\",\n" +
                    "         \"Patient\":{\"class\":\"Meeting\"}}}";
            SemanticsModel spokenByThemHyp1 = new SemanticsModel(jsonString1);
            hyp1.setSpokenByThem(spokenByThemHyp1);

            DiscourseUnit2.DialogStateHypothesis hyp2 = new DiscourseUnit2.DialogStateHypothesis();
            String jsonString2 = "{\"dialogAct\":\"Command\",\n" +
                    " \"verb\":{\"class\":\"Create\",\n" +
                    "         \"Patient\":{\"class\":\"Email\"}}}";
            SemanticsModel spokenByThemHyp2 = new SemanticsModel(jsonString2);
            hyp2.setSpokenByThem(spokenByThemHyp2);

            du.getHypotheses().put("hyp1", hyp1);
            du.getHypotheses().put("hyp2", hyp2);
            StringDistribution weights = new StringDistribution();
            weights.put("hyp1", .6);
            weights.put("hyp2", .4);
            du.setHypothesisDistribution(weights);

            Map<String, Object> bestDialogActionParameters = new HashMap<>();
            bestDialogActionParameters.put("v1", new SemanticsModel("{\"class\":\"Meeting\"}").getInternalRepresentation());
            DialogAct bestAction = new RequestConfirmValue();
            bestAction.bindVariables(bestDialogActionParameters);
            TestCase testCase = new TestCase(du, bestAction, yodaEnvironment);
            ans.add(testCase);

        }



        // test case 1: confirm sense suggestion
        {
            YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();
            DiscourseUnit2 du = new DiscourseUnit2();

            DiscourseUnit2.DialogStateHypothesis hyp1 = new DiscourseUnit2.DialogStateHypothesis();
            String jsonString1 = "{\"dialogAct\":null,\n" +
                    " \"verb\":{\"class\":\"Create\",\n" +
                    "         \"Patient\":{\"class\":\"Suggested\",\n" +
                    "\"HasValue\":{\"class\":\"Email\"}}}}";
            SemanticsModel spokenByThemHyp1 = new SemanticsModel(jsonString1);
            hyp1.setSpokenByThem(spokenByThemHyp1);

            String jsonString2 = "{\"dialogAct\":\"Command\",\n" +
                    " \"verb\":{\"class\":\"Create\",\n" +
                    "         \"Patient\":{\"class\":\"Email\"}}}";
            SemanticsModel spokenByMeHyp1 = new SemanticsModel(jsonString2);
            hyp1.setSpokenByMe(spokenByMeHyp1);

            du.getHypotheses().put("hyp1", hyp1);
            du.getHypotheses().put("hyp2", new DiscourseUnit2.DialogStateHypothesis());
            StringDistribution weights = new StringDistribution();
            weights.put("hyp1", .8);
            weights.put("hyp2", .2);
            du.setHypothesisDistribution(weights);

            DialogAct bestAction = new ConfirmSenseSuggestion();
            bestAction.bindVariables(new HashMap<>());
            TestCase testCase = new TestCase(du, bestAction, yodaEnvironment);
            ans.add(testCase);
        }



        // test case 1: confirm sense suggestion
        {
            YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();
            DiscourseUnit2 du = new DiscourseUnit2();

            DiscourseUnit2.DialogStateHypothesis hyp1 = new DiscourseUnit2.DialogStateHypothesis();
            String jsonString1 = "{\"dialogAct\":null,\n" +
                    " \"verb\":{\"class\":\"Create\",\n" +
                    "         \"Patient\":{\"class\":\"Suggested\",\n" +
                    "\"HasValue\":{\"class\":\"Email\"}}}}";
            SemanticsModel spokenByThemHyp1 = new SemanticsModel(jsonString1);
            hyp1.setSpokenByThem(spokenByThemHyp1);

            String jsonString2 = "{\"dialogAct\":\"Command\",\n" +
                    " \"verb\":{\"class\":\"Create\",\n" +
                    "         \"Patient\":{\"class\":\"Meeting\"}}}";
            SemanticsModel spokenByMeHyp1 = new SemanticsModel(jsonString2);
            hyp1.setSpokenByMe(spokenByMeHyp1);

            du.getHypotheses().put("hyp1", hyp1);
            du.getHypotheses().put("hyp2", new DiscourseUnit2.DialogStateHypothesis());
            StringDistribution weights = new StringDistribution();
            weights.put("hyp1", .8);
            weights.put("hyp2", .2);
            du.setHypothesisDistribution(weights);

            DialogAct bestAction = new DenyIncorrectSenseSuggestion();
            bestAction.bindVariables(new HashMap<>());
            TestCase testCase = new TestCase(du, bestAction, yodaEnvironment);
            ans.add(testCase);
        }




//        // test case 0: role ambiguity with different depths
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogActionParameters = new HashMap<>();
//        bestDialogActionParameters.put("r1", "theme.endTime");
//        bestDialogActionParameters.put("r2", "toTime");
//        bestDialogAction = new RequestDisambiguateRole().bindVariables(bestDialogActionParameters);
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "WHQuestion");
//        hyp1.getSlots().put("Theme", "meeting0");
//        hyp1.getSlots().put("fromTime", "t0");
//        hyp1.getChildren().put("meeting0", child1);
//        child1.getSlots().put("endTime", "t1");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .6);
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "WHQuestion");
//        hyp2.getSlots().put("Theme", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getSlots().put("toTime", "t1");
//        hyp2.getChildren().put("meeting1", child2);
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .4);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);

//        // test case 1: request patient
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogActionParameters = new HashMap<>();
//        bestDialogActionParameters.put("r1", "Patient");
//        bestDialogActionParameters.put("v1", "Exist");
//        bestDialogAction = new RequestVerbRole().bindVariables(bestDialogActionParameters);
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "YNQuestion");
//        hyp1.getSlots().put("verb", "Exist");
//        hyp1.getSlots().put("Agent", "meeting0");
//        hyp1.getSlots().put("fromTime", "t0");
//        hyp1.getChildren().put("meeting0", child1);
//        child1.getSlots().put("endTime", "t1");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .9);
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "YNQuestion");
//        hyp2.getSlots().put("verb", "Exist");
//        hyp2.getSlots().put("Agent", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getSlots().put("toTime", "t1");
//        hyp2.getChildren().put("meeting1", child2);
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .1);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//
//        // test case 2: ask for a rephrase
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogAction = new RequestRephrase().bindVariables(new HashMap<>());
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "WHQuestion");
//        hyp1.getSlots().put("Theme", "meeting0");
//        hyp1.getSlots().put("fromTime", "t0");
//        hyp1.getChildren().put("meeting0", child1);
//        child1.getSlots().put("endTime", "t1");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .25);
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "WHQuestion");
//        hyp2.getSlots().put("Theme", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getSlots().put("toTime", "t1");
//        hyp2.getChildren().put("meeting1", child2);
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .25);
//
//        hyp3 = new SemanticsModel();
//        child3 = new SemanticsModel();
//        hyp3.getSlots().put("dialogAct", "YNQuestion");
//        hyp3.getSlots().put("Theme", "meeting2");
//        hyp3.getSlots().put("fromTime", "t0");
//        hyp3.getChildren().put("meeting2", child3);
//        child3.getSlots().put("endTime", "t1");
//        utterances.put("hyp3", hyp3);
//        weights.put("hyp3", .25);
//
//        hyp4 = new SemanticsModel();
//        child4 = new SemanticsModel();
//        hyp4.getSlots().put("dialogAct", "YNQuestion");
//        hyp4.getSlots().put("Theme", "meeting3");
//        hyp4.getSlots().put("fromTime", "t0");
//        hyp4.getSlots().put("toTime", "t1");
//        hyp4.getChildren().put("meeting3", child4);
//        utterances.put("hyp4", hyp4);
//        weights.put("hyp4", .25);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//
//        // test case 3: value ambiguity
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogActionParameters = new HashMap<>();
//        bestDialogActionParameters.put("v1", "t1");
//        bestDialogAction = new RequestConfirmValue().bindVariables(bestDialogActionParameters);
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "WHQuestion");
//        hyp1.getSlots().put("Theme", "meeting0");
//        hyp1.getSlots().put("fromTime", "t0");
//        hyp1.getChildren().put("meeting0", child1);
//        child1.getSlots().put("endTime", "t1");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .6);
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "WHQuestion");
//        hyp2.getSlots().put("Theme", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getChildren().put("meeting1", child2);
//        child2.getSlots().put("endTime", "t2");
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .4);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//
//        // test case 4: respond to YN question
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogAction = new RespondToYNQuestionTask(db);
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "YNQuestion");
//        hyp1.getSlots().put("Theme", "meeting0");
//        hyp1.getSlots().put("fromTime", "t0");
//        hyp1.getChildren().put("meeting0", child1);
//        child1.getSlots().put("endTime", "t1");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .9);
//
//        ((DialogTask) bestDialogAction).setTaskSpec(hyp1.deepCopy());
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "YNQuestion");
//        hyp2.getSlots().put("Theme", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getSlots().put("toTime", "t1");
//        hyp2.getChildren().put("meeting1", child2);
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .1);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//
//        // test case 5: send an email
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogAction = new SendEmailTask(db);
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "Command");
//        hyp1.getSlots().put("verb", "Send");
//        hyp1.getSlots().put("Theme", "email0");
//        hyp1.getSlots().put("recipient", "p0");
//        hyp1.getChildren().put("email0", child1);
//        child1.getSlots().put("class", "Email");
//        child1.getSlots().put("number", "<SG>");
//        child1.getSlots().put("ref-type", "<INDEF>");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .9);
//
//        ((NonDialogTask) bestDialogAction).setTaskSpec(hyp1.deepCopy());
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "YNQuestion");
//        hyp2.getSlots().put("Theme", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getSlots().put("toTime", "t1");
//        hyp2.getChildren().put("meeting1", child2);
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .1);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//        // test case 6: create a meeting
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogAction = new CreateMeetingTask(db);
//
//        hyp1 = new SemanticsModel();
//        child1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "Command");
//        hyp1.getSlots().put("verb", "Create");
//        hyp1.getSlots().put("Theme", "meeting0");
//        hyp1.getSlots().put("recipient", "p0");
//        hyp1.getChildren().put("meeting0", child1);
//        child1.getSlots().put("class", "Meeting");
//        child1.getSlots().put("number", "<SG>");
//        child1.getSlots().put("ref-type", "<INDEF>");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .9);
//
//        ((NonDialogTask) bestDialogAction).setTaskSpec(hyp1.deepCopy());
//
//        hyp2 = new SemanticsModel();
//        child2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "YNQuestion");
//        hyp2.getSlots().put("Theme", "meeting1");
//        hyp2.getSlots().put("fromTime", "t0");
//        hyp2.getSlots().put("toTime", "t1");
//        hyp2.getChildren().put("meeting1", child2);
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .1);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//        // test case 7: requestConfirmRole
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogActionParameters = new HashMap<>();
//        bestDialogActionParameters.put("r1", "fromTime");
//        bestDialogAction = new RequestConfirmRole().bindVariables(bestDialogActionParameters);
//
//        hyp1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "WHQuestion");
//        hyp1.getSlots().put("fromTime", "t0");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .4);
//
//        hyp2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "WHQuestion");
//        hyp2.getSlots().put("toTime", "t0");
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .25);
//
//        hyp3 = new SemanticsModel();
//        hyp3.getSlots().put("dialogAct", "WHQuestion");
//        hyp3.getSlots().put("atTime", "t0");
//        utterances.put("hyp3", hyp3);
//        weights.put("hyp3", .25);
//
//        hyp4 = new SemanticsModel();
//        hyp4.getSlots().put("dialogAct", "YNQuestion");
//        hyp4.getSlots().put("fromTime", "t0");
//        utterances.put("hyp4", hyp4);
//        weights.put("hyp4", .10);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);
//
//
//        // test case 8: request disambiguate value
//        utterances = new HashMap<>();
//        weights = new StringDistribution();
//        bestDialogActionParameters = new HashMap<>();
//        bestDialogActionParameters.put("v1", "t0");
//        bestDialogActionParameters.put("v2", "t1");
//        bestDialogAction = new RequestDisambiguateValue().bindVariables(bestDialogActionParameters);
//
//        hyp1 = new SemanticsModel();
//        hyp1.getSlots().put("dialogAct", "WHQuestion");
//        hyp1.getSlots().put("fromTime", "t0");
//        hyp1.getSlots().put("endTime", "t2");
//        utterances.put("hyp1", hyp1);
//        weights.put("hyp1", .6);
//
//        hyp2 = new SemanticsModel();
//        hyp2.getSlots().put("dialogAct", "WHQuestion");
//        hyp2.getSlots().put("fromTime", "t1");
//        hyp2.getSlots().put("endTime", "t2");
//        utterances.put("hyp2", hyp2);
//        weights.put("hyp2", .4);
//
//        testCase = new TestCase(utterances, weights, bestDialogAction);
//        ans.add(testCase);


        return ans;
    }


}

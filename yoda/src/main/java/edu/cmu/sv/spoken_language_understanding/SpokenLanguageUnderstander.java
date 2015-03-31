package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * Created by David Cohen on 11/21/14.
 */
public interface SpokenLanguageUnderstander {
    void process1BestAsr(String asrResult);
    void processNBestAsr(StringDistribution asrNBestResult);

    public default void evaluate(YodaEnvironment yodaEnvironment, SLUDataset dataset){
        int numTestCases = dataset.getDataSet().size();

        int numTotalCorrect = 0;
        int numDialogActCorrect = 0;
        int numVerbOrTopicClassCorrect = 0;

        int numTotalCorrectPresent = 0;
        int numDialogActCorrectPresent = 0;
        int numVerbOrTopicClassCorrectPresent = 0;

        double weightTotalCorrect = 0.0;
        double weightDialogActCorrect = 0.0;
        double weightVerbOrTopicClassCorrect = 0.0;


        for (Pair<String, SemanticsModel> sample : dataset.getDataSet()){
            process1BestAsr(sample.getKey());
            Pair<Turn, Long> dstInputTurn = yodaEnvironment.DstInputQueue.poll();
            if (dstInputTurn==null)
                throw new Error("null slu result");
            StringDistribution sluDistribution = dstInputTurn.getKey().getHypothesisDistribution();
            Map<String, SemanticsModel> sluHypotheses = dstInputTurn.getKey().getHypotheses();

            // evaluate the best hypothesis
            SemanticsModel bestHypothesis = sluHypotheses.get(sluDistribution.getTopHypothesis());

            Pair<Boolean, String> contentComparisonReport = SemanticsModel.contentEquivalenceComparisonAndReport(bestHypothesis, sample.getRight());
            if (contentComparisonReport.getLeft())
                numTotalCorrect++;
            else
                System.err.println(contentComparisonReport.getRight());
            if (bestHypothesis.newGetSlotPathFiller("dialogAct").equals(sample.getRight().newGetSlotPathFiller("dialogAct")))
                numDialogActCorrect++;
            if (bestHypothesis.newGetSlotPathFiller("verb.class")!=null &&
                    sample.getRight().newGetSlotPathFiller("verb.class")!=null &&
                    bestHypothesis.newGetSlotPathFiller("verb.class").equals(sample.getRight().newGetSlotPathFiller("verb.class")))
                numVerbOrTopicClassCorrect++;
            if (bestHypothesis.newGetSlotPathFiller("topic.class")!=null &&
                    sample.getRight().newGetSlotPathFiller("topic.class")!=null &&
                    bestHypothesis.newGetSlotPathFiller("topic.class").equals(sample.getRight().newGetSlotPathFiller("topic.class")))
                numVerbOrTopicClassCorrect++;

            // evaluate the n-best list
            boolean totalCorrectPresent = false;
            boolean dialogActCorrectPresent = false;
            boolean verbOrTopicCorrectPresent = false;
            for (String key : sluDistribution.keySet()) {
                double thisWeight = sluDistribution.get(key);
                SemanticsModel thisHypothesis = sluHypotheses.get(key);
                contentComparisonReport = SemanticsModel.contentEquivalenceComparisonAndReport(bestHypothesis, sample.getRight());
                if (contentComparisonReport.getLeft()) {
                    weightTotalCorrect += thisWeight;
                    totalCorrectPresent = true;
                }
                if (thisHypothesis.newGetSlotPathFiller("dialogAct").equals(sample.getRight().newGetSlotPathFiller("dialogAct"))) {
                    weightDialogActCorrect += thisWeight;
                    dialogActCorrectPresent = true;
                }
                if (thisHypothesis.newGetSlotPathFiller("verb.class") != null &&
                        sample.getRight().newGetSlotPathFiller("verb.class") != null &&
                        thisHypothesis.newGetSlotPathFiller("verb.class").equals(sample.getRight().newGetSlotPathFiller("verb.class"))) {
                    weightVerbOrTopicClassCorrect += thisWeight;
                    verbOrTopicCorrectPresent = true;
                }
                if (thisHypothesis.newGetSlotPathFiller("topic.class") != null &&
                        sample.getRight().newGetSlotPathFiller("topic.class") != null &&
                        thisHypothesis.newGetSlotPathFiller("topic.class").equals(sample.getRight().newGetSlotPathFiller("topic.class"))) {
                    weightVerbOrTopicClassCorrect += thisWeight;
                    verbOrTopicCorrectPresent = true;
                }
            }
            numTotalCorrectPresent += totalCorrectPresent ? 1 : 0;
            numDialogActCorrectPresent += dialogActCorrectPresent ? 1 : 0;
            numVerbOrTopicClassCorrectPresent += verbOrTopicCorrectPresent ? 1 : 0;
        }



        System.out.println("Total number of test cases:" + numTestCases);

        System.out.println();
        System.out.println("1-best result evaluation:");
        System.out.println("Number completely correct:" + numTotalCorrect + " ("+1.0*numTotalCorrect/numTestCases + ")");
        System.out.println("Number dialog act correct:" + numDialogActCorrect + " ("+1.0*numDialogActCorrect/numTestCases + ")");
        System.out.println("Number verb/topic class correct:" + numVerbOrTopicClassCorrect + " ("+1.0*numVerbOrTopicClassCorrect/numTestCases + ")");

        System.out.println();
        System.out.println("N-best list presence evaluation");
        System.out.println("Number of times completely correct answer is present:" + numTotalCorrectPresent + " ("+1.0*numTotalCorrectPresent/numTestCases + ")");
        System.out.println("Number of times dialog act correct answer is present:" + numDialogActCorrectPresent + " ("+1.0*numDialogActCorrectPresent/numTestCases + ")");
        System.out.println("Number of times verb/topic class correct answer is present:" + numVerbOrTopicClassCorrectPresent + " ("+1.0*numVerbOrTopicClassCorrectPresent/numTestCases + ")");

        System.out.println();
        System.out.println("N-best list probability evaluation");
        System.out.println("Weight given to completely correct answers:" + weightTotalCorrect + " ("+1.0*weightTotalCorrect/numTestCases + ")");
        System.out.println("Weight given to dialog act correct answers:" + weightDialogActCorrect + " ("+1.0*weightDialogActCorrect/numTestCases + ")");
        System.out.println("Weight given to verb/topic class correct answers:" + weightVerbOrTopicClassCorrect + " ("+1.0*weightVerbOrTopicClassCorrect/numTestCases + ")");

    }
}

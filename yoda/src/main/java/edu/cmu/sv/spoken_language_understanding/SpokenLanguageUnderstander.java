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

            if (SemanticsModel.contentEqual(bestHypothesis, sample.getRight()))
                numTotalCorrect++;
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
            for (String key : sluDistribution.keySet()) {
                double thisWeight = sluDistribution.get(key);
                SemanticsModel thisHypothesis = sluHypotheses.get(key);
                if (SemanticsModel.contentEqual(thisHypothesis, sample.getRight()))
                    weightTotalCorrect+=thisWeight;
                if (thisHypothesis.newGetSlotPathFiller("dialogAct").equals(sample.getRight().newGetSlotPathFiller("dialogAct")))
                    weightDialogActCorrect+=thisWeight;
                if (thisHypothesis.newGetSlotPathFiller("verb.class") != null &&
                        sample.getRight().newGetSlotPathFiller("verb.class") != null &&
                        thisHypothesis.newGetSlotPathFiller("verb.class").equals(sample.getRight().newGetSlotPathFiller("verb.class")))
                    weightVerbOrTopicClassCorrect+=thisWeight;
                if (thisHypothesis.newGetSlotPathFiller("topic.class") != null &&
                        sample.getRight().newGetSlotPathFiller("topic.class") != null &&
                        thisHypothesis.newGetSlotPathFiller("topic.class").equals(sample.getRight().newGetSlotPathFiller("topic.class")))
                    weightVerbOrTopicClassCorrect+=thisWeight;
            }
        }



        System.out.println("1-best result evaluation:");
        System.out.println("Total number of test cases:" + numTestCases);
        System.out.println("Number completely correct:" + numTotalCorrect + " ("+1.0*numTotalCorrect/numTestCases + ")");
        System.out.println("Number dialog act correct:" + numDialogActCorrect + " ("+1.0*numDialogActCorrect/numTestCases + ")");
        System.out.println("Number verb/topic class correct:" + numVerbOrTopicClassCorrect + " ("+1.0*numVerbOrTopicClassCorrect/numTestCases + ")");

        System.out.println();
        System.out.println("N-best list result evaluation");
        System.out.println("Total number of test cases:" + numTestCases);
        System.out.println("Weight given to completely correct answers:" + weightTotalCorrect + " ("+1.0*weightTotalCorrect/numTestCases + ")");
        System.out.println("Weight given to dialog act correct answers:" + weightDialogActCorrect + " ("+1.0*weightDialogActCorrect/numTestCases + ")");
        System.out.println("Weight given to verb/topic class correct answers:" + weightVerbOrTopicClassCorrect + " ("+1.0*weightVerbOrTopicClassCorrect/numTestCases + ")");

    }
}

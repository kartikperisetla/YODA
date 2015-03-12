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

        for (Pair<String, SemanticsModel> sample : dataset.getDataSet()){
            process1BestAsr(sample.getKey());
            Pair<Turn, Long> dstInputTurn = yodaEnvironment.DstInputQueue.poll();
            if (dstInputTurn==null)
                throw new Error("null slu result");
            StringDistribution sluDistribution = dstInputTurn.getKey().getHypothesisDistribution();
            Map<String, SemanticsModel> sluHypotheses = dstInputTurn.getKey().getHypotheses();

            // for now, only evaluate the best hypothesis
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

        }

        System.out.println("Total number of test cases:" + numTestCases);
        System.out.println("Number completely correct:" + numTotalCorrect + " ("+1.0*numTotalCorrect/numTestCases + ")");
        System.out.println("Number dialog act correct:" + numDialogActCorrect + " ("+1.0*numDialogActCorrect/numTestCases + ")");
        System.out.println("Number verb/topic class correct:" + numVerbOrTopicClassCorrect + " ("+1.0*numVerbOrTopicClassCorrect/numTestCases + ")");

    }
}

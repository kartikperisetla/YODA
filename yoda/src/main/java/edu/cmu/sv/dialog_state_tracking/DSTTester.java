package edu.cmu.sv.dialog_state_tracking;

import com.google.common.collect.Iterables;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/17/14.
 *
 * A class to run tests on the dialog state tracker
 *
 */
public class DSTTester {

    Map<Turn, Float> turns;
    Map<SemanticsModel, Float> evaluationStates;


    public void evaluate(){
        DiscourseUnit2 DU = new DiscourseUnit2();
        Float startTime = turns.values().stream().min(Float::compare).orElse((float)0)-1;
        Float endTime = turns.values().stream().max(Float::compare).orElse((float)0)+1;

        Set<Turn> turnsDone = new HashSet<>();
        Set<SemanticsModel> evaluationStatesDone = new HashSet<>();
        // .1 second increments
        for (Float t = startTime; t < endTime; t+=(float).1) {
            for (Turn turn : turns.keySet()){
                if (turns.get(turn) > endTime && !turnsDone.contains(turn)){
                    DU.updateDiscourseUnit(turn.hypotheses, turn.hypothesisDistribution,
                            turn.speaker, turns.get(turn));
                    turnsDone.add(turn);
                }
            }
            for (SemanticsModel groundTruth : evaluationStates.keySet()){
                if (turns.get(groundTruth) > endTime && !turnsDone.contains(groundTruth)){
                    //TODO: evaluate the dialog state and record results
                    evaluationStatesDone.add(groundTruth);
                }
            }
        }

    }

    public class Turn{
        Map<String, SemanticsModel> hypotheses;
        StringDistribution hypothesisDistribution;
        String speaker;
    }

}

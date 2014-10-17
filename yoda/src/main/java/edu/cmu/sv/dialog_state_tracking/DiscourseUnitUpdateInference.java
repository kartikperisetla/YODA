package edu.cmu.sv.dialog_state_tracking;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Describe the conditions and effects of major categories of discourse unit updates.
 * Updates within a single DU usually relate to clarification and grounding.
 *
 */
public interface DiscourseUnitUpdateInference {

    /*
    * Return a DU2 containing all the new hypotheses generated from applying
    * this inference class to an assumedHypothesisID from a previous DU2
    *
    * The returned DU contains the conditional probabilities of these hypotheses
    * given that the assumedHypothesisID is true.
    *
    * The returned value won't replace the previous value, but its hypotheses will
    * be weighted by the assumed hypothesis' prior and collected to create the new DU
    *
    * */
    public DiscourseUnit2 applyAll(DiscourseUnit2.DialogStateHypothesis currentState, Turn turn, float timeStamp);

}

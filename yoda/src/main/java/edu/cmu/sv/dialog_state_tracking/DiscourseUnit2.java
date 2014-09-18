package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnit2 {
    static int BEAM_WIDTH = 10;
    StringDistribution hypothesisDistribution;
    Map<String, SemanticsModel> spokenByThem;
    Map<String, SemanticsModel> understoodByThem;
    SemanticsModel spokenByMe;
    float timeOfLastActByThem;
    float timeOfLastActByMe;

    public DiscourseUnit2() {
        hypothesisDistribution = new StringDistribution();
        hypothesisDistribution.put("initial_hypothesis", 1.0);
        SemanticsModel blank = new SemanticsModel();
        spokenByMe = blank.deepCopy();
        spokenByThem = new HashMap<>();
        spokenByThem.put("initial_hypothesis", blank.deepCopy());
        understoodByThem = new HashMap<>();
        understoodByThem.put("initial_hypothesis", blank.deepCopy());
    }

    /*
        * Within a single discourse unit,
        * all dialog acts are either initial presentation, clarification, or elaboration
        *
        * This function makes heavy use of SemanticsModel.update(SemanticsModel other)
        *
        * If the speaker is "system", utteranceHypotheses = null
        * If the speaker is "user", systemUtterance = null
        * */
    public void updateDiscourseUnit(Map<String, SemanticsModel> utteranceHypotheses,
                                    SemanticsModel systemUtterance,
                                    StringDistribution weights,
                                    String speaker,
                                    float timeOfAct){
        assert speaker.equals("system") || speaker.equals("user");
        int newDUHypothesisCounter = 0;
        Map<String, SemanticsModel> newSpokenByThem = new HashMap<>();
        Map<String, SemanticsModel> newUnderstoodByThem = new HashMap<>();
        StringDistribution newHypothesisDistribution = new StringDistribution();
        if (speaker.equals("system")){
            timeOfLastActByMe = timeOfAct;
            spokenByMe = systemUtterance;
            for (String DUHypothesisID : hypothesisDistribution.keySet()) {
                String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                // updates to understoodByThem to account for the system utterance
                newUnderstoodByThem.put(newDUHypothesisID,
                        simpleSemanticsUpdate(spokenByThem.get(DUHypothesisID), spokenByMe));
                // no change to spokenByThem
                newSpokenByThem.put(newDUHypothesisID, spokenByThem.get(DUHypothesisID));
                // probability is copied from the current distribution
                newHypothesisDistribution.put(newDUHypothesisID,
                        hypothesisDistribution.get(DUHypothesisID));
            }
        }
        else {
            timeOfLastActByThem = timeOfAct;
            for (String DUHypothesisID : hypothesisDistribution.keySet()){
                for (String utteranceHypothesisID : utteranceHypotheses.keySet()){
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    // no change to spokenByMe
                    // updates to the user grounding model based on context
                    Pair<SemanticsModel, SemanticsModel> newUserModel =
                            interpretClarificationDialog(spokenByMe, understoodByThem.get(DUHypothesisID),
                            utteranceHypotheses.get(utteranceHypothesisID));
                    newSpokenByThem.put(newDUHypothesisID, newUserModel.getLeft());
                    newUnderstoodByThem.put(newDUHypothesisID, newUserModel.getRight());
                    // probability is the product of old DU and utterance
                    newHypothesisDistribution.put(newDUHypothesisID,
                            hypothesisDistribution.get(DUHypothesisID) *
                                    weights.get(utteranceHypothesisID));
                }
            }
        }
        List<String> hypothesesRemoved = newHypothesisDistribution.sortedHypotheses();
        if (hypothesesRemoved.size() > BEAM_WIDTH)
            hypothesesRemoved = hypothesesRemoved.subList(BEAM_WIDTH, hypothesesRemoved.size());
        else
            hypothesesRemoved = new LinkedList<>();
        hypothesesRemoved.stream().forEach(newHypothesisDistribution::remove);
        hypothesesRemoved.stream().forEach(newSpokenByThem::remove);
        hypothesesRemoved.stream().forEach(newUnderstoodByThem::remove);

        newHypothesisDistribution.normalize();
        hypothesisDistribution = newHypothesisDistribution;
        spokenByThem = newSpokenByThem;
        understoodByThem = newUnderstoodByThem;
    }

    /*
    * Return a pair of SemanticsModels <newSpokenByUser, newUnderstoodByUser>,
    * which is an update to these models based on interpreting a new user utterance in context
    * */
    public Pair<SemanticsModel, SemanticsModel> interpretClarificationDialog(SemanticsModel spokenBySystem,
                                                                             SemanticsModel understoodByUser,
                                                                             SemanticsModel userUtterance){
        SemanticsModel newSpokenByUser = userUtterance.deepCopy();
        SemanticsModel newUnderstoodByUser = understoodByUser.deepCopy();
        // todo: add clarification interpretation
        return new ImmutablePair<>(newSpokenByUser, newUnderstoodByUser);
    }

    public SemanticsModel simpleSemanticsUpdate(SemanticsModel old, SemanticsModel delta){
        SemanticsModel ans = old.deepCopy();
        // todo: add changes according to delta
        return ans;
    }


    public StringDistribution getHypothesisDistribution() {
        return hypothesisDistribution;
    }

    public void setHypothesisDistribution(StringDistribution hypothesisDistribution) {
        this.hypothesisDistribution = hypothesisDistribution;
    }

    public Map<String, SemanticsModel> getSpokenByThem() {
        return spokenByThem;
    }

    public void setSpokenByThem(Map<String, SemanticsModel> spokenByThem) {
        this.spokenByThem = spokenByThem;
    }

    public Map<String, SemanticsModel> getUnderstoodByThem() {
        return understoodByThem;
    }

    public void setUnderstoodByThem(Map<String, SemanticsModel> understoodByThem) {
        this.understoodByThem = understoodByThem;
    }

    public SemanticsModel getSpokenByMe() {
        return spokenByMe;
    }

    public void setSpokenByMe(SemanticsModel spokenByMe) {
        this.spokenByMe = spokenByMe;
    }

    public float getTimeOfLastActByThem() {
        return timeOfLastActByThem;
    }

    public void setTimeOfLastActByThem(float timeOfLastActByThem) {
        this.timeOfLastActByThem = timeOfLastActByThem;
    }

    public float getTimeOfLastActByMe() {
        return timeOfLastActByMe;
    }

    public void setTimeOfLastActByMe(float timeOfLastActByMe) {
        this.timeOfLastActByMe = timeOfLastActByMe;
    }


}

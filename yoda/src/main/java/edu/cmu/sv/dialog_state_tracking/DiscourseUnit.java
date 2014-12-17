package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.DialogTask;
import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnit {
    SemanticsModel spokenByMe;
    SemanticsModel spokenByThem;
    Long timeOfLastActByThem;
    Long timeOfLastActByMe;
    String initiator;
    SemanticsModel groundTruth; // if self-initiated
    SemanticsModel groundInterpretation; // if other-initiated

    public ActionAnalysis actionAnalysis;

    /*
    * Performs and contains results for analysis of the action-related consequences of a discourse unit
    * */
    public static class ActionAnalysis{
        public Double ynqTruth;

        public ActionAnalysis deepCopy(){
            ActionAnalysis ans = new ActionAnalysis();
            ans.ynqTruth = ynqTruth;
            return ans;
        }

    }

    public void analyse(YodaEnvironment yodaEnvironment){
        try {
            String dialogActString = (String) spokenByThem.newGetSlotPathFiller("dialogAct");
            Class<? extends DialogTask> taskClass = DialogRegistry.dialogTaskMap.
                    get(DialogRegistry.dialogActNameMap.get(dialogActString));
            taskClass.newInstance().analyse(this, yodaEnvironment);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    /**
     * Performs and contains results for analysis of the discourse-update-related consequences of a discourse unit
     *
     * Analysis methods may throw AssertException if the type of analysis requested is not applicable
     * modifying the stored analysis result JSONObjects should modify the source discourse unit
     * */
    public static class DiscourseAnalysis {
        private DiscourseUnit discourseUnit;
        private YodaEnvironment yodaEnvironment;

        // parameters primarily used for dialog state tracking
        public String suggestionPath;
        public JSONObject suggestedContent;
        public JSONObject groundedSuggestionIndividual;
        public Double descriptionMatch;

        public DiscourseAnalysis(DiscourseUnit discourseUnit, YodaEnvironment yodaEnvironment) {
            this.discourseUnit = discourseUnit;
            this.yodaEnvironment = yodaEnvironment;
        }

        public boolean ungroundedByAct(Class<? extends DialogAct> ungroundingAction) throws Assert.AssertException {
            if (discourseUnit.initiator.equals("user")){
                Assert.verify(discourseUnit.spokenByMe!= null);
                Assert.verify(discourseUnit.groundTruth != null);
                return discourseUnit.groundTruth.newGetSlotPathFiller("dialogAct").
                        equals(ungroundingAction.getSimpleName());
            } else { //discourseUnitHypothesis.initiator.equals("system")
                Assert.verify(discourseUnit.spokenByThem!= null);
                Assert.verify(discourseUnit.groundInterpretation != null);
                return discourseUnit.groundInterpretation.newGetSlotPathFiller("dialogAct").
                        equals(ungroundingAction.getSimpleName());
            }
        }

        public void analyseSuggestions() throws Assert.AssertException {
            if (discourseUnit.initiator.equals("user")) {
                Set<String> suggestionPaths = discourseUnit.getSpokenByMe().
                        findAllPathsToClass(Suggested.class.getSimpleName());
                Assert.verify(suggestionPaths.size() == 1);
                suggestionPath = new LinkedList<>(suggestionPaths).get(0);
                suggestedContent = (JSONObject) discourseUnit.getSpokenByMe().deepCopy().
                        newGetSlotPathFiller(suggestionPath + "." + HasValue.class.getSimpleName());
                groundedSuggestionIndividual = (JSONObject) discourseUnit.groundInterpretation.
                        newGetSlotPathFiller(suggestionPath);
            } else { //discourseUnitHypothesis.initiator.equals("system")
                Set<String> suggestionPaths = discourseUnit.getSpokenByThem().
                        findAllPathsToClass(Suggested.class.getSimpleName());
                Assert.verify(suggestionPaths.size() == 1);
                suggestionPath = new LinkedList<>(suggestionPaths).get(0);
                suggestedContent = (JSONObject) discourseUnit.getSpokenByThem().deepCopy().
                        newGetSlotPathFiller(suggestionPath + "." + HasValue.class.getSimpleName());
                groundedSuggestionIndividual = (JSONObject) discourseUnit.groundTruth.
                        newGetSlotPathFiller(suggestionPath);
            }
            descriptionMatch = ReferenceResolution.descriptionMatch(yodaEnvironment,
                    groundedSuggestionIndividual, suggestedContent);
            if (descriptionMatch==null)
                descriptionMatch=0.0;
        }



    }

    public Long getMostRecentContributionTime(){
        Long ans = (long) 0;
        if (timeOfLastActByMe!=null)
            ans = Long.max(timeOfLastActByMe, ans);
        if (timeOfLastActByThem!=null)
            ans = Long.max(timeOfLastActByThem, ans);
        return ans;
    }

    public Pair<Map<String, DiscourseUnit>, StringDistribution> ground(YodaEnvironment yodaEnvironment){
        try {
            String dialogActString = (String) spokenByThem.newGetSlotPathFiller("dialogAct");
            Class<? extends DialogTask> taskClass = DialogRegistry.dialogTaskMap.
                    get(DialogRegistry.dialogActNameMap.get(dialogActString));
            return taskClass.newInstance().ground(this, yodaEnvironment);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public Object getFromInitiator(String slotPath){
        if (initiator.equals("user")){
            return spokenByThem.newGetSlotPathFiller(slotPath);
        } else { //initiator.equals("system")
            return spokenByMe.newGetSlotPathFiller("verb");
        }
    }

    public DiscourseUnit deepCopy(){
        DiscourseUnit ans = new DiscourseUnit();
        if (spokenByMe!=null)
            ans.spokenByMe = spokenByMe.deepCopy();
        if (spokenByThem!=null)
            ans.spokenByThem = spokenByThem.deepCopy();
        ans.timeOfLastActByMe = timeOfLastActByMe;
        ans.timeOfLastActByThem = timeOfLastActByThem;
        ans.initiator = initiator;
        if (groundTruth!=null)
            ans.groundTruth = groundTruth.deepCopy();
        if (groundInterpretation!=null)
            ans.groundInterpretation = groundInterpretation.deepCopy();
        ans.actionAnalysis = actionAnalysis.deepCopy();
        return ans;
    }

    public SemanticsModel getSpokenByMe() {
        return spokenByMe;
    }

    public void setSpokenByMe(SemanticsModel spokenByMe) {
        this.spokenByMe = spokenByMe;
    }

    public SemanticsModel getSpokenByThem() {
        return spokenByThem;
    }

    public void setSpokenByThem(SemanticsModel spokenByThem) {
        this.spokenByThem = spokenByThem;
    }

    public Long getTimeOfLastActByThem() {
        return timeOfLastActByThem;
    }

    public void setTimeOfLastActByThem(Long timeOfLastActByThem) {
        this.timeOfLastActByThem = timeOfLastActByThem;
    }

    public Long getTimeOfLastActByMe() {
        return timeOfLastActByMe;
    }

    public void setTimeOfLastActByMe(Long timeOfLastActByMe) {
        this.timeOfLastActByMe = timeOfLastActByMe;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public SemanticsModel getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(SemanticsModel groundTruth) {
        this.groundTruth = groundTruth;
    }

    public SemanticsModel getGroundInterpretation() {
        return groundInterpretation;
    }

    public void setGroundInterpretation(SemanticsModel groundInterpretation) {
        this.groundInterpretation = groundInterpretation;
    }

    @Override
    public String toString() {
        return "DiscourseUnitHypothesis{" +
                "initiator='" + initiator +
                "', timeOfLastActByThem=" + timeOfLastActByThem +
                ", timeOfLastActByMe=" + timeOfLastActByMe +
                "\nspokenByMe=" + spokenByMe +
                "\nspokenByThem=" + spokenByThem +
                "\ngroundTruth=" + groundTruth +
                "\ngroundInterpretation=" + groundInterpretation +
                '}';
    }

}

package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.Set;

/**
 * Performs and contains results for analysis of the discourse-update-related consequences of a discourse unit
 *
 * Analysis methods may throw AssertException if the type of analysis requested is not applicable
 * modifying the stored analysis result JSONObjects should modify the source discourse unit
 * */
public class DiscourseAnalysis {
    private DiscourseUnit discourseUnit;
    private YodaEnvironment yodaEnvironment;

    public String requestPath;

    public String suggestionPath;
    public JSONObject suggestedContent;
    public JSONObject groundedSuggestionIndividual;
    public Double descriptionMatch;

    public boolean groundMatch;

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

    public void analyseValidity() throws Assert.AssertException {
        Assert.verify(discourseUnit.getFromInitiator("verb") != null);
    }

    public void analyseSlotFilling() throws Assert.AssertException {
        analyseValidity();
        if (discourseUnit.initiator.equals("user")){
            Assert.verify(discourseUnit.spokenByMe!=null);
            Assert.verify(discourseUnit.groundTruth != null);
            Set<String> requestPaths = discourseUnit.spokenByMe.findAllPathsToClass(Requested.class.getSimpleName());
            Assert.verify(requestPaths.size()==1);
            requestPath = new LinkedList<>(requestPaths).get(0);
        }
    }

    public void analyseSuggestions() throws Assert.AssertException {
        analyseValidity();
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

    public void analyseGround() throws Assert.AssertException {
        analyseValidity();
        if (discourseUnit.groundTruth==null || discourseUnit.groundInterpretation==null) {
            groundMatch = false;
            return;
        }
        groundMatch = SemanticsModel.contentEqual(discourseUnit.groundInterpretation, discourseUnit.groundTruth);
    }

}

package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.database.Sensor;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.*;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by David Cohen on 9/19/14.
 */
public class DialogStateTracker implements Runnable {
    private static Logger logger = Logger.getLogger("yoda.dialog_state_tracking.DialogStateTracker");
    private static FileHandler fh;
    static {
        try {
            if (YodaEnvironment.mongoLoggingActive){
                MongoLogHandler handler = new MongoLogHandler();
                logger.addHandler(handler);
            } else {
                FileHandler fh;
                fh = new FileHandler("DialogStateTracker.log");
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    static Set<Class <? extends DialogStateUpdateInference>> updateInferences;
    static {
        updateInferences = new HashSet<>();
        updateInferences.add(PresentInference.class);
        updateInferences.add(AnswerInference.class);
        updateInferences.add(GiveGroundingSuggestionInference.class);
        updateInferences.add(ConfirmGroundingSuggestionInference.class);
        updateInferences.add(RejectGroundingSuggestionInference.class);
        updateInferences.add(ReiterateIgnoreGroundingSuggestionInference.class);
        updateInferences.add(RequestSlotInference.class);
        updateInferences.add(ElaborateInference.class);
        updateInferences.add(TakeRequestedActionInference.class);
        updateInferences.add(MisunderstoodTurnInference.class);
        updateInferences.add(RequestFixMisunderstandingInference.class);
        updateInferences.add(DialogLostInference.class);
        updateInferences.add(ResetLostDialogInference.class);
    }

    YodaEnvironment yodaEnvironment;
    NBestDistribution<DialogState> dialogStateNBestDistribution;
//    Map<String, DialogState> hypothesisMap;
//    StringDistribution hypothesisDistribution;

    public DialogStateTracker(YodaEnvironment yodaEnvironment){
        this.yodaEnvironment = yodaEnvironment;
        dialogStateNBestDistribution = new NBestDistribution<>();
//        hypothesisDistribution = new StringDistribution();
//        hypothesisMap = new HashMap<>();
        dialogStateNBestDistribution.put(new DialogState(), 1.0);
        this.yodaEnvironment.DmInputQueue.add(dialogStateNBestDistribution);
    }

    private void updateDialogState(Turn turn, long timeStamp){
        try {
            JSONObject turnStartRecord = MongoLogHandler.createEventRecord("dst_turn_input");
            turnStartRecord.put("speaker", turn.speaker);
            logger.info(turnStartRecord.toJSONString());

            // validate input
            if (turn.hypotheses != null) {
                for (SemanticsModel sm : turn.hypotheses.values()) {
                    sm.validateSLUHypothesis();
                }
            }

            NBestDistribution<DialogState> newDialogStateDistribution = new NBestDistribution<>();
//            int newDialogStateHypothesisCounter = 0;
//            StringDistribution newHypothesisDistribution = new StringDistribution();
//            Map<String, DialogState> newHypotheses = new HashMap<>();


            // synchronize so that the RefRes cache is unique to this turn
            synchronized (ReferenceResolution.lock) {
                ReferenceResolution.clearCache();
                for (Class<? extends DialogStateUpdateInference> updateInferenceClass : updateInferences) {
                    if (ReferenceResolution.PRINT_CACHING_DEBUG_OUTPUT)
                        System.err.println("DialogStateTracker: updateInferenceClass:" + updateInferenceClass);
                    for (DialogState currentDialogState : dialogStateNBestDistribution.keySet()) {
                        NBestDistribution<DialogState> inferredUpdatedState = updateInferenceClass.newInstance().
                                applyAll(yodaEnvironment, currentDialogState, turn, timeStamp);
                        for (DialogState newDialogState : inferredUpdatedState.keySet()) {
                            newDialogStateDistribution.put(newDialogState, inferredUpdatedState.get(newDialogState) *
                                    dialogStateNBestDistribution.get(currentDialogState));
                        }
                    }
                }

                dialogStateNBestDistribution = HypothesisSetManagement.keepRatioDistribution(newDialogStateDistribution, .05, 5);
                dialogStateNBestDistribution.normalize();
                ReferenceResolution.updateSalience(yodaEnvironment, dialogStateNBestDistribution);
                ReferenceResolution.clearCache();
            }

//            // generate log record
//            JSONObject loopCompleteRecord = MongoLogHandler.createEventRecord("dst_loop_complete");
//            loopCompleteRecord.put("n_hypotheses", hypothesisMap.size());
//            loopCompleteRecord.put("hypothesis_distribution", new JSONObject(hypothesisDistribution.getInternalDistribution()));
//
//            JSONObject dialogStateHypothesesJSON = new JSONObject();
//            for (String key : hypothesisMap.keySet()){
//                DialogState hypothesisState = hypothesisMap.get(key);
//                JSONObject dialogStateHypothesisJSON = new JSONObject();
//
//                JSONObject discourseUnitsJSON = new JSONObject();
//                Map<String, DiscourseUnit> discourseUnitMap = hypothesisState.getDiscourseUnitHypothesisMap();
//                for (String duKey : discourseUnitMap.keySet()){
//                    DiscourseUnit discourseUnit = discourseUnitMap.get(duKey);
//                    JSONObject discourseUnitJSON = new JSONObject();
//                    discourseUnitJSON.put("initiator", discourseUnit.getInitiator());
//                    if (discourseUnit.getSpokenByThem()!=null) {
//                        discourseUnitJSON.put("spoken_by_them", discourseUnit.getSpokenByThem().getInternalRepresentation());
//                        discourseUnitJSON.put("ground_interpretation", discourseUnit.getGroundInterpretation().getInternalRepresentation());
//                    }
//                    if (discourseUnit.getSpokenByMe()!=null) {
//                        discourseUnitJSON.put("spoken_by_me", discourseUnit.getSpokenByMe().getInternalRepresentation());
//                        discourseUnitJSON.put("ground_truth", discourseUnit.getGroundTruth().getInternalRepresentation());
//                    }
//                    discourseUnitsJSON.put(duKey, discourseUnitJSON);
//                }
//
//                JSONArray argumentationLinks = new JSONArray();
//                for (DialogState.ArgumentationLink link : hypothesisState.argumentationLinks){
//                    JSONObject argumentationLinkJSON = new JSONObject();
//                    argumentationLinkJSON.put("predecessor", link.getPredecessor());
//                    argumentationLinkJSON.put("successor", link.getSuccessor());
//                    argumentationLinks.add(argumentationLinkJSON);
//                }
//
//                    dialogStateHypothesisJSON.put("discourse_units", discourseUnitsJSON);
//                dialogStateHypothesisJSON.put("argumentation_links", argumentationLinks);
//                dialogStateHypothesesJSON.put(key, dialogStateHypothesisJSON);
//            }
//            loopCompleteRecord.put("hypotheses", dialogStateHypothesesJSON);
//            logger.info(loopCompleteRecord.toJSONString());

            yodaEnvironment.DmInputQueue.add(dialogStateNBestDistribution);
            if (turn.speaker.equals("system"))
                yodaEnvironment.dm.detectSystemAction();

//        System.out.println("End of DialogStateTracker2.updateDialogStateTurn. discourseUnit.hypotheses:\n");
//        for (DiscourseUnit2.DialogStateHypothesis hyp : discourseUnit.hypotheses.values()){
//            System.out.println(hyp+"\n");
//        }
        } catch (IllegalAccessException | InstantiationException e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void run() {
        while (true){
            for (Sensor sensor : yodaEnvironment.db.sensors){
                sensor.sense(yodaEnvironment);
            }
            try {
                Pair<Turn, Long> DstInput = yodaEnvironment.DstInputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (DstInput!=null) {
                    updateDialogState(DstInput.getKey(), DstInput.getValue());
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}

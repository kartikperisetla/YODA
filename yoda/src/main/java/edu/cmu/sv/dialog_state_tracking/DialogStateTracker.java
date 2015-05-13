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
import org.json.simple.JSONArray;
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
        updateInferences.add(DialogLostInference.class);
        updateInferences.add(OOCInference.class);
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
            synchronized (yodaEnvironment.db.connection) {
                ReferenceResolution.clearCache();
                for (Class<? extends DialogStateUpdateInference> updateInferenceClass : updateInferences) {
                    if (ReferenceResolution.PRINT_CACHING_DEBUG_OUTPUT)
                        System.err.println("DialogStateTracker: updateInferenceClass:" + updateInferenceClass);
                    for (DialogState currentDialogState : dialogStateNBestDistribution.keySet()) {
                        NBestDistribution<DialogState> inferredUpdatedState = updateInferenceClass.newInstance().
                                applyAll(yodaEnvironment, currentDialogState, turn, timeStamp);
                        for (DialogState newDialogState : inferredUpdatedState.keySet()) {
                            newDialogState.clean();
                            newDialogStateDistribution.put(newDialogState, inferredUpdatedState.get(newDialogState) *
                                    dialogStateNBestDistribution.get(currentDialogState));
                        }
                    }
                }

                // if
                if (newDialogStateDistribution.internalDistribution.size()==0) {
                    System.err.println("DialogStateTracker: DST has no dialog state hypotheses. Starting over from empty dialog state.");
                    newDialogStateDistribution.internalDistribution.put(new DialogState(), 1.0);
                }

                dialogStateNBestDistribution = HypothesisSetManagement.keepRatioDistribution(newDialogStateDistribution, .05, 5);
                dialogStateNBestDistribution.normalize();
                ReferenceResolution.updateSalience(yodaEnvironment, dialogStateNBestDistribution);
                ReferenceResolution.clearCache();
            }

//            // generate log record
            JSONObject loopCompleteRecord = MongoLogHandler.createEventRecord("dst_loop_complete");
            loopCompleteRecord.put("speaker", turn.speaker);
            loopCompleteRecord.put("n_hypotheses", dialogStateNBestDistribution.internalDistribution.size());
            JSONArray dialogStateDistributionDescription = new JSONArray();
            for (DialogState dialogState : dialogStateNBestDistribution.keySet()){
                JSONObject thisDialogStateDescription = new JSONObject();
                DiscourseUnit activeDu = dialogState.activeDiscourseUnit();
                String activeDuInitiator = null;
                String activeDuDialogAct = null;
                String activeVerb = null;
                if (activeDu!=null){
                    activeDuInitiator = activeDu.initiator;
                    activeDuDialogAct = (String) activeDu.getFromInitiator("dialogAct");
                    activeVerb = (String) activeDu.getFromInitiator("verb.class");
                }
                thisDialogStateDescription.put("initiator", activeDuInitiator);
                thisDialogStateDescription.put("dA", activeDuDialogAct);
                thisDialogStateDescription.put("verb", activeVerb);
                thisDialogStateDescription.put("p", dialogStateNBestDistribution.get(dialogState));
                dialogStateDistributionDescription.add(thisDialogStateDescription);
            }
            loopCompleteRecord.put("NBestDialogStates", dialogStateDistributionDescription);

            logger.info(loopCompleteRecord.toJSONString());

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
            synchronized (yodaEnvironment.db.connection) {
                for (Sensor sensor : yodaEnvironment.db.sensors) {
                    sensor.sense(yodaEnvironment);
                }
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

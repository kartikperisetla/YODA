package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SpokenLanguageUnderstander;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by David Cohen on 11/21/14.
 *
 * A simple keyword-based SLU system for quick-n-dirty tests
 *
 */
public class RegexPlusKeywordUnderstander implements SpokenLanguageUnderstander{
    private static Logger logger = Logger.getLogger("yoda.spoken_language_understanding.RegexPlusKeywordUnderstander");
    static {
        try {
            if (YodaEnvironment.mongoLoggingActive){
                MongoLogHandler handler = new MongoLogHandler();
                logger.addHandler(handler);
            } else {
                FileHandler fh;
                fh = new FileHandler("RegexPlusKeywordUnderstander.log");
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static NounPhraseInterpreter nounPhraseInterpreter = new NounPhraseInterpreter();
    static Set<MiniLanguageInterpreter> languageInterpreters = new HashSet<>();
    static {
        // add regex interpreters
        for (Class<? extends Adjective> adjectiveClass : OntologyRegistry.adjectiveClasses){
            languageInterpreters.add(new YnqAdjectiveRegexInterpreter(adjectiveClass));
        }
        for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualityClasses){
            languageInterpreters.add(new WhqAdjectiveRegexInterpreter(qualityClass));
        }
        for (Class<? extends Verb> verbClass : OntologyRegistry.verbClasses){
            languageInterpreters.add(new CommandRegexInterpreter(verbClass));
            languageInterpreters.add(new CommandKeywordInterpreter(verbClass));
        }
        languageInterpreters.add(new NamedEntityFragmentInterpreter(PointOfInterest.class));

        // add simple string match interpreters
        languageInterpreters.add(
                new SimpleStringMatchInterpreter("{\"dialogAct\":\"Accept\"}",
                        new HashSet(Arrays.asList("yes","yeah","yep","right","correct","yup","yes sir","sure","uh huh")),
                        1.0));

        languageInterpreters.add(
                new SimpleStringMatchInterpreter("{\"dialogAct\":\"Reject\"}",
                        new HashSet(Arrays.asList("no", "nope", "negative", "i don't think so", "wrong", "not really", "not")),
                        1.0));

        // add keyword interpreters
    }


    @Override
    public void process1BestAsr(String asrResult) {
        JSONObject inputRecord = MongoLogHandler.createEventRecord("asr_input_event");
        inputRecord.put("asr_result", asrResult);
        logger.info(inputRecord.toJSONString());

        Map<String, SemanticsModel> hypotheses = new HashMap<>();
        StringDistribution hypothesisDistribution = new StringDistribution();
        int hypothesisId = 0;

        // incorporate regex templates
        for (MiniLanguageInterpreter miniLanguageInterpreter : languageInterpreters){
            Pair<JSONObject, Double> interpretation = miniLanguageInterpreter.interpret(asrResult, yodaEnvironment);
            if (interpretation==null)
                continue;
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(interpretation.getKey()));
            hypothesisDistribution.put("hyp" + hypothesisId, interpretation.getRight());
            hypothesisId++;
        }

        // create a turn and update the DST
        hypothesisDistribution.normalize();
        Turn newTurn = new Turn("user", null, null, hypotheses, hypothesisDistribution);
        Calendar calendar = Calendar.getInstance();
        yodaEnvironment.DstInputQueue.add(new ImmutablePair<>(newTurn, calendar.getTimeInMillis()));

        Map<String, JSONObject> JSONHypotheses = new HashMap<>();
        hypotheses.keySet().forEach(x -> JSONHypotheses.put(x, hypotheses.get(x).getInternalRepresentation()));
        JSONObject outputRecord = MongoLogHandler.createEventRecord("slu_output_record");
        outputRecord.put("hypothesis_distribution", hypothesisDistribution.getInternalDistribution());
        outputRecord.put("hypotheses", new JSONObject(JSONHypotheses));
        logger.info(outputRecord.toJSONString());
    }

    @Override
    public void processNBestAsr(StringDistribution asrNBestResult) {
        process1BestAsr(asrNBestResult.getTopHypothesis());
    }

    YodaEnvironment yodaEnvironment;
    public RegexPlusKeywordUnderstander(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }
}

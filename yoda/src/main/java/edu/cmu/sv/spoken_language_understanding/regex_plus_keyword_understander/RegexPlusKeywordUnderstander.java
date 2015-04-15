package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SpokenLanguageUnderstander;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.utils.NBestDistribution;
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
    public static boolean switchToMultiInterpretersWhenPossible = true;

    public NounPhraseInterpreter nounPhraseInterpreter;
    public TimeInterpreter timeInterpreter;
    YodaEnvironment yodaEnvironment;
    Set<MiniLanguageInterpreter> languageInterpreters = new HashSet<>();
    Set<MiniMultiLanguageInterpreter> multiLanguageInterpreters = new HashSet<>();

    // define parameters for the SLU component
    public static final double keywordInterpreterWeight = 0.5;
    public static final double regexInterpreterWeight = 0.5;
    public static final double namedEntityFragmentWeight = 0.1;
    public static final double nounPhraseInterpreterWeight = 1.0;
    public static final double timeInterpreterWeight = 1.0;
    public static final double simpleStringMatchInterpreterWeight = 1.0;
    public static final double secondaryRegexMatchWeight = 0.3;
    public static final double requiredRoleWeight = 0.9;
    public static final double optionalRoleWeight = 0.7;


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

    public RegexPlusKeywordUnderstander(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public void constructTemplates(){
        nounPhraseInterpreter = new NounPhraseInterpreter(yodaEnvironment);
        timeInterpreter = new TimeInterpreter(yodaEnvironment);

        // add regex interpreters
        languageInterpreters.add(new YnqExistRegexInterpreter());
        for (Class<? extends Adjective> adjectiveClass : Ontology.adjectiveClasses){
            languageInterpreters.add(new YnqHasPropertyRegexInterpreter(adjectiveClass, yodaEnvironment));
        }
        for (Class<? extends TransientQuality> qualityClass : Ontology.qualityClasses){
            languageInterpreters.add(new WhqHasPropertyRegexInterpreter(qualityClass, yodaEnvironment));
        }
        for (Class<? extends Verb> verbClass : Ontology.verbClasses){
            if (switchToMultiInterpretersWhenPossible)
                multiLanguageInterpreters.add(new CommandMultiInterpreter(verbClass, yodaEnvironment));
            else
                languageInterpreters.add(new CommandRegexInterpreter(verbClass, yodaEnvironment));
            languageInterpreters.add(new CommandKeywordInterpreter(verbClass, yodaEnvironment));
        }
        languageInterpreters.add(new NamedEntityFragmentInterpreter(PointOfInterest.class));
        languageInterpreters.add(new NounPhraseFragmentInterpreter(nounPhraseInterpreter));
        languageInterpreters.add(new TimeFragmentInterpreter(timeInterpreter));

        // add simple string match interpreters
        languageInterpreters.add(
                new SimpleStringMatchInterpreter("{\"dialogAct\":\"Accept\"}",
                        new HashSet(Arrays.asList("yes","yeah","yep","right","correct","yup","yes sir","sure","uh huh"))));

        languageInterpreters.add(
                new SimpleStringMatchInterpreter("{\"dialogAct\":\"Reject\"}",
                        new HashSet(Arrays.asList("no", "nope", "negative", "i don't think so", "wrong", "not really", "not"))));


    }


    @Override
    public void process1BestAsr(String asrResult) {
        JSONObject inputRecord = MongoLogHandler.createEventRecord("asr_input_event");
        inputRecord.put("asr_result", asrResult);
        logger.info(inputRecord.toJSONString());

        Map<String, SemanticsModel> hypotheses = new HashMap<>();
        StringDistribution hypothesisDistribution = new StringDistribution();
        int hypothesisId = 0;

        // synchronize so that the RefRes cache is unique to this utterance
        synchronized (ReferenceResolution.lock) {
            ReferenceResolution.clearCache();
            // incorporate mini-interpreters
            for (MiniLanguageInterpreter miniLanguageInterpreter : languageInterpreters) {
                Pair<JSONObject, Double> interpretation = miniLanguageInterpreter.interpret(Tokenizer.tokenize(asrResult), yodaEnvironment);
                if (interpretation == null)
                    continue;
                hypotheses.put("hyp" + hypothesisId, new SemanticsModel(interpretation.getKey()));
                hypothesisDistribution.put("hyp" + hypothesisId, interpretation.getRight());
                hypothesisId++;
            }

            // incorporate mini multi-interpreters
            for (MiniMultiLanguageInterpreter multiLanguageInterpreter : multiLanguageInterpreters) {
                NBestDistribution<JSONObject> interpretation = multiLanguageInterpreter.interpret(Tokenizer.tokenize(asrResult), yodaEnvironment);
                if (interpretation == null)
                    continue;
                for (JSONObject key : interpretation.keySet()) {
                    hypotheses.put("hyp" + hypothesisId, new SemanticsModel(key));
                    hypothesisDistribution.put("hyp" + hypothesisId, interpretation.get(key));
                    hypothesisId++;
                }
            }
            ReferenceResolution.clearCache();
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

}

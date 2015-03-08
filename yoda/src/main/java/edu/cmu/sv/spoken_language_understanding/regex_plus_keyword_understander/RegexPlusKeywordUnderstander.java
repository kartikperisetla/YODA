package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.poi_types.Bars;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.poi_types.Mexican;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.poi_types.Restaurants;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.poi_types.Thai;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SpokenLanguageUnderstander;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
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
    public NounPhraseInterpreter nounPhraseInterpreter;
    YodaEnvironment yodaEnvironment;
    Set<MiniLanguageInterpreter> languageInterpreters = new HashSet<>();

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

        // add regex interpreters
        languageInterpreters.add(new YnqExistRegexInterpreter());
        for (Class<? extends Adjective> adjectiveClass : Ontology.adjectiveClasses){
            languageInterpreters.add(new YnqHasPropertyRegexInterpreter(adjectiveClass, yodaEnvironment));
        }
        for (Class<? extends TransientQuality> qualityClass : Ontology.qualityClasses){
            languageInterpreters.add(new WhqHasPropertyRegexInterpreter(qualityClass, yodaEnvironment));
        }
        for (Class<? extends Verb> verbClass : Ontology.verbClasses){
            languageInterpreters.add(new CommandRegexInterpreter(verbClass, yodaEnvironment));
            languageInterpreters.add(new CommandKeywordInterpreter(verbClass, yodaEnvironment));
        }
        languageInterpreters.add(new NamedEntityFragmentInterpreter(PointOfInterest.class));
        languageInterpreters.add(new NounPhraseFragmentInterpreter(nounPhraseInterpreter));

        // add simple string match interpreters
        languageInterpreters.add(
                new SimpleStringMatchInterpreter("{\"dialogAct\":\"Accept\"}",
                        new HashSet(Arrays.asList("yes","yeah","yep","right","correct","yup","yes sir","sure","uh huh")),
                        1.0));

        languageInterpreters.add(
                new SimpleStringMatchInterpreter("{\"dialogAct\":\"Reject\"}",
                        new HashSet(Arrays.asList("no", "nope", "negative", "i don't think so", "wrong", "not really", "not")),
                        1.0));


    }


    @Override
    public void process1BestAsr(String asrResult) {
        JSONObject inputRecord = MongoLogHandler.createEventRecord("asr_input_event");
        inputRecord.put("asr_result", asrResult);
        logger.info(inputRecord.toJSONString());

        Map<String, SemanticsModel> hypotheses = new HashMap<>();
        StringDistribution hypothesisDistribution = new StringDistribution();
        int hypothesisId = 0;


        List<String> words = Tokenizer.tokenize(asrResult);
        // 0: command give directions to rest (named entity)
        if (words.size() > 0 && words.get(0).equals("0")) {
            String rest = String.join(" ", words.subList(1, words.size()));
            String uri = yodaEnvironment.db.insertValue(rest);
            JSONObject namedEntity = SemanticsModel.parseJSON(Ontology.webResourceWrap(uri));
            SemanticsModel.wrap(namedEntity, "Noun", "HasName");
            JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"Destination\":" +
                    namedEntity.toJSONString() +
                    ",\"class\":\"GiveDirections\"}}");
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // give directions there
        else if (words.size() > 0 && words.get(0).equals("0b")) {
            JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"Destination\":{\"refType\":\"pronoun\",\"class\":\"PointOfInterest\"},\"class\":\"GiveDirections\"}}");
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // give directions
        else if (words.size() > 0 && words.get(0).equals("0c")) {
            JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"GiveDirections\"}}");
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // 1: command make reservation to rest (named entity)
        else if (words.size() > 0 && words.get(0).equals("1")) {
            String rest = String.join(" ", words.subList(1, words.size()));
            String uri = yodaEnvironment.db.insertValue(rest);
            JSONObject namedEntity = SemanticsModel.parseJSON(Ontology.webResourceWrap(uri));
            SemanticsModel.wrap(namedEntity, "Noun", "HasName");
            JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"Destination\":" +
                    namedEntity.toJSONString() +
                    ",\"class\":\"MakeReservation\"}}");
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // 1b: command make reservation there
        else if (words.size() > 0 && words.get(0).equals("1b")) {
            JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"Destination\":{\"refType\":\"pronoun\",\"class\":\"PointOfInterest\"},\"class\":\"MakeReservation\"}}");
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // 1c: command make reservation
        else if (words.size() > 0 && words.get(0).equals("1c")) {
            JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"MakeReservation\"}}");
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // 2 <ADJ> <CLS> rest: search for a ADJ / Cls near rest
        else if (words.size() > 0 && words.get(0).equals("2")) {
            String adjClass = words.get(1);
            String poiClass = words.get(2);
            String rest = String.join(" ", words.subList(3, words.size()));

            JSONObject ans = SemanticsModel.parseJSON("{}");
            ans.put("dialogAct", "YNQuestion");

            JSONObject verbObject = SemanticsModel.parseJSON("{}");
            verbObject.put("class", "Exist");

            JSONObject nearToObject = SemanticsModel.parseJSON("{}");
            if (rest.equals("it")) {
                nearToObject.put("class", "Noun");
                nearToObject.put("refType", "pronoun");
            } else {
                String uri = yodaEnvironment.db.insertValue(rest);
                JSONObject namedEntity = SemanticsModel.parseJSON(Ontology.webResourceWrap(uri));
                SemanticsModel.wrap(namedEntity, "Noun", "HasName");
                nearToObject = namedEntity;
            }

            JSONObject patientObject = SemanticsModel.parseJSON("{}");
            patientObject.put("class", "UnknownThingWithRoles");
            if (adjClass.equals("good"))
                patientObject.put("HasGoodness", SemanticsModel.parseJSON("{\"class\":\"Good\"}"));
            else if (adjClass.equals("cheap"))
                patientObject.put("HasExpensiveness", SemanticsModel.parseJSON("{\"class\":\"Cheap\"}"));
            else if (adjClass.equals("expensive"))
                patientObject.put("HasExpensiveness", SemanticsModel.parseJSON("{\"class\":\"Expensive\"}"));
            else if (adjClass.equals("none")) {
                // do nothing
            } else
                throw new Error();

            if (poiClass.equals("restaurants"))
                patientObject.put("class", Restaurants.class.getSimpleName());
            else if (poiClass.equals("mexican"))
                patientObject.put("class", Mexican.class.getSimpleName());
            else if (poiClass.equals("thai"))
                patientObject.put("class", Thai.class.getSimpleName());
            else if (poiClass.equals("bar"))
                patientObject.put("class", Bars.class.getSimpleName());
            else
                throw new Error();

            patientObject.put("HasDistance", SemanticsModel.parseJSON("{\"class\":\"IsCloseTo\", \"InRelationTo\":" +
                    nearToObject.toJSONString() + "}"));

            verbObject.put("Agent", patientObject);
            ans.put("verb", verbObject);

            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);

        }
        // 3 <ADJ> rest: ynq: is NE rest ADJ?
        else if (words.size() > 3 && words.get(0).equals("3")) {
            String adjClass = words.get(1);
            System.out.println(adjClass);
            String rest = String.join(" ", words.subList(2, words.size()));

            JSONObject ans = SemanticsModel.parseJSON("{}");
            ans.put("dialogAct", "YNQuestion");
            JSONObject verbObject = SemanticsModel.parseJSON("{}");
            verbObject.put("class", "HasProperty");

            JSONObject agentObject = SemanticsModel.parseJSON("{}");
            if (rest.equals("it")) {
                agentObject.put("class", "Noun");
                agentObject.put("refType", "pronoun");
            } else {
                String uri = yodaEnvironment.db.insertValue(rest);
                JSONObject namedEntity = SemanticsModel.parseJSON(Ontology.webResourceWrap(uri));
                SemanticsModel.wrap(namedEntity, "Noun", "HasName");
                agentObject = namedEntity;
            }

            JSONObject patientObject = SemanticsModel.parseJSON("{}");
            patientObject.put("class", "UnknownThingWithRoles");
            if (adjClass.equals("good"))
                patientObject.put("HasGoodness", SemanticsModel.parseJSON("{\"class\":\"Good\"}"));
            else if (adjClass.equals("cheap"))
                patientObject.put("HasExpensiveness", SemanticsModel.parseJSON("{\"class\":\"Cheap\"}"));
            else if (adjClass.equals("expensive"))
                patientObject.put("HasExpensiveness", SemanticsModel.parseJSON("{\"class\":\"Expensive\"}"));
            else
                throw new Error();

            verbObject.put("Agent", agentObject);
            verbObject.put("Patient", patientObject);
            ans.put("verb", verbObject);

            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // 3b quality rest: whq: how quality is NE(rest)?
        else if (words.size() > 3 && words.get(0).equals("3b")) {
            String qualityClass = words.get(1);
            System.out.println(qualityClass);
            String rest = String.join(" ", words.subList(2, words.size()));

            JSONObject ans = SemanticsModel.parseJSON("{}");
            ans.put("dialogAct", "WHQuestion");
            JSONObject verbObject = SemanticsModel.parseJSON("{}");
            verbObject.put("class", "HasProperty");

            JSONObject agentObject = SemanticsModel.parseJSON("{}");
            if (rest.equals("it")) {
                agentObject.put("class", "Noun");
                agentObject.put("refType", "pronoun");
            } else {
                String uri = yodaEnvironment.db.insertValue(rest);
                JSONObject namedEntity = SemanticsModel.parseJSON(Ontology.webResourceWrap(uri));
                SemanticsModel.wrap(namedEntity, "Noun", "HasName");
                agentObject = namedEntity;
            }

            JSONObject patientObject = SemanticsModel.parseJSON("{}");
            patientObject.put("class", "Requested");
            if (qualityClass.equals("good"))
                patientObject.put("HasValue", SemanticsModel.parseJSON("{\"class\":\"Goodness\"}"));
            else if (qualityClass.equals("cheap"))
                patientObject.put("HasValue", SemanticsModel.parseJSON("{\"class\":\"Expensiveness\"}"));
            else
                throw new Error();

            verbObject.put("Agent", agentObject);
            verbObject.put("Patient", patientObject);
            ans.put("verb", verbObject);

            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }


        // 4 rest : NP fragment
        else if (words.size() > 0 && words.get(0).equals("4")) {
            String rest = String.join(" ", words.subList(1, words.size()));
            Pair<JSONObject, Double> interpretation = new NounPhraseFragmentInterpreter(nounPhraseInterpreter).
                    interpret(rest, yodaEnvironment);
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(interpretation.getKey()));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        // 5: np fragment including name
        else if (words.size() > 0 && words.get(0).equals("5")) {
            String rest = String.join(" ", words.subList(1, words.size()));
            Pair<JSONObject, Double> npInterpretation = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                    nounPhraseInterpreter.interpret(rest, yodaEnvironment);
            String jsonString = "{\"dialogAct\":\"Fragment\"}";
            JSONObject ans = SemanticsModel.parseJSON(jsonString);
            ans.put("topic", npInterpretation.getLeft());
            hypotheses.put("hyp" + hypothesisId, new SemanticsModel(ans));
            hypothesisDistribution.put("hyp" + hypothesisId, 1.0);
        }
        else {
            // incorporate regex templates
            for (MiniLanguageInterpreter miniLanguageInterpreter : languageInterpreters) {
                Pair<JSONObject, Double> interpretation = miniLanguageInterpreter.interpret(asrResult, yodaEnvironment);
                if (interpretation == null)
                    continue;
                hypotheses.put("hyp" + hypothesisId, new SemanticsModel(interpretation.getKey()));
                hypothesisDistribution.put("hyp" + hypothesisId, interpretation.getRight());
                hypothesisId++;
            }
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

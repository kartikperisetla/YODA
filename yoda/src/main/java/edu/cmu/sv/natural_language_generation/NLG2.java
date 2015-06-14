package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.WebResource;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasURI;
import edu.cmu.sv.natural_language_generation.nlg2_phrase_generators.AdjectiveGenerator;
import edu.cmu.sv.natural_language_generation.nlg2_phrase_generators.DefiniteReferenceGenerator;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.AcceptTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.AcknowledgeTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.ConfirmGroundingSuggestionTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.DontKnowTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.top_level_templates.StatementTopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Acknowledge;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.DontKnow;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Statement;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ConfirmValueSuggestion;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * YODA's built-in NLG module
 */
public class NLG2 {
    private static Logger logger = Logger.getLogger("yoda.natural_language_generation.NaturalLanguageGenerator");
    private static FileHandler fh;
    static {
        try {
            fh = new FileHandler("NaturalLanguageGenerator.log");
            fh.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.addHandler(fh);
    }
    public static Random random = new Random();
    YodaEnvironment yodaEnvironment;
    static Map<String, TopLevelNLGTemplate> topLevelNLGTemplateMap = new HashMap<>();
    static {
        topLevelNLGTemplateMap.put(Accept.class.getSimpleName(), new AcceptTopLevelNLGTemplate());
        topLevelNLGTemplateMap.put(Acknowledge.class.getSimpleName(), new AcknowledgeTopLevelNLGTemplate());
        topLevelNLGTemplateMap.put(ConfirmValueSuggestion.class.getSimpleName(), new ConfirmGroundingSuggestionTopLevelNLGTemplate());
        topLevelNLGTemplateMap.put(DontKnow.class.getSimpleName(), new DontKnowTopLevelNLGTemplate());
        topLevelNLGTemplateMap.put(Statement.class.getSimpleName(), new StatementTopLevelNLGTemplate());
    }

    public NLG2(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public ImmutablePair<String, SemanticsModel> generateBestForSemantics(SemanticsModel model){
        return topLevelNLGTemplateMap.get(model.getSlotPathFiller("dialogAct")).generate(model, yodaEnvironment);
    }

    public static Logger getLogger() {
        return logger;
    }

    /*
    * Selects the appropriate internal template so that every dialog act generation routine doesn't need it
    * to be encoded locally.
    * */
    public static PhraseGenerationRoutine getAppropriatePhraseGenerationRoutine(JSONObject constraints){
        JSONObject tmpConstraints = SemanticsModel.parseJSON(constraints.toJSONString());
        if (constraints.get("class").equals(WebResource.class.getSimpleName()) &&
                constraints.keySet().size()==2 &&
                constraints.containsKey(HasURI.class.getSimpleName())){
            return new DefiniteReferenceGenerator();
        } else if (constraints.get("class").equals(UnknownThingWithRoles.class.getSimpleName()) &&
                constraints.keySet().size()==2 &&
                Adjective.class.isAssignableFrom(Ontology.thingNameMap.get(
                        ((JSONObject) constraints.get(
                                constraints.keySet().stream().filter(x -> !x.equals("class")).findAny().get())).
                        get("class")))){
            return new AdjectiveGenerator();
        }

        return null;

    }

}

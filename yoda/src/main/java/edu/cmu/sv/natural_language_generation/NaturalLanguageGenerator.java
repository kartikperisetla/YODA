package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.phrase_generators.*;
import edu.cmu.sv.natural_language_generation.top_level_templates.*;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ConfirmValueSuggestion;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestFixMisunderstanding;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRoleGivenRole;
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
public class NaturalLanguageGenerator {
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
        topLevelNLGTemplateMap.put(SearchReturnedNothing.class.getSimpleName(), new SearchReturnedNothingNLGTemplate());
        topLevelNLGTemplateMap.put(InformDialogLost.class.getSimpleName(), new InformDialogLostNLGTemplate());
        topLevelNLGTemplateMap.put(Reject.class.getSimpleName(), new RejectNLGTemplate());
        topLevelNLGTemplateMap.put(RequestFixMisunderstanding.class.getSimpleName(), new RequestFixMisunderstandingNLGTemplate());
        topLevelNLGTemplateMap.put(RequestConfirmValue.class.getSimpleName(), new RequestConfirmValueNLGTemplate());
        topLevelNLGTemplateMap.put(RequestRoleGivenRole.class.getSimpleName(), new RequestAgentNLGTemplate());
        topLevelNLGTemplateMap.put(RequestRole.class.getSimpleName(), new RequestRoleNLGTemplate());
        topLevelNLGTemplateMap.put(OOCRespondToRequestListOptions.class.getSimpleName(), new OOCRespondToRequestListOptionsNLGTemplate());
        topLevelNLGTemplateMap.put(OOCRespondToRequestSearchAlternative.class.getSimpleName(), new OOCRespondToRequestSearchAlternativeNLGTemplate());
    }

    public NaturalLanguageGenerator(YodaEnvironment yodaEnvironment) {
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
        Map<Object, JSONObject> prepositionDescriptors = new HashMap<>();
        Map<Object, JSONObject> adjectiveDescriptors = new HashMap<>();

        String className = (String) constraints.get("class");
        for (Object key : constraints.keySet()) {
            if (key.equals("refType") || key.equals("class"))
                continue;
            if (!Ontology.roleNameMap.containsKey(key))
                continue;
            String keyString = (String)key;
            if (Ontology.qualityNameMap.containsKey(keyString.substring(3)) && Ontology.qualityNameMap.get(keyString.substring(3)).secondArgumentClassConstraint==null)
                adjectiveDescriptors.put(key, (JSONObject) constraints.get(key));
            else if (Ontology.qualityNameMap.containsKey(keyString.substring(3)) && Ontology.qualityNameMap.get(keyString.substring(3)).secondArgumentClassConstraint!=null)
                prepositionDescriptors.put(key, (JSONObject) constraints.get(key));
        }

        if (className.equals(YodaSkeletonOntologyRegistry.webResource.name) &&
                constraints.keySet().size()==2 &&
                constraints.containsKey(YodaSkeletonOntologyRegistry.hasUri.name)){
            return new DefiniteReferenceGenerator();
        } else if (className.equals(YodaSkeletonOntologyRegistry.unknownThingWithRoles.name) &&
                constraints.keySet().size()==2 &&
                adjectiveDescriptors.size()>0) {
            return new AdjectiveGenerator();
        } else if (className.equals(YodaSkeletonOntologyRegistry.unknownThingWithRoles.name) &&
                constraints.keySet().size()==2 &&
                prepositionDescriptors.size()>0) {
            return new PrepositionGenerator();
        } else if (constraints.containsKey("class") &&
                constraints.keySet().size()==1) {
            return new NounClassGenerator();
//        } else if (constraints.containsKey("class") &&
//                constraints.containsKey(YodaSkeletonOntologyRegistry.hasName.name)){
//            return new NamedThingGenerator();
        } else if (constraints.containsKey("refType") &&
                constraints.get("refType").equals("indefinite")){
            return new IndefiniteDescriptionGenerator();
        }

        return null;

    }

}

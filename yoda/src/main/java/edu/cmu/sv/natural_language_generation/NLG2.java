package edu.cmu.sv.natural_language_generation;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    YodaEnvironment yodaEnvironment;
    static Map<String, TopLevelNLGTemplate> generationTemplates = new HashMap<>();
    static {
        generationTemplates.put(Accept.class.getSimpleName(), new AcceptTopLevelNLGTemplate());
        generationTemplates.put(Acknowledge.class.getSimpleName(), new AcknowledgeTopLevelNLGTemplate());
        generationTemplates.put(ConfirmValueSuggestion.class.getSimpleName(), new ConfirmGroundingSuggestionTopLevelNLGTemplate());
        generationTemplates.put(DontKnow.class.getSimpleName(), new DontKnowTopLevelNLGTemplate());
        generationTemplates.put(Statement.class.getSimpleName(), new StatementTopLevelNLGTemplate());
    }

    public NLG2(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public ImmutablePair<String, SemanticsModel> generateBestForSemantics(SemanticsModel model){
        return generationTemplates.get(model.getSlotPathFiller("dialogAct")).generate(model, yodaEnvironment);
    }

    public static Logger getLogger() {
        return logger;
    }

}

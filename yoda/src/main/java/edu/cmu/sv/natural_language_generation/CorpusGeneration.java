package edu.cmu.sv.natural_language_generation;

import com.google.common.collect.Iterables;
import edu.cmu.sv.database.dialog_task.ActionEnumeration;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.WHQuestion;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 12/30/14.
 */
public class CorpusGeneration {
    public static Map<String, SemanticsModel> generateCorpus(){
        Map<String, SemanticsModel> ans = new HashMap<>();

        String empty = "{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\"}";

        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String poiSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";


        Grammar.GrammarPreferences corpusPreferences = new Grammar.GrammarPreferences(.01, .2, 5, 2, 5, 5, 2, new HashMap<>());


        int numPOIs = 0;
        for (String uri : yodaEnvironment.db.runQuerySelectX(poiSelectionQuery)) {
            numPOIs ++;
            if (numPOIs > 100)
                break;

            // Generate YNQ for HasProperty where a PP is the property
            String YNQBaseString = "{\"dialogAct\":\"" + YNQuestion.class.getSimpleName() +
                    "\", \"verb\": {\"class\":\"" +
                    HasProperty.class.getSimpleName() + "\", \"" +
                    Agent.class.getSimpleName() + "\":" + empty + ", \"" +
                    Patient.class.getSimpleName() + "\":" + empty + "}}";
            for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualityClasses) {
                Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> qualityDescriptor =
                        OntologyRegistry.qualityDescriptors(qualityClass);
                for (Class<? extends ThingWithRoles> absoluteQualityDegreeClass : qualityDescriptor.getRight()) {
                    if (Preposition.class.isAssignableFrom(absoluteQualityDegreeClass)) {
                        // get 3 example URIs
                        Object[] childURIs = yodaEnvironment.nlg.randomData.nextSample(yodaEnvironment.db.runQuerySelectX(poiSelectionQuery), 3);
                        for (int i = 0; i < 1; i++) {

                            SemanticsModel ex0 = new SemanticsModel(YNQBaseString);
                            ex0.extendAndOverwriteAtPoint("verb." + Agent.class.getSimpleName(),
                                    new SemanticsModel(OntologyRegistry.WebResourceWrap(uri)));

                            JSONObject tmp = SemanticsModel.parseJSON(OntologyRegistry.WebResourceWrap((String) childURIs[i]));
                            SemanticsModel.wrap(tmp, absoluteQualityDegreeClass.getSimpleName(), InRelationTo.class.getSimpleName());
                            SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                    qualityDescriptor.getLeft().getSimpleName());
                            ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                    new SemanticsModel(tmp));

                            ans.putAll(yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences));
                        }
                    } else if (Adjective.class.isAssignableFrom(absoluteQualityDegreeClass)) {
                        SemanticsModel ex0 = new SemanticsModel(YNQBaseString);
                        ex0.extendAndOverwriteAtPoint("verb." + Agent.class.getSimpleName(),
                                new SemanticsModel(OntologyRegistry.WebResourceWrap(uri)));

                        JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + absoluteQualityDegreeClass.getSimpleName() + "\"}");
                        SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                qualityDescriptor.getLeft().getSimpleName());
                        ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                new SemanticsModel(tmp));

                        ans.putAll(yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences));
                    }
                }
            }

            // Generate WHQ for HasProperty where a PP is the property
            String WHQBaseString = "{\"dialogAct\":\"" + WHQuestion.class.getSimpleName() +
                    "\", \"verb\": {\"class\":\"" +
                    HasProperty.class.getSimpleName() + "\", \"" +
                    Agent.class.getSimpleName() + "\":" + empty + ", \"" +
                    Patient.class.getSimpleName() + "\":" + empty + "}}";
            for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualityClasses) {
                Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> qualityDescriptor =
                        OntologyRegistry.qualityDescriptors(qualityClass);

                for (Class<? extends ThingWithRoles> absoluteQualityDegreeClass : qualityDescriptor.getRight()) {
                    if (Preposition.class.isAssignableFrom(absoluteQualityDegreeClass)) {
                        // get 3 example URIs
                        Object[] childURIs = yodaEnvironment.nlg.randomData.nextSample(yodaEnvironment.db.runQuerySelectX(poiSelectionQuery), 3);
                        for (int i = 0; i < 1; i++) {

                            SemanticsModel ex0 = new SemanticsModel(WHQBaseString);
                            ex0.extendAndOverwriteAtPoint("verb." + Agent.class.getSimpleName(),
                                    new SemanticsModel(OntologyRegistry.WebResourceWrap(uri)));

                            JSONObject tmp = SemanticsModel.parseJSON(OntologyRegistry.WebResourceWrap((String) childURIs[i]));
                            SemanticsModel.wrap(tmp, absoluteQualityDegreeClass.getSimpleName(), InRelationTo.class.getSimpleName());
//                        SemanticsModel.wrap(tmp, Requested.class.getSimpleName(), HasValue.class.getSimpleName());
                            SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                    qualityDescriptor.getLeft().getSimpleName());
                            ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                    new SemanticsModel(tmp));

                            ans.putAll(yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences));
                        }
                    } else if (Adjective.class.isAssignableFrom(absoluteQualityDegreeClass)) {
                        SemanticsModel ex0 = new SemanticsModel(WHQBaseString);
                        ex0.extendAndOverwriteAtPoint("verb." + Agent.class.getSimpleName(),
                                new SemanticsModel(OntologyRegistry.WebResourceWrap(uri)));

                        JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + absoluteQualityDegreeClass.getSimpleName() + "\"}");
//                    SemanticsModel.wrap(tmp, Requested.class.getSimpleName(), HasValue.class.getSimpleName());
                        SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                qualityDescriptor.getLeft().getSimpleName());
                        ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                new SemanticsModel(tmp));

                        ans.putAll(yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences));
                    }
                }

            }


            SemanticsModel ex1 = new SemanticsModel("{\"dialogAct\": \"Fragment\", \"topic\": " +
                    OntologyRegistry.WebResourceWrap(uri) + "}");
            ans.putAll(yodaEnvironment.nlg.generateAll(ex1, yodaEnvironment, corpusPreferences));
        }
        return ans;
    }

    public static Map<String, SemanticsModel> generateCorpus2() {
        Map<String, SemanticsModel> ans = new HashMap<>();
        YodaEnvironment yodaEnvironment = YodaEnvironment.minimalLanguageProcessingEnvironment();

        try {
            // iterate through system actions
//            for (Class<? extends DialogAct> dialogActClass : Iterables.concat(DialogRegistry.argumentationDialogActs,
//                    DialogRegistry.clarificationDialogActs)) {
//                DialogAct dialogActInstance = dialogActClass.newInstance();
//                Set<Map<String, Object>> possibleBindings = ActionEnumeration.
//                        getPossibleIndividualBindings(dialogActInstance, yodaEnvironment, ActionEnumeration.FOCUS_CONSTRAINT.IN_KB);
//                for (Map<String, Object> binding : possibleBindings) {
//                    DialogAct newDialogActInstance = dialogActClass.newInstance();
//                    newDialogActInstance.bindVariables(binding);
//                    Map<String, SemanticsModel> generatedEntries = yodaEnvironment.nlg.generateAll(
//                            newDialogActInstance.getNlgCommand(), yodaEnvironment, Grammar.DEFAULT_GRAMMAR_PREFERENCES);
//                    ans.putAll(generatedEntries);
//                }
//            }

            for (Class<? extends DialogAct> dialogActClass : DialogRegistry.slotFillingDialogActs){
                DialogAct dialogActInstance = dialogActClass.newInstance();
                Set<Map<String, Object>> possibleBindings = ActionEnumeration.getPossibleNonIndividualBindings(dialogActInstance, null);
                for (Map<String, Object> binding : possibleBindings) {
                    System.out.println("dialogAct:"+dialogActClass+", binding:"+binding);
                    DialogAct newDialogActInstance = dialogActClass.newInstance();
                    newDialogActInstance.bindVariables(binding);
                    System.out.println(newDialogActInstance.getNlgCommand());
                    Map<String, SemanticsModel> generatedEntries = yodaEnvironment.nlg.generateAll(
                            newDialogActInstance.getNlgCommand(), yodaEnvironment, Grammar.DEFAULT_GRAMMAR_PREFERENCES);
                    ans.putAll(generatedEntries);
                    generatedEntries.keySet().forEach(System.out::println);
                }

            }

        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return ans;
    }

}

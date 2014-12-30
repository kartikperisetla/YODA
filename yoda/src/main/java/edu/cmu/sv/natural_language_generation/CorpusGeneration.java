package edu.cmu.sv.natural_language_generation;

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
            if (numPOIs > 10)
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
                        for (int i = 0; i < 3; i++) {

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
                        for (int i = 0; i < 3; i++) {

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
}

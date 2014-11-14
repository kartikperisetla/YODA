package edu.cmu.sv;

import edu.cmu.sv.natural_language_generation.Grammar;
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
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class TestGenerateCorpus {

    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        String empty = "{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\"}";

        String outputFileName = "/home/cohend/YODA_corpus.txt";
        PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");

        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String poiSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";
        String restaurantSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:Restaurant . \n }";
        List<String> restaurantURIList = new LinkedList<>(yodaEnvironment.db.runQuerySelectX(restaurantSelectionQuery));

        Random r = new Random();
        for (String restaurantURI : restaurantURIList){
            // randomly insert Expensiveness
            String expensivenessInsertString = yodaEnvironment.db.prefixes +
                    "INSERT DATA {<"+restaurantURI+"> base:expensiveness "+r.nextDouble()+"}";
            try {
                Update update = yodaEnvironment.db.connection.prepareUpdate(QueryLanguage.SPARQL, expensivenessInsertString, yodaEnvironment.db.baseURI);
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                e.printStackTrace();
            }
        }

        Grammar.GrammarPreferences corpusPreferences = new Grammar.GrammarPreferences(.01, .2, 5, 2, 5, 5, 2, new HashMap<>());

        for (String uri : yodaEnvironment.db.runQuerySelectX(poiSelectionQuery)) {

            // Generate YNQ for HasProperty where a PP is the property
            String YNQBaseString = "{\"dialogAct\":\""+YNQuestion.class.getSimpleName()+
                    "\", \"verb\": {\"class\":\""+
                    HasProperty.class.getSimpleName()+"\", \""+
                    Agent.class.getSimpleName()+"\":"+empty+", \""+
                    Patient.class.getSimpleName()+"\":"+empty+"}}";
            for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualityClasses){
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

                            Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                            for (String key : generated.keySet()) {
                                System.out.println(key);
                                System.out.println(generated.get(key));
                                System.out.println("---");
                            }
                        }
                    } else if (Adjective.class.isAssignableFrom(absoluteQualityDegreeClass)){
                        SemanticsModel ex0 = new SemanticsModel(YNQBaseString);
                        ex0.extendAndOverwriteAtPoint("verb." + Agent.class.getSimpleName(),
                                new SemanticsModel(OntologyRegistry.WebResourceWrap(uri)));

                        JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\""+absoluteQualityDegreeClass.getSimpleName()+"\"}");
                        SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                qualityDescriptor.getLeft().getSimpleName());
                        ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                new SemanticsModel(tmp));

                        Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                        for (String key : generated.keySet()) {
                            System.out.println(key);
                            System.out.println(generated.get(key));
                            System.out.println("---");
                        }
                    }
                }
            }

            // Generate YNQ for HasProperty where a PP is the property
            String WHQBaseString = "{\"dialogAct\":\""+WHQuestion.class.getSimpleName()+
                    "\", \"verb\": {\"class\":\""+
                    HasProperty.class.getSimpleName()+"\", \""+
                    Agent.class.getSimpleName()+"\":"+empty+", \""+
                    Patient.class.getSimpleName()+"\":"+empty+"}}";
            //TODO: adapt YNQ -> WHQ for the following for loop
            for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualityClasses){
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
                            SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                    qualityDescriptor.getLeft().getSimpleName());
                            ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                    new SemanticsModel(tmp));

                            Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                            for (String key : generated.keySet()) {
                                System.out.println(key);
                                System.out.println(generated.get(key));
                                System.out.println("---");
                            }
                        }
                    } else if (Adjective.class.isAssignableFrom(absoluteQualityDegreeClass)){
                        SemanticsModel ex0 = new SemanticsModel(WHQBaseString);
                        ex0.extendAndOverwriteAtPoint("verb." + Agent.class.getSimpleName(),
                                new SemanticsModel(OntologyRegistry.WebResourceWrap(uri)));

                        JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\""+absoluteQualityDegreeClass.getSimpleName()+"\"}");
                        SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                qualityDescriptor.getLeft().getSimpleName());
                        ex0.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(),
                                new SemanticsModel(tmp));

                        Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                        for (String key : generated.keySet()) {
                            System.out.println(key);
                            System.out.println(generated.get(key));
                            System.out.println("---");
                        }
                    }
                }
            }



//            SemanticsModel ex1 = new SemanticsModel("{\"dialogAct\": \"Fragment\", \"topic\": " +
//                    OntologyRegistry.WebResourceWrap(uri) + "}");
//            Map<String, SemanticsModel> tmp = yodaEnvironment.nlg.generateAll(ex1, yodaEnvironment, corpusPreferences);
//            for (String key : tmp.keySet()){
//                System.out.println(key);
////                System.out.println(tmp.get(key).getInternalRepresentation().toJSONString() + "\n");
////                writer.write("---\n");
//                writer.write(key+"\n");
//                writer.write(tmp.get(key).getInternalRepresentation().toJSONString() + "\n");
//            }
//
//            // usually, the command won't have a topic,
//            // but this is a quick way to generate a more interesting corpus for Bing
//            SemanticsModel ex2 = new SemanticsModel("{\"dialogAct\": \"Command\", \"topic\": " +
//                    OntologyRegistry.WebResourceWrap(uri) + "}");
//            Map<String, SemanticsModel> tmp2 = yodaEnvironment.nlg.generateAll(ex2, yodaEnvironment, corpusPreferences);
//            for (String key : tmp2.keySet()){
//                System.out.println(key);
////                System.out.println(tmp2.get(key).getInternalRepresentation().toJSONString() + "\n");
////                writer.write("---\n");
//                writer.write(key+"\n");
//                writer.write(tmp2.get(key).getInternalRepresentation().toJSONString() + "\n");
//            }
        }

        writer.close();

    }

}

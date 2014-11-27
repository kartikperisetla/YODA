package edu.cmu.sv;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.*;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.WHQuestion;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
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

        String outputFileName = "./src/resources/YODA_corpus.txt";
        PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");

        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String poiSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";


        Grammar.GrammarPreferences corpusPreferences = new Grammar.GrammarPreferences(.01, .2, 5, 2, 5, 5, 2, new HashMap<>());

        for (String uri : yodaEnvironment.db.runQuerySelectX(poiSelectionQuery)) {

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

                            Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                            for (String key : generated.keySet()) {
                                writer.write("---\n");
                                writer.write(key + "\n");
                                writer.write(generated.get(key).getInternalRepresentation().toJSONString() + "\n");
                                writeChunkingData(key, generated.get(key), writer);

                            }
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

                        Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                        for (String key : generated.keySet()) {
                            writer.write("---\n");
                            writer.write(key + "\n");
                            writer.write(generated.get(key).getInternalRepresentation().toJSONString() + "\n");
                            writeChunkingData(key, generated.get(key), writer);

                        }
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

                            Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                            for (String key : generated.keySet()) {
                                writer.write("---\n");
                                writer.write(key + "\n");
                                writer.write(generated.get(key).getInternalRepresentation().toJSONString() + "\n");
                                writeChunkingData(key, generated.get(key), writer);
                            }
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

                        Map<String, SemanticsModel> generated = yodaEnvironment.nlg.generateAll(ex0, yodaEnvironment, corpusPreferences);
                        for (String key : generated.keySet()) {
                            writer.write("---\n");
                            writer.write(key + "\n");
                            writer.write(generated.get(key).getInternalRepresentation().toJSONString() + "\n");
                            writeChunkingData(key, generated.get(key), writer);
                        }
                    }
                }

            }


            SemanticsModel ex1 = new SemanticsModel("{\"dialogAct\": \"Fragment\", \"topic\": " +
                    OntologyRegistry.WebResourceWrap(uri) + "}");
            Map<String, SemanticsModel> tmp = yodaEnvironment.nlg.generateAll(ex1, yodaEnvironment, corpusPreferences);
            for (String key : tmp.keySet()) {
                writer.write("---\n");
                writer.write(key + "\n");
                writer.write(tmp.get(key).getInternalRepresentation().toJSONString() + "\n");
                writeChunkingData(key, tmp.get(key), writer);

            }

            // usually, the command won't have a topic,
            // but this is a quick way to generate a more interesting corpus for Bing
            SemanticsModel ex2 = new SemanticsModel("{\"dialogAct\": \"Command\", \"topic\": " +
                    OntologyRegistry.WebResourceWrap(uri) + "}");
            Map<String, SemanticsModel> tmp2 = yodaEnvironment.nlg.generateAll(ex2, yodaEnvironment, corpusPreferences);
            for (String key : tmp2.keySet()) {
                writer.write("---\n");
                writer.write(key + "\n");
                writer.write(tmp2.get(key).getInternalRepresentation().toJSONString() + "\n");
                writeChunkingData(key, tmp2.get(key), writer);

//            }
            }
        }
        writer.close();

    }

    /*
    * Recursively create chunk labels based on the indeces given
    *
    * */
    public void writeChunkingData(String utterance, Object inputChunk, PrintWriter writer){
        JSONObject input;
        if (inputChunk instanceof JSONObject) {
            writer.write("--- Internal Chunking ---\n");
            input = (JSONObject) inputChunk;
        } else if (inputChunk instanceof SemanticsModel) {
            writer.write("--- Top Level Chunking ---\n");
            input = ((SemanticsModel) inputChunk).getInternalRepresentation();
        } else
            return;

        Map<String, Pair<Integer, Integer>> pathChunkIndexMap = new HashMap<>();
        Map<String, JSONObject> pathChildContentMap = new HashMap<>();
        Map<String, String> pathUtteranceMap = new HashMap<>();

        String[] words = utterance.split(" ");
        Set<Pair<String,JSONObject>> activeChildren = new HashSet<>();

        for (Object key : input.keySet()){
            if (input.get(key) instanceof JSONObject){
                activeChildren.add(new ImmutablePair<String, JSONObject>(
                        (String)key,
                        (JSONObject) input.get(key)));
            }
        }

        while (!activeChildren.isEmpty()){
            Iterator<Pair<String,JSONObject>> it = activeChildren.iterator();
            Pair<String, JSONObject> child = it.next();
            it.remove();
            if (child.getValue().containsKey("chunk-start")){
                Integer start;
                if (child.getValue().get("chunk-start") instanceof Long){
                    start = (int) (long) child.getValue().get("chunk-start");
                } else
                    start = (Integer)child.getValue().get("chunk-start");
                Integer end;
                if (child.getValue().get("chunk-end") instanceof Long){
                    end = (int) (long) child.getValue().get("chunk-end");
                } else
                    end = (Integer) child.getValue().get("chunk-end");

                pathChunkIndexMap.put(child.getKey(), new ImmutablePair<>(start, end));
                pathChildContentMap.put(child.getKey(), child.getValue());
            } else {
                for (Object key : child.getValue().keySet()){
                    if (child.getValue().get(key) instanceof JSONObject){
                        activeChildren.add(new ImmutablePair<String, JSONObject>(
                                child.getKey()+"."+(String)key,
                                (JSONObject) child.getValue().get(key)));
                    }
                }
            }
        }

        for (int i = 0; i < words.length; i++) {
            String line = words[i]+" ";
            boolean anyLabel = false;
            for (String key : pathChunkIndexMap.keySet()){
                if (pathChunkIndexMap.get(key).getLeft()<=i && i<=pathChunkIndexMap.get(key).getRight()){
                    if (!pathUtteranceMap.containsKey(key))
                        pathUtteranceMap.put(key, "");
                    pathUtteranceMap.put(key, pathUtteranceMap.get(key) + line);
                    line += key;
                    anyLabel = true;
                    break;
                }
            }
            if (!anyLabel)
                line += "<no-label>";
            writer.write(line+"\n");
        }

        for (String key: pathChildContentMap.keySet()) {
            if (WebResource.class.getSimpleName().equals(pathChildContentMap.get(key).get("class")))
                continue;
            writeChunkingData(pathUtteranceMap.get(key), pathChildContentMap.get(key), writer);
        }
    }

}

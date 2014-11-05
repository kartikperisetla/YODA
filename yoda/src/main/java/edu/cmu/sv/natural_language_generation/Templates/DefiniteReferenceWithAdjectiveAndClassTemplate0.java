package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.database.Database;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasQualityRole;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 11/3/14.
 *
 * Try all adjectives that this individual is in the range for.
 * Generate the + adj + cls if > .5 truth to the description.
 * Generates {cls:UnknownThingWithRoles, X:HasAQD: {cls: Y:aqd}}
 *
 */
public class DefiniteReferenceWithAdjectiveAndClassTemplate0 implements Template {
    static JSONObject applicabilityConstraint;
    static {
        try {
            applicabilityConstraint= (JSONObject) SemanticsModel.parser.
                    parse("{\"class\":\"" + WebResource.class.getSimpleName() + "\"}");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        Map<String, JSONObject> ans = new HashMap<>();

        if (SemanticsModel.anySenseConflicts(applicabilityConstraint, constraints))
            return new HashMap<>();

        String entityURI = (String) new SemanticsModel(constraints).
                newGetSlotPathFiller(HasURI.class.getSimpleName());
        String queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { <"+entityURI+"> rdf:type ?x .}";
        Set<String> classNames = yodaEnvironment.db.runQuerySelectX(queryString);

        // define the chunks that this template composes
        Map<String, JSONObject> detChunks = new HashMap<>();
        Map<String, JSONObject> adjChunks = new HashMap<>();
        Map<String, JSONObject> clsChunks = new HashMap<>();

        // compose det chunks
        detChunks.put("the", new SemanticsModel("{}").getInternalRepresentation());

        // collect class name chunks
        for (String clsName : classNames.stream().map(Database::getLocalName).
                collect(Collectors.toList())) {
            if (!OntologyRegistry.thingNameMap.containsKey(clsName))
                continue;
            Set<String> singularNounForms = GenerationUtils.getPOSForClass(
                    OntologyRegistry.thingNameMap.get(clsName), "singularNounForms");
            for (String singularNounForm : singularNounForms) {
                clsChunks.put(singularNounForm, SemanticsModel.parseJSON("{\"class\":\"" + clsName + "\"}"));
            }
        }

        String mostSpecificClass = yodaEnvironment.db.mostSpecificClass(entityURI);
        if (OntologyRegistry.thingNameMap.containsKey(mostSpecificClass) &&
                ThingWithRoles.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(mostSpecificClass))) {

            // collect adjectives
            System.out.println("most specific class:"+mostSpecificClass);
            for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualitiesForClass.get(
                    OntologyRegistry.thingNameMap.get(mostSpecificClass))) {
                List<Class<? extends Thing>> qualityArguments = OntologyRegistry.qualityArguments(qualityClass);
                // iterate through every possible binding for the quality arguments
                // adjectives
                if (qualityArguments.size() == 0) {
                    List<String> fullArgumentList = Arrays.asList(entityURI);

                    Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor =
                            OntologyRegistry.qualityDescriptors(qualityClass);
                    for (Class<? extends ThingWithRoles> adjectiveClass : descriptor.getRight()) {
                        double degreeOfMatch = yodaEnvironment.db.
                                evaluateQualityDegree(fullArgumentList,
                                        descriptor.getLeft(), adjectiveClass);
                        if (degreeOfMatch > 0.5) {
                            Set<String> adjStrings = GenerationUtils.getPOSForClass(adjectiveClass, "adjectives");
                            for (String adjString : adjStrings) {
                                JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}");
                                SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                        descriptor.getLeft().getSimpleName());
                                adjChunks.put(adjString, tmp);
                            }
                        }
                    }
                }
            }
            // include the empty adjective chunk
            adjChunks.put("", SemanticsModel.parseJSON("{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\"}"));

            if (remainingDepth>1) {
                // collect prepositions
                for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualitiesForClass.get(
                        OntologyRegistry.thingNameMap.get(mostSpecificClass))) {
                    List<Class<? extends Thing>> qualityArguments = OntologyRegistry.qualityArguments(qualityClass);
                    // iterate through every possible binding for the quality arguments
                    if (qualityArguments.size() == 1) {
                        Set<List<String>> bindings = yodaEnvironment.db.possibleBindings(qualityArguments);
                        for (List<String> binding : bindings) {
                            System.out.println("binding:"+binding);
                            List<String> fullArgumentList = new LinkedList<>(Arrays.asList(entityURI));
                            fullArgumentList.addAll(binding);

                            // prepositions
                            Map<String, JSONObject> childChunks = new HashMap<>();
                            JSONObject childContent = SemanticsModel.parseJSON(
                                    OntologyRegistry.WebResourceWrap(binding.get(0)));
                            yodaEnvironment.nlg.generateAll(childContent, yodaEnvironment, remainingDepth-1).
                                    entrySet().forEach(y -> childChunks.put(y.getKey(), y.getValue()));


                            Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor =
                                    OntologyRegistry.qualityDescriptors(qualityClass);
                            for (Class<? extends ThingWithRoles> prepositionClass : descriptor.getRight()) {
                                Map<String, JSONObject> ppChunks = new HashMap<>();
                                double degreeOfMatch = yodaEnvironment.db.
                                        evaluateQualityDegree(fullArgumentList,
                                                descriptor.getLeft(), prepositionClass);
                                if (degreeOfMatch > 0.5) {
                                    Set<String> ppStrings = GenerationUtils.getPOSForClass(prepositionClass, "relationalPrepositionalPhrases");
                                    for (String ppString : ppStrings) {
                                        JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + prepositionClass.getSimpleName() + "\"}");
                                        SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                                descriptor.getLeft().getSimpleName());
                                        ppChunks.put(ppString, tmp);
                                    }
                                }

                                // collect the referent strings that contain this PP
                                List<Map<String, JSONObject>> chunks = Arrays.asList(detChunks, adjChunks, clsChunks, ppChunks, childChunks);
                                Map<String, Pair<Integer, Integer>> childChunkingIndexMap = new HashMap<>();
                                childChunkingIndexMap.put(descriptor.getLeft().getSimpleName() + "." + InRelationTo.class.getSimpleName(), new ImmutablePair<>(4, 4));
                                for (Map.Entry<String, JSONObject> entry : GenerationUtils.simpleOrderedCombinations(chunks,
                                        DefiniteReferenceWithAdjectiveAndClassTemplate0::compositionFunctionWithPrepositionPhrase, childChunkingIndexMap).entrySet()) {
                                    ans.put(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                }
            }
        }

        List<Map<String, JSONObject>> chunks = Arrays.asList(detChunks, adjChunks, clsChunks);
        for (Map.Entry<String, JSONObject> entry : GenerationUtils.simpleOrderedCombinations(chunks,
                DefiniteReferenceWithAdjectiveAndClassTemplate0::compositionFunction, new HashMap<>()).entrySet()){
            ans.put(entry.getKey(), entry.getValue());
        }
        return ans;
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        JSONObject det = children.get(0);
        JSONObject adj = children.get(1);
        JSONObject cls = children.get(2);
        SemanticsModel ans = new SemanticsModel(cls.toJSONString());
        ans.extendAndOverwrite(new SemanticsModel(adj.toJSONString()));
        return ans.getInternalRepresentation();
    }

    private static JSONObject compositionFunctionWithPrepositionPhrase(List<JSONObject> children){
        JSONObject det = children.get(0);
        JSONObject adj = children.get(1);
        JSONObject cls = children.get(2);
        JSONObject pp = children.get(3);
        JSONObject child = children.get(4);

        // insert the child inside the preposition
//        System.out.println("PP keyset:"+pp.keySet());
//        System.out.println("PP:"+pp.toJSONString());
        List<Object> ppKeys = new LinkedList<>(pp.keySet());
        ppKeys.remove("class");
        String hasPPQualityRole = (String) ppKeys.get(0);
        ((JSONObject)pp.get(hasPPQualityRole)).put(InRelationTo.class.getSimpleName(), child);

        SemanticsModel ans = new SemanticsModel(cls.toJSONString());
        ans.extendAndOverwrite(new SemanticsModel(adj.toJSONString()));
        ans.extendAndOverwrite(new SemanticsModel(pp.toJSONString()));
        return ans.getInternalRepresentation();
    }


}
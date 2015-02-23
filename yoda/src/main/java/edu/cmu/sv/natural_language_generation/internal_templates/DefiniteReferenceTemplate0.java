package edu.cmu.sv.natural_language_generation.internal_templates;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.natural_language_generation.*;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.database.Database;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

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
public class DefiniteReferenceTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        String entityURI;
        // ensure that the constraints match this template
        try {
            Assert.verify(constraints.get("class").equals(WebResource.class.getSimpleName()));
            Assert.verify(constraints.keySet().size()==2);
            Assert.verify(constraints.containsKey(HasURI.class.getSimpleName()));
            entityURI = (String) new SemanticsModel(constraints).
                    newGetSlotPathFiller(HasURI.class.getSimpleName());
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        Map<String, JSONObject> ans = new HashMap<>();

        boolean expandPP = NaturalLanguageGenerator.random.nextDouble() <
                yodaEnvironment.nlg.grammarPreferences.pExpandPrepositionalPhrase;
        boolean expandAdj = NaturalLanguageGenerator.random.nextDouble() <
                yodaEnvironment.nlg.grammarPreferences.pIncludeAdjective;

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
            Set<String> singularNounForms;
            try {
                singularNounForms = Lexicon.getPOSForClass(OntologyRegistry.thingNameMap.get(clsName),
                        Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, yodaEnvironment.nlg.grammarPreferences, false);
            } catch (Lexicon.NoLexiconEntryException e) {
                singularNounForms = new HashSet<>();
            }
            for (String singularNounForm : singularNounForms) {
                clsChunks.put(singularNounForm, SemanticsModel.parseJSON("{\"class\":\"" + clsName + "\"}"));
            }
        }

        String mostSpecificClass = yodaEnvironment.db.mostSpecificClass(entityURI);
        if (OntologyRegistry.thingNameMap.containsKey(mostSpecificClass) &&
                ThingWithRoles.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(mostSpecificClass))) {

            // collect adjectives
//            System.out.println("most specific class:"+mostSpecificClass);
            for (Class<? extends TransientQuality> qualityClass : OntologyRegistry.qualitiesForClass.get(
                    OntologyRegistry.thingNameMap.get(mostSpecificClass))) {
                List<Class<? extends Thing>> qualityArguments = OntologyRegistry.qualityArguments(qualityClass);
                // iterate through every possible binding for the quality arguments
                // adjectives
                if (qualityArguments.size() == 0) {
                    if (!expandAdj)
                        continue;
                    List<String> fullArgumentList = Arrays.asList(entityURI);

                    Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor =
                            OntologyRegistry.qualityDescriptors(qualityClass);
                    for (Class<? extends ThingWithRoles> adjectiveClass : descriptor.getRight()) {
//                        System.out.println(adjectiveClass);
                        double degreeOfMatch = yodaEnvironment.db.
                                evaluateQualityDegree(fullArgumentList, adjectiveClass);
                        if (degreeOfMatch > 0.5) {
                            Set<String> adjStrings;
                            try {
                                adjStrings = Lexicon.getPOSForClass(adjectiveClass,
                                        Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, yodaEnvironment.nlg.grammarPreferences, false);
                            } catch (Lexicon.NoLexiconEntryException e) {
                                adjStrings = new HashSet<>();
                            }
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
                        if (!expandPP)
                            continue;

                        Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor =
                                OntologyRegistry.qualityDescriptors(qualityClass);
                        // init preposition counter
                        Map<Class<? extends ThingWithRoles>, Integer> prepositionUsageCounter = new HashMap<>();
                        for (Class<? extends ThingWithRoles> prepositionClass : descriptor.getRight())
                            prepositionUsageCounter.put(prepositionClass, 0);

                        Set<List<String>> bindings = yodaEnvironment.db.possibleBindings(qualityArguments);
                        for (List<String> binding : bindings) {
                            if (binding.get(0).equals(entityURI)) {
                                continue;
                            }
                            List<String> fullArgumentList = new LinkedList<>(Arrays.asList(entityURI));
                            fullArgumentList.addAll(binding);

                            // nested noun phrases
                            Map<String, JSONObject> childChunks = new HashMap<>();
                            JSONObject childContent = null;

                            // prepositions
                            for (Class<? extends ThingWithRoles> prepositionClass : descriptor.getRight()) {
                                if (prepositionUsageCounter.get(prepositionClass) == yodaEnvironment.nlg.grammarPreferences.maxPhrasesPerPreposition)
                                    continue;
                                Map<String, JSONObject> ppChunks = new HashMap<>();
                                double degreeOfMatch = yodaEnvironment.db.
                                        evaluateQualityDegree(fullArgumentList, prepositionClass);

                                if (degreeOfMatch > 0.5) {

                                    if (childContent==null){
                                        childContent = SemanticsModel.parseJSON(
                                                OntologyRegistry.webResourceWrap(binding.get(0)));
                                        for (Map.Entry<String, JSONObject> entry : yodaEnvironment.nlg.
                                                generateAll(childContent, yodaEnvironment, remainingDepth-1).entrySet()){
                                            childChunks.put(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    Set<String> ppStrings;
                                    try{
                                        ppStrings = Lexicon.getPOSForClass(prepositionClass,
                                                Lexicon.LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE,
                                                yodaEnvironment.nlg.grammarPreferences, false);
                                    } catch (Lexicon.NoLexiconEntryException e) {
                                        ppStrings = new HashSet<>();
                                    }
                                    for (String ppString : ppStrings) {
                                        JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + prepositionClass.getSimpleName() + "\"}");
                                        SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(),
                                                descriptor.getLeft().getSimpleName());
                                        ppChunks.put(ppString, tmp);
                                    }

                                    prepositionUsageCounter.put(prepositionClass, prepositionUsageCounter.get(prepositionClass)+1);
                                }

                                // collect the referent strings that contain this PP
                                List<Map<String, JSONObject>> chunks = Arrays.asList(detChunks, adjChunks, clsChunks, ppChunks, childChunks);
                                Map<String, Pair<Integer, Integer>> childChunkingIndexMap = new HashMap<>();
                                childChunkingIndexMap.put(descriptor.getLeft().getSimpleName() + "." + InRelationTo.class.getSimpleName(), new ImmutablePair<>(4, 4));
                                for (Map.Entry<String, JSONObject> entry : GenerationUtils.simpleOrderedCombinations(chunks,
                                        DefiniteReferenceTemplate0::compositionFunctionWithPrepositionPhrase, childChunkingIndexMap, yodaEnvironment).entrySet()) {
                                    ans.put(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                }
            }
        }

        // compose without PP
        List<Map<String, JSONObject>> chunks = Arrays.asList(detChunks, adjChunks, clsChunks);
        for (Map.Entry<String, JSONObject> entry : GenerationUtils.simpleOrderedCombinations(chunks,
                DefiniteReferenceTemplate0::compositionFunction, new HashMap<>(), yodaEnvironment).entrySet()){
            ans.put(entry.getKey(), entry.getValue());
        }

        Map<String, JSONObject> newAns = new HashMap<>();

        // only select references that are discriminative within the current dialog context focus
        for (String reference : ans.keySet()) {
            SemanticsModel filteredModel = new SemanticsModel(ans.get(reference).toJSONString());
            filteredModel.filterOutLeafSlot("chunk-start");
            filteredModel.filterOutLeafSlot("chunk-end");
//            System.out.println("reference:"+reference+", model:"+filteredModel.getInternalRepresentation());
            StringDistribution referenceAmbiguity = ReferenceResolution.resolveReference(
                    yodaEnvironment, filteredModel.getInternalRepresentation(), true);
//            System.out.println("information:"+referenceAmbiguity.information() + ", distribution:"+referenceAmbiguity);
            if (!referenceAmbiguity.containsKey(entityURI)){
//                System.out.println("the intended reference doesn't even appear as a top 10 referent in the discourse context");
            } else {
                if (referenceAmbiguity.information() < yodaEnvironment.nlg.grammarPreferences.referenceAmbiguityThreshold &&
                        referenceAmbiguity.getTopHypothesis().equals(entityURI)) {
                    newAns.put(reference, ans.get(reference));
//                    System.out.println("this reference is acceptable");
                }
            }
        }
        return newAns;
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
        List<Object> ppKeys = new LinkedList<Object>(pp.keySet());
        ppKeys.remove("class");
        String hasPPQualityRole = (String) ppKeys.get(0);
        ((JSONObject)pp.get(hasPPQualityRole)).put(InRelationTo.class.getSimpleName(), child);
//        System.out.println("compositionFunctionWithPP: pp after adding child:");
//        System.out.println(pp.toJSONString());

        SemanticsModel ans = new SemanticsModel(cls.toJSONString());
        ans.extendAndOverwrite(new SemanticsModel(adj.toJSONString()));
        ans.extendAndOverwrite(new SemanticsModel(pp.toJSONString()));
        return ans.getInternalRepresentation();
    }


}

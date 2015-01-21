package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestProperty;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by David Cohen on 11/13/14.
 */
public class HasPropertyWHQTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {

        Map<String, JSONObject> ans = new HashMap<>();
        SemanticsModel constraintsModel = new SemanticsModel(constraints);
        String verbClassString;
        String requestedSlotPath;
        String givenSlotPath;
        JSONObject requestedContent;
        JSONObject givenDescription;
        Class<? extends TransientQuality> requestedQuality = null;
        Class<? extends Role> requestedRoleClass;
        Class<? extends Role> givenRoleClass;

        try {
            Assert.verify(constraints.get("dialogAct").equals(RequestProperty.class.getSimpleName()));
            Assert.verify(constraints.containsKey("verb"));
            JSONObject verbObject = (JSONObject) constraints.get("verb");
            verbClassString = (String) verbObject.get("class");
            Assert.verify(verbClassString.equals(HasProperty.class.getSimpleName()));
            Assert.verify(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName()).size() == 1);
            requestedSlotPath = new LinkedList<>(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName())).get(0);
            requestedContent = (JSONObject) constraintsModel.newGetSlotPathFiller(requestedSlotPath);
            if (requestedContent.containsKey(HasValue.class.getSimpleName())) {
                requestedQuality = (Class<? extends TransientQuality>) OntologyRegistry.thingNameMap.get(
                        (String) ((JSONObject) requestedContent.get(HasValue.class.getSimpleName())).get("class"));
            }
            String[] fillerSequence = requestedSlotPath.split("\\.");
            Assert.verify(OntologyRegistry.roleNameMap.containsKey(fillerSequence[fillerSequence.length - 1]));
            requestedRoleClass = OntologyRegistry.roleNameMap.get(fillerSequence[fillerSequence.length - 1]);

            Assert.verify(verbObject.size() == 3); //class, requested, given
            List<Object> verbRoles = new LinkedList<>(verbObject.keySet());
            verbRoles.remove("class");
            verbRoles.remove(fillerSequence[fillerSequence.length - 1]);
            givenSlotPath = "verb." + verbRoles.get(0);
            givenDescription = (JSONObject) new SemanticsModel(constraints).newGetSlotPathFiller(givenSlotPath);
            Assert.verify(OntologyRegistry.roleNameMap.containsKey(verbRoles.get(0)));
            givenRoleClass = OntologyRegistry.roleNameMap.get(verbRoles.get(0));
            // remove the given information from the verb chunk content
            System.out.println(constraints);
            verbObject.remove(verbRoles.get(0));
        } catch (Assert.AssertException e) {
            return new HashMap<>();
        }


        // Pair<given, requested>
        Set<String> adjectiveStrings = new HashSet<>();
        Set<String> qualityStrings = new HashSet<>();
        Set<String> presentVerbStrings;


        if (requestedQuality != null) {
            Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> qualityDescriptors = OntologyRegistry.qualityDescriptors(requestedQuality);
            for (Class<? extends ThingWithRoles> adjectiveClass : qualityDescriptors.getRight()) {
                try {
                    adjectiveStrings.addAll(Lexicon.getPOSForClassHierarchy(adjectiveClass,
                            Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, yodaEnvironment));
                } catch (Lexicon.NoLexiconEntryException e) {
                }
                // just because one of the classes in the descriptor has no lexical info doesn't mean the template is broken
            }
        }

        try {
            qualityStrings.addAll(Lexicon.getPOSForClassHierarchy(requestedQuality,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, yodaEnvironment));
        } catch (Lexicon.NoLexiconEntryException e) {
        }

        Map<String, JSONObject> howPlusAdjectiveChunks = adjectiveStrings.stream().
                collect(Collectors.toMap(x -> "how " + x, x -> SemanticsModel.parseJSON("{}")));
        Map<String, JSONObject> qualityChunks = qualityStrings.stream().
                collect(Collectors.toMap(x -> "how " + x, x -> SemanticsModel.parseJSON("{}")));
        Map<String, JSONObject> whatChunk = new HashMap<>();
        whatChunk.put("what", SemanticsModel.parseJSON("{}"));
        Map<String, JSONObject> toBeChunk = new HashMap<>();
        whatChunk.put("is", SemanticsModel.parseJSON(constraints.toJSONString()));


        Map<String, JSONObject> givenChunks = yodaEnvironment.nlg.generateAll(givenDescription, yodaEnvironment, remainingDepth - 1);
        // recursively wrap the given chunks in the given path
        String[] slots = givenSlotPath.split("\\.");
        for (int i = 0; i < slots.length; i++) {
            for (String key : givenChunks.keySet()) {
                SemanticsModel.wrap(givenChunks.get(key), UnknownThingWithRoles.class.getSimpleName(), slots[slots.length - i - 1]);
            }
        }

        { // template: how X is Y?
            Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
            List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
            orderedChunks.add(howPlusAdjectiveChunks);
            orderedChunks.add(toBeChunk);
            orderedChunks.add(givenChunks);
            childNodeChunks.put(givenSlotPath, new ImmutablePair<>(2, 2));
            childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(0, 0));
            ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                    HasPropertyWHQTemplate0::compositionFunction, childNodeChunks, yodaEnvironment));
        }

        { // template: what X is Y?
            Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
            List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
            orderedChunks.add(whatChunk);
            orderedChunks.add(qualityChunks);
            orderedChunks.add(toBeChunk);
            orderedChunks.add(givenChunks);
            childNodeChunks.put(givenSlotPath, new ImmutablePair<>(3, 3));
            childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(0, 1));
            ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                    HasPropertyWHQTemplate0::compositionFunction, childNodeChunks, yodaEnvironment));
        }


        return ans;
    }

    private static JSONObject compositionFunction(List<JSONObject> children) {
        SemanticsModel ans = new SemanticsModel("{}");
        for (JSONObject child : children)
            ans.extendAndOverwrite(new SemanticsModel(child.toJSONString()));
        return ans.getInternalRepresentation();
    }
}

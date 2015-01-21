package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRoleGivenRole;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 11/1/14.
 *
 * NLG template for requesting roles given one role's description.
 * encode the given role as Subject and requested role as OBJ1
 * OR encode given role as OBJ1 and requested as OBJ2
 * OR encode given role as OBJ2 and requested as OBJ1
 * OR encode given as OBJ1 and requested as Subject
 *
 * exs:
 * give directions to where?
 * is what?
 *
 */
public class RequestRoleGivenRoleTemplate implements Template {

    // TODO: this is not completely implemented
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        Map<String, JSONObject> ans = new HashMap<>();
        SemanticsModel constraintsModel = new SemanticsModel(constraints);
        String verbClassString;
        String requestedSlotPath;
        String givenSlotPath;
        JSONObject givenDescription;
        Class<? extends Role> requestedRoleClass;
        Class<? extends Role> givenRoleClass;

        try {
            Assert.verify(constraints.get("dialogAct").equals(RequestRoleGivenRole.class.getSimpleName()));
            Assert.verify(constraints.containsKey("verb"));
            JSONObject verbObject = (JSONObject)constraints.get("verb");
            verbClassString = (String)verbObject.get("class");
            Assert.verify(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName()).size()==1);
            requestedSlotPath = new LinkedList<>(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName())).get(0);

            String[] fillerSequence = requestedSlotPath.split("\\.");
            Assert.verify(OntologyRegistry.roleNameMap.containsKey(fillerSequence[fillerSequence.length - 1]));
            requestedRoleClass = OntologyRegistry.roleNameMap.get(fillerSequence[fillerSequence.length - 1]);

            Assert.verify(verbObject.size()==3); //class, requested, given
            List<Object> verbRoles = new LinkedList<>(verbObject.keySet());
            verbRoles.remove("class");
            verbRoles.remove(fillerSequence[fillerSequence.length - 1]);
            givenSlotPath = "verb."+verbRoles.get(0);
            givenDescription = (JSONObject) new SemanticsModel(constraints).newGetSlotPathFiller(givenSlotPath);
            Assert.verify(OntologyRegistry.roleNameMap.containsKey(verbRoles.get(0)));
            givenRoleClass = OntologyRegistry.roleNameMap.get(verbRoles.get(0));
            // remove the given information from the verb chunk content
            System.out.println(constraints);
            verbObject.remove(verbRoles.get(0));
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }


        // Pair<given, requested>
        Set<Pair<String, String>> rolePartsOfSpeech = new HashSet<>();
        rolePartsOfSpeech.add(new ImmutablePair<>("subject", "obj1"));
        rolePartsOfSpeech.add(new ImmutablePair<>("obj1", "obj2"));
        rolePartsOfSpeech.add(new ImmutablePair<>("obj2", "obj1"));
        rolePartsOfSpeech.add(new ImmutablePair<>("obj1", "subject"));

        for (Pair<String, String> rolePartOfSpeechPair : rolePartsOfSpeech){
            Set<String> givenPrefixStrings;
            Set<String> requestedPrefixStrings;
            Set<String> whStrings = new HashSet<>();
            Set<String> presentVerbStrings;
            Set<String> progressiveVerbStrings;

            try {
                if (rolePartOfSpeechPair.getLeft().equals("subject"))
                    givenPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, yodaEnvironment);
                else if (rolePartOfSpeechPair.getLeft().equals("obj1"))
                    givenPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, yodaEnvironment);
                else //(rolePartOfSpeechPair.getLeft().equals("obj2"))
                    givenPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, yodaEnvironment);

                if (rolePartOfSpeechPair.getRight().equals("subject"))
                    requestedPrefixStrings = Lexicon.getPOSForClass(requestedRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, yodaEnvironment);
                else if (rolePartOfSpeechPair.getRight().equals("obj1"))
                    requestedPrefixStrings = Lexicon.getPOSForClass(requestedRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, yodaEnvironment);
                else //(rolePartOfSpeechPair.getRight().equals("obj2"))
                    requestedPrefixStrings = Lexicon.getPOSForClass(requestedRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, yodaEnvironment);

                // assume that classesInRange only contains the most general classes possible
                Set<Class <? extends Thing>> classesInRange = requestedRoleClass.newInstance().getRange();
                for (Class <? extends Thing> cls : classesInRange){
                    try {
                        whStrings.addAll(Lexicon.getPOSForClassHierarchy(cls, Lexicon.LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, yodaEnvironment));
                    } catch(Lexicon.NoLexiconEntryException e){}
                    // just because one of the classes in range has no lexical info doesn't mean the template is broken
                }

                presentVerbStrings = Lexicon.getPOSForClass(OntologyRegistry.thingNameMap.get(verbClassString),
                        Lexicon.LexicalEntry.PART_OF_SPEECH.S1_VERB, yodaEnvironment);
                Map<String, JSONObject> singularVerbChunks = presentVerbStrings.stream().
                        collect(Collectors.toMap(x->x, (x -> SemanticsModel.parseJSON(constraints.toJSONString()))));

                progressiveVerbStrings = Lexicon.getPOSForClass(OntologyRegistry.thingNameMap.get(verbClassString),
                        Lexicon.LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, yodaEnvironment);
                Map<String, JSONObject> progressiveVerbChunks = progressiveVerbStrings.stream().
                        collect(Collectors.toMap(x->x, (x -> SemanticsModel.parseJSON(constraints.toJSONString()))));


                Map<String, JSONObject> requestedPrefixChunks = requestedPrefixStrings.stream().
                        collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));
                Map<String, JSONObject> whChunks = whStrings.stream().
                        collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));
                Map<String, JSONObject> givenPrefixChunks = givenPrefixStrings.stream().
                        collect(Collectors.toMap(x -> x, x -> SemanticsModel.parseJSON("{}")));

                Map<String, JSONObject> givenChunks = yodaEnvironment.nlg.generateAll(givenDescription, yodaEnvironment, remainingDepth - 1);
                // recursively wrap the given chunks in the given path
                String[] slots = givenSlotPath.split("\\.");
                for (int i = 0; i < slots.length ; i++) {
                    for (String key : givenChunks.keySet()){
                        SemanticsModel.wrap(givenChunks.get(key), UnknownThingWithRoles.class.getSimpleName(), slots[slots.length - i - 1]);
                    }
                }



                if (rolePartOfSpeechPair.getLeft().equals("subject") && rolePartOfSpeechPair.getRight().equals("obj1")) {
                    {
                        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
                        List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
                        orderedChunks.add(givenPrefixChunks);
                        orderedChunks.add(givenChunks);
                        orderedChunks.add(singularVerbChunks);
                        orderedChunks.add(requestedPrefixChunks);
                        orderedChunks.add(whChunks);
                        childNodeChunks.put(givenSlotPath, new ImmutablePair<>(1, 1));
                        childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(4, 4));
                        ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                                RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment));
                    }

                    {
                        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
                        List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
                        orderedChunks.add(requestedPrefixChunks);
                        orderedChunks.add(whChunks);
                        orderedChunks.add(givenPrefixChunks);
                        orderedChunks.add(givenChunks);
                        orderedChunks.add(progressiveVerbChunks);
                        childNodeChunks.put(givenSlotPath, new ImmutablePair<>(3, 3));
                        childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(1, 1));
                        ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                                RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment));
                    }

                } else if (rolePartOfSpeechPair.getLeft().equals("obj1") && rolePartOfSpeechPair.getRight().equals("obj2")) {

                    Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
                    List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
                    orderedChunks.add(singularVerbChunks);
                    orderedChunks.add(givenPrefixChunks);
                    orderedChunks.add(givenChunks);
                    orderedChunks.add(requestedPrefixChunks);
                    orderedChunks.add(whChunks);
                    childNodeChunks.put(givenSlotPath, new ImmutablePair<>(2, 2));
                    childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(4, 4));
                    ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                            RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment));

                } else if (rolePartOfSpeechPair.getLeft().equals("obj1") && rolePartOfSpeechPair.getRight().equals("subj")) {
                    Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
                    List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
                    orderedChunks.add(requestedPrefixChunks);
                    orderedChunks.add(whChunks);
                    orderedChunks.add(singularVerbChunks);
                    orderedChunks.add(givenPrefixChunks);
                    orderedChunks.add(givenChunks);
                    childNodeChunks.put(givenSlotPath, new ImmutablePair<>(4, 4));
                    childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(1, 1));
                    ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                            RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment));
                } else if (rolePartOfSpeechPair.getLeft().equals("obj2") && rolePartOfSpeechPair.getRight().equals("obj1")) {

                    Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
                    List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
                    orderedChunks.add(singularVerbChunks);
                    orderedChunks.add(requestedPrefixChunks);
                    orderedChunks.add(whChunks);
                    orderedChunks.add(givenPrefixChunks);
                    orderedChunks.add(givenChunks);
                    childNodeChunks.put(givenSlotPath, new ImmutablePair<>(4, 4));
                    childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(2, 2));
                    ans.putAll(GenerationUtils.simpleOrderedCombinations(orderedChunks,
                            RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment));


                } else {
                    continue;
                }



            } catch (Lexicon.NoLexiconEntryException | InstantiationException | IllegalAccessException e) {}
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

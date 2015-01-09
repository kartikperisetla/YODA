package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
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

        try{
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
            givenDescription = (JSONObject) constraints.get(verbRoles.get(0));
            Assert.verify(OntologyRegistry.roleNameMap.containsKey(verbRoles.get(0)));
            givenRoleClass = OntologyRegistry.roleNameMap.get(verbRoles.get(0));


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
            Set<String> verbStrings;

            try {
                if (rolePartOfSpeechPair.getLeft().equals("subject"))
                    givenPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, yodaEnvironment);
                else if (rolePartOfSpeechPair.getLeft().equals("obj1"))
                    givenPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, yodaEnvironment);
                else //(rolePartOfSpeechPair.getLeft().equals("obj2"))
                    givenPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, yodaEnvironment);

                if (rolePartOfSpeechPair.getRight().equals("subject"))
                    requestedPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, yodaEnvironment);
                else if (rolePartOfSpeechPair.getRight().equals("obj1"))
                    requestedPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, yodaEnvironment);
                else //(rolePartOfSpeechPair.getRight().equals("obj2"))
                    requestedPrefixStrings = Lexicon.getPOSForClass(givenRoleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, yodaEnvironment);

                // assume that classesInRange only contains the most general classes possible
                Set<Class <? extends Thing>> classesInRange = requestedRoleClass.newInstance().getRange();
                for (Class <? extends Thing> cls : classesInRange){
                    try {
                        whStrings.addAll(Lexicon.getPOSForClassHierarchy(cls, Lexicon.LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, yodaEnvironment));
                    } catch(Lexicon.NoLexiconEntryException e){}
                    // just because one of the classes in range has no lexical info doesn't mean the template is broken
                }

                verbStrings = Lexicon.getPOSForClass(OntologyRegistry.thingNameMap.get(verbClassString),
                        Lexicon.LexicalEntry.PART_OF_SPEECH.PRESENT_SINGULAR_VERB, yodaEnvironment);

                Map<String, JSONObject> requestedPrefixChunks = requestedPrefixStrings.stream().
                        collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));
                Map<String, JSONObject> whChunks = whStrings.stream().
                        collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));

                Map<String, JSONObject> givenPrefixChunks = givenPrefixStrings.stream().
                        collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));
                Map<String, JSONObject> givenChunks = yodaEnvironment.nlg.generateAll(givenDescription, yodaEnvironment, remainingDepth-1);

                Map<String, JSONObject> verbChunks = verbStrings.stream().
                        collect(Collectors.toMap(x->x, (x -> SemanticsModel.parseJSON(constraints.toJSONString()))));

                Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();

                List<Map<String, JSONObject>> orderedChunks = new LinkedList<>();
                if (rolePartOfSpeechPair.getLeft().equals("subject")){
                    orderedChunks.add(givenPrefixChunks);
                    orderedChunks.add(givenChunks);
                    childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(orderedChunks.size()-1, orderedChunks.size()-1));
                    orderedChunks.add(requestedPrefixChunks);
                    orderedChunks.add(whChunks);
                }

                ans.putAll(GenerationUtils.simpleOrderedCombinations(Arrays.asList(verbChunks, rolePrefixChunks, whChunks),
                        RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment));


            } catch (Lexicon.NoLexiconEntryException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        Set<String> rolePrefixStrings = new HashSet<>();
        Set<String> whStrings = new HashSet<>();
        Set<String> verbStrings = new HashSet<>();
        try {
            // assume that classesInRange only contains the most general classes possible
            Set<Class <? extends Thing>> classesInRange = requestedRoleClass.newInstance().getRange();
            for (Class <? extends Thing> cls : classesInRange){
                try {
                    whStrings.addAll(Lexicon.getPOSForClassHierarchy(cls, Lexicon.LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, yodaEnvironment));
                } catch(Lexicon.NoLexiconEntryException e){}
                // just because one of the classes in range has no lexical info doesn't mean the template is broken
            }

            rolePrefixStrings = Lexicon.getPOSForClass(requestedRoleClass,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, yodaEnvironment);

            verbStrings = Lexicon.getPOSForClass(OntologyRegistry.thingNameMap.get(verbClassString),
                    Lexicon.LexicalEntry.PART_OF_SPEECH.PRESENT_SINGULAR_VERB, yodaEnvironment);

        } catch (InstantiationException | IllegalAccessException | Lexicon.NoLexiconEntryException e) {
//            e.printStackTrace();
        }

        Map<String, JSONObject> whChunks = whStrings.stream().
                collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));
        Map<String, JSONObject> rolePrefixChunks = rolePrefixStrings.stream().
                collect(Collectors.toMap(x->x, x->SemanticsModel.parseJSON("{}")));
        Map<String, JSONObject> verbChunks = verbStrings.stream().
                collect(Collectors.toMap(x->x, (x -> SemanticsModel.parseJSON(constraints.toJSONString()))));

        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
        childNodeChunks.put(requestedSlotPath, new ImmutablePair<>(2,2));
        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(verbChunks, rolePrefixChunks, whChunks),
                RequestRoleGivenRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children) {
        JSONObject verbPhrase = children.get(0);
        return SemanticsModel.parseJSON(verbPhrase.toJSONString());
    }
}

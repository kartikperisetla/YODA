package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.Ontology;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
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
 * NLG template for requesting roles, encode the requested role as the verb's first object (S V O1 O2 O3...)
 *
 * exs:
 * give directions to where?
 * is what?
 *
 */
public class RequestRoleTemplate implements Template {

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        SemanticsModel constraintsModel = new SemanticsModel(constraints);
        String verbClassString;
        String requestedSlotPath;
        Class<? extends Role> roleClass;

        try{
            Assert.verify(constraints.get("dialogAct").equals(RequestRole.class.getSimpleName()));
            Assert.verify(constraints.containsKey("verb"));
            JSONObject verbObject = (JSONObject)constraints.get("verb");
            verbClassString = (String)verbObject.get("class");
            Assert.verify(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName()).size()==1);
            requestedSlotPath = new LinkedList<>(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName())).get(0);
            String[] fillerPath = requestedSlotPath.split("\\.");
            Assert.verify(Ontology.roleNameMap.containsKey(fillerPath[fillerPath.length - 1]));
            roleClass = Ontology.roleNameMap.get(fillerPath[fillerPath.length - 1]);
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        Set<String> rolePrefixStrings = new HashSet<>();
        Set<String> whStrings = new HashSet<>();
        Set<String> verbStrings = new HashSet<>();
        try {
            // assume that classesInRange only contains the most general classes possible
            Set<Class <? extends Thing>> classesInRange = roleClass.newInstance().getRange();
            for (Class <? extends Thing> cls : classesInRange){
                try {
                    whStrings.addAll(yodaEnvironment.lex.getPOSForClassHierarchy(cls, Lexicon.LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, yodaEnvironment.nlg.grammarPreferences, false));
                } catch(Lexicon.NoLexiconEntryException e){}
                // just because one of the classes in range has no lexical info doesn't mean the template is broken
            }

            rolePrefixStrings = yodaEnvironment.lex.getPOSForClass(roleClass,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, yodaEnvironment.nlg.grammarPreferences, false);

            verbStrings = yodaEnvironment.lex.getPOSForClass(Ontology.thingNameMap.get(verbClassString),
                    Lexicon.LexicalEntry.PART_OF_SPEECH.S1_VERB, yodaEnvironment.nlg.grammarPreferences, false);

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
                RequestRoleTemplate::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children) {
        JSONObject verbPhrase = children.get(0);
        return SemanticsModel.parseJSON(verbPhrase.toJSONString());
    }
}

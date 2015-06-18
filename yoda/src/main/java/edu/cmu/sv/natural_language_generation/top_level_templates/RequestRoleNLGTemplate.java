package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology2.Noun2;
import edu.cmu.sv.domain.ontology2.Role2;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.Set;

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
public class RequestRoleNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        String verbClassString;
        String requestedSlotPath;
        Role2 roleClass;

        JSONObject verbObject = (JSONObject)constraints.newGetSlotPathFiller("verb");
        verbClassString = (String)verbObject.get("class");
        requestedSlotPath = new LinkedList<>(constraints.findAllPathsToClass(YodaSkeletonOntologyRegistry.requested.name)).get(0);
        String[] fillerPath = requestedSlotPath.split("\\.");
        roleClass = Ontology.roleNameMap.get(fillerPath[fillerPath.length - 1]);

        String rolePrefixString = null;
        String whString = null;
        String verbString = null;
        try {
            // assume that classesInRange only contains the most general classes possible
            Set<Object> classesInRange = roleClass.getRange();
            for (Object cls : classesInRange){
                try {
                    if (!(cls instanceof Noun2))
                        continue;
                    whString = yodaEnvironment.lex.getPOSForClassHierarchy((Noun2)cls, Lexicon.LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, false).
                            stream().findAny().get();
                    break;
                } catch(Lexicon.NoLexiconEntryException e){}
                // just because one of the classes in range has no lexical info doesn't mean the template is broken
            }

            rolePrefixString = yodaEnvironment.lex.getPOSForClass(roleClass,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, false).stream().findAny().get();

            verbString = yodaEnvironment.lex.getPOSForClass(Ontology.thingNameMap.get(verbClassString),
                    Lexicon.LexicalEntry.PART_OF_SPEECH.S1_VERB, false).stream().findAny().get();

        } catch (Lexicon.NoLexiconEntryException e) {}
        if (Ontology.verbNameMap.get(verbClassString).equals(YodaSkeletonOntologyRegistry.hasProperty)){
            verbString = "is";
        }

        return new ImmutablePair<>(verbString+" "+rolePrefixString+" "+whString, constraints.deepCopy());
    }

}

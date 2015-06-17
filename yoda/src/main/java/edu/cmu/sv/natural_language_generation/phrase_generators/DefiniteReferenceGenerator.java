package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology2.Noun2;
import edu.cmu.sv.domain.ontology2.Quality2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasName;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasURI;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/*
*  Generate a definite reference: the + (adj) + class noun
* */
public class DefiniteReferenceGenerator implements PhraseGenerationRoutine {
    @Override
    public ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        String entityURI = (String) new SemanticsModel(constraints).newGetSlotPathFiller(HasURI.class.getSimpleName());
//        boolean expandPP = NLG2.random.nextDouble() < .1;
        boolean expandAdj = NaturalLanguageGenerator.random.nextDouble() < .2;
        boolean preferNameReference = NaturalLanguageGenerator.random.nextDouble() < .9;
        String entityNameString = null;
        String classNounString = null;
        String adjString = null;
//        String ppString = null;
        JSONObject entityNameJSON = null;
        JSONObject classNounJSON = null;
        JSONObject adjJSON = null;
//        JSONObject ppJSON = null;


        if (preferNameReference){
            String queryString = yodaEnvironment.db.prefixes +
                    "SELECT ?x WHERE { <"+entityURI+"> rdfs:label ?x .}";

            entityNameString = yodaEnvironment.db.runQuerySelectX(queryString).stream().findAny().get();

            String uri = yodaEnvironment.db.insertValue(entityNameString);
            JSONObject content = SemanticsModel.parseJSON("{\"HasURI\":\""+uri+"\",\"class\":\"WebResource\"}");
            SemanticsModel.wrap(content, yodaEnvironment.db.mostSpecificClass(entityURI),
                    HasName.class.getSimpleName());
            return new ImmutablePair<>(entityNameString, content);
        }

        String mostSpecificClass = yodaEnvironment.db.mostSpecificClass(entityURI);
        try {
            classNounString = yodaEnvironment.lex.getPOSForClass(Ontology.thingNameMap.get(mostSpecificClass),
                    Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, false).
                    stream().findAny().get();
            classNounJSON = SemanticsModel.parseJSON("{\"class\":\"" + mostSpecificClass + "\"}");
        } catch (Lexicon.NoLexiconEntryException e) {}

        if (expandAdj){
            for (Quality2 qualityClass : Ontology.qualitiesForClass.get(
                    Ontology.thingNameMap.get(mostSpecificClass))) {
                if (adjString!=null)
                    break;
                //todo: ensure that the entityURI meets the firstQualityArgument constraint
                Object firstQualityArgument = qualityClass.firstArgumentClassConstraint;
                Object secondQualityArgument = qualityClass.secondArgumentClassConstraint;
                // iterate through every possible binding for the quality arguments adjectives
                if (secondQualityArgument == null) {
                    if (!expandAdj)
                        continue;
                    List<String> fullArgumentList = Arrays.asList(entityURI);

                    Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor =
                            Ontology.qualityDescriptors(qualityClass);
                    for (Class<? extends ThingWithRoles> adjectiveClass : descriptor.getRight()) {
                        if (adjString!=null)
                            break;
                        if (adjectiveClass==null)
                            continue;
                        Double degreeOfMatch = yodaEnvironment.db.
                                evaluateQualityDegree(fullArgumentList, adjectiveClass);
                        if (degreeOfMatch!=null && degreeOfMatch > 0.5) {
                            try {
                                adjString = yodaEnvironment.lex.getPOSForClass(adjectiveClass,
                                        Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, false).
                                        stream().findAny().get();
                                adjJSON = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}");
                                SemanticsModel.wrap(adjJSON, UnknownThingWithRoles.class.getSimpleName(),
                                        descriptor.getLeft().getSimpleName());
                            } catch (Lexicon.NoLexiconEntryException e) {}
                        }
                    }
                }
            }
        }

        if (classNounString==null)
            return null;
        String ansString = "the " + (adjString==null ? "" : adjString +" ") + classNounString;
        SemanticsModel ans = new SemanticsModel(classNounJSON.toJSONString());
        ans.extendAndOverwrite(new SemanticsModel(adjJSON.toJSONString()));
        JSONObject ansJSON = ans.getInternalRepresentation();
        return new ImmutablePair<>(ansString, ansJSON);
    }
}

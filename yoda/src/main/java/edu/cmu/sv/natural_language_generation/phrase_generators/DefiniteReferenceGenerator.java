package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology.Quality;
import edu.cmu.sv.domain.ontology.QualityDegree;
import edu.cmu.sv.domain.ontology.Role;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.NoSuchElementException;
import java.util.Set;

/*
*  Generate a definite reference: the + (adj) + class noun
* */
public class DefiniteReferenceGenerator implements PhraseGenerationRoutine {
    @Override
    public ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        String entityURI = (String) new SemanticsModel(constraints).newGetSlotPathFiller(YodaSkeletonOntologyRegistry.hasUri.name);
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
            try {
                entityNameString = yodaEnvironment.db.runQuerySelectX(queryString).stream().findAny().get();

                String uri = yodaEnvironment.db.insertValue(entityNameString);
                JSONObject content = SemanticsModel.parseJSON("{\"HasURI\":\"" + uri + "\",\"class\":\"WebResource\"}");
                SemanticsModel.wrap(content, yodaEnvironment.db.mostSpecificClass(entityURI), YodaSkeletonOntologyRegistry.hasName.name);
                return new ImmutablePair<>(entityNameString, content);
            } catch (NoSuchElementException e ){}
        }

        String mostSpecificClass = yodaEnvironment.db.mostSpecificClass(entityURI);
        try {
            classNounString = yodaEnvironment.lex.getPOSForClass(Ontology.thingNameMap.get(mostSpecificClass),
                    Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, false).
                    stream().findAny().get();
            classNounJSON = SemanticsModel.parseJSON("{\"class\":\"" + mostSpecificClass + "\"}");
        } catch (Lexicon.NoLexiconEntryException e) {}

        if (expandAdj){
            for (Quality qualityClass : Ontology.qualitiesForClass.get(
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

                    Pair<Role, Set<QualityDegree>> descriptor = Ontology.qualityDescriptors(qualityClass);
                    for (QualityDegree adjectiveClass : descriptor.getRight()) {
                        if (adjString!=null)
                            break;
                        if (adjectiveClass==null)
                            continue;
                        Double degreeOfMatch = yodaEnvironment.db.
                                evaluateQualityDegree(entityURI, null, adjectiveClass);
                        if (degreeOfMatch!=null && degreeOfMatch > 0.5) {
                            try {
                                adjString = yodaEnvironment.lex.getPOSForClass(adjectiveClass,
                                        Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, false).
                                        stream().findAny().get();
                                adjJSON = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.name + "\"}");
                                SemanticsModel.wrap(adjJSON, YodaSkeletonOntologyRegistry.unknownThingWithRoles.name,
                                        descriptor.getLeft().name);
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
        if (adjJSON!=null)
            ans.extendAndOverwrite(new SemanticsModel(adjJSON.toJSONString()));
        JSONObject ansJSON = ans.getInternalRepresentation();
        return new ImmutablePair<>(ansString, ansJSON);
    }
}

package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasName;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.InRelationTo;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Utils;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 1/27/15.
 */
public class NounPhraseInterpreter implements MiniLanguageInterpreter{
    Map<Class<? extends Preposition>, String> prepositionSeparatorRegexStringMap = new HashMap<>();
    Map<Class<? extends Noun>, Set<String>> pronounStringSetMap = new HashMap<>();
    Map<Class<? extends Noun>, Set<String>> nounStringSetMap = new HashMap<>();
    Map<Class<? extends Adjective>, Set<String>> adjectiveStringSetMap = new HashMap<>();
    YodaEnvironment yodaEnvironment;

    public NounPhraseInterpreter(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
        for (Class<? extends Preposition> prepositionClass : Ontology.prepositionClasses) {
            try {
                Set<String> relationalPhraseStrings = this.yodaEnvironment.lex.getPOSForClass(prepositionClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.PREPOSITION, false);
                relationalPhraseStrings = relationalPhraseStrings.stream().
                        map(x -> x.equals("") ? x : " "+x+" ").collect(Collectors.toSet());
                String regexString = "(" + String.join("|", relationalPhraseStrings) + ")";
                if (!regexString.equals("()"))
                    prepositionSeparatorRegexStringMap.put(prepositionClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

        for (Class<? extends Noun> nounClass : Ontology.nounClasses) {
            try {
                Set<String> pronounStrings = this.yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, false);
                pronounStringSetMap.put(nounClass, pronounStrings);
//                String regexString = "(" + String.join("|", pronounStrings) + ")";
//                if (!regexString.equals("()"))
//                    pronounStringSetMap.put(nounClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

        for (Class<? extends Noun> nounClass : Ontology.nounClasses) {
            Set<String> nounStrings = new HashSet<>();
            try {
                nounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, false));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            try {
                nounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, false));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            nounStringSetMap.put(nounClass, nounStrings);
//            String regexString = "(" + String.join("|", nounStrings) + ")";
//            if (!regexString.equals("()"))
//                nounStringSetMap.put(nounClass, regexString);
        }

        for (Class<? extends Adjective> adjectiveClass : Ontology.adjectiveClasses) {
            try {
                Set<String> adjectiveStrings = this.yodaEnvironment.lex.getPOSForClass(adjectiveClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, false);
                adjectiveStringSetMap.put(adjectiveClass, adjectiveStrings);
//                String regexString = "(" + String.join("|", adjectiveStrings) + ")";
//                if (!regexString.equals("()"))
//                    adjectiveStringSetMap.put(adjectiveClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        String utterance = String.join(" ", tokens).trim();
        String entity1String = utterance;
        String entity2String = null;
        JSONObject entity1JSON = SemanticsModel.parseJSON("{}");
        JSONObject entity2JSON = SemanticsModel.parseJSON("{}");
        Class<? extends Preposition> prepositionClass = null;

        // check for PP separator (indicates a nested noun phrase) (only support 1 level of nesting)
        for (Class<? extends Preposition> cls : prepositionSeparatorRegexStringMap.keySet()){
            Pattern regexPattern = Pattern.compile("(.+)" + prepositionSeparatorRegexStringMap.get(cls) + "(.+)");
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                entity1String = matcher.group(1).trim();
                entity2String = matcher.group(3).trim();
                prepositionClass = cls;

                try {
                    Class<? extends TransientQuality> qualityClass = prepositionClass.newInstance().getQuality();
                    Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor = Ontology.qualityDescriptors(qualityClass);
                    JSONObject intermediateObject = SemanticsModel.parseJSON("{\"class\":\""+cls.getSimpleName()+"\"}");
                    intermediateObject.put(InRelationTo.class.getSimpleName(), entity2JSON);
                    entity1JSON.put(descriptor.getKey().getSimpleName(), intermediateObject);

                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
            }
        }

        Double entity1CoverageScore = null;
        Double entity2CoverageScore = null;

        Pair<Map<String, Object>, Double> entity1classAndAdjectives = getClassAdjectivesAndRemaining(entity1String);
        entity1JSON.putAll(entity1classAndAdjectives.getLeft());
        entity1CoverageScore = entity1classAndAdjectives.getRight();
        if (entity2String!=null) {
            Pair<Map<String, Object>, Double> entity2classAndAdjectives = getClassAdjectivesAndRemaining(entity2String);
            entity2JSON.putAll(entity2classAndAdjectives.getLeft());
            entity2CoverageScore = entity2classAndAdjectives.getRight();
        }

        double namedEntityScore = 1.0;

        // check for named entities
        if (entity1JSON.isEmpty() || entity1CoverageScore < .75)
            entity1JSON.put(HasName.class.getSimpleName(), entity1String);
        if (entity2String!=null && (entity2JSON.isEmpty() || entity2CoverageScore < .75))
            entity2JSON.put(HasName.class.getSimpleName(), entity2String);

        // if a named entity has no class, give it the Noun class
        if (entity1JSON.containsKey(HasName.class.getSimpleName()) && !entity1JSON.containsKey("class"))
            entity1JSON.put("class", Noun.class.getSimpleName());
        if (entity2JSON.containsKey(HasName.class.getSimpleName()) && !entity2JSON.containsKey("class"))
            entity2JSON.put("class", Noun.class.getSimpleName());

//        // If we think there's a named entity, score it based on how well its string matches known NEs
//        if (entity2JSON.containsKey(HasName.class.getSimpleName())){
//            StringDistribution referenceDistribution =
//                    ReferenceResolution.resolveReference(yodaEnvironment, entity2JSON, false, false);
//            namedEntityScore *= Doubles.min(1.0,
//                    referenceDistribution.get(referenceDistribution.getTopHypothesis()) /
//                            RegexPlusKeywordUnderstander.normalNamedEntityStringSimilarity /
//                            ReferenceResolution.minFocusSalience);
//        }
//        if (entity1JSON.containsKey(HasName.class.getSimpleName())){
//            StringDistribution referenceDistribution =
//                    ReferenceResolution.resolveReference(yodaEnvironment, entity1JSON, false, false);
//            namedEntityScore *= Doubles.min(1.0,
//                    referenceDistribution.get(referenceDistribution.getTopHypothesis()) /
//                            RegexPlusKeywordUnderstander.normalNamedEntityStringSimilarity /
//                            ReferenceResolution.minFocusSalience);
//        }

//        namedEntityScore = Math.pow(namedEntityScore, 3);
//        System.err.println("NPInterpreter: input string:" + utterance);
//        System.err.println("NPInterpreter: namedEntityScore:" + namedEntityScore);

        // default to UnknownThingWithRoles
        if (!entity1JSON.containsKey("class")){
            entity1JSON.put("class", UnknownThingWithRoles.class.getSimpleName());
        }
        if (entity2String!=null && !entity2JSON.containsKey("class")){
            entity2JSON.put("class", UnknownThingWithRoles.class.getSimpleName());
        }
        return new ImmutablePair<>(entity1JSON,
                RegexPlusKeywordUnderstander.nounPhraseInterpreterWeight * namedEntityScore);
    }

    private Pair<Map<String, Object>, Double> getClassAdjectivesAndRemaining(String entityString){
//        String remaining = entityString;
        Map<String,Object> ans = new HashMap<>();
        Double pronounCoverage = 0.0;
        Double bestNounCoverage = 0.0;
        Double totalAdjectiveCoverage = 0.0;

        Class<? extends Noun> nounClass = null;
        Set<Class<? extends Adjective>> adjectiveClasses = new HashSet<>();

        // get class from pronoun
        for (Class<? extends Noun> cls : pronounStringSetMap.keySet()){
            Double coverage = Utils.stringSetBestCoverage(entityString, pronounStringSetMap.get(cls));
            if (coverage > pronounCoverage){
                pronounCoverage = coverage;
                nounClass = cls;
                ans.put("refType", "pronoun");
            }
        }

        // get class from noun
        for (Class<? extends Noun> cls : nounStringSetMap.keySet()) {
            Double coverage = Utils.stringSetBestCoverage(entityString, nounStringSetMap.get(cls));
            if (coverage > bestNounCoverage){
                bestNounCoverage = coverage;
                nounClass = cls;
            }
        }

        // get adjective
        for (Class<? extends Adjective> cls : adjectiveStringSetMap.keySet()) {
            Double coverage = Utils.stringSetBestCoverage(entityString, adjectiveStringSetMap.get(cls));
            if (coverage > 0) {
                totalAdjectiveCoverage += coverage;
                adjectiveClasses.add(cls);
            }
        }

        // create sub-JSON object for adjectives
        for (Class<? extends Adjective> cls : adjectiveClasses){
            try {
                Class<? extends TransientQuality> qualityClass = cls.newInstance().getQuality();
                Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor = Ontology.qualityDescriptors(qualityClass);
                ans.put(descriptor.getKey().getSimpleName(), SemanticsModel.parseJSON("{\"class\":\"" + cls.getSimpleName() + "\"}"));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (nounClass!=null)
            ans.put("class", nounClass.getSimpleName());

//        ans.put("<remaining>", remaining);
//        System.err.println("NPInterpreter: total coverage:" + (pronounCoverage + bestNounCoverage + totalAdjectiveCoverage));
        return new ImmutablePair<>(ans, pronounCoverage + bestNounCoverage + totalAdjectiveCoverage);
    }

}

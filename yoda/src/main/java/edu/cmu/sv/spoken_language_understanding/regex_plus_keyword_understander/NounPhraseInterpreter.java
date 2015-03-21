package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import com.google.common.primitives.Doubles;
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
import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                        Lexicon.LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false);
                String regexString = "(" + String.join("|", relationalPhraseStrings) + ")";
                if (!regexString.equals("()"))
                    prepositionSeparatorRegexStringMap.put(prepositionClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

        for (Class<? extends Noun> nounClass : Ontology.nounClasses) {
            try {
                Set<String> pronounStrings = this.yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false);
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
                        Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            try {
                nounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false));
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
                        Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false);
                adjectiveStringSetMap.put(adjectiveClass, adjectiveStrings);
//                String regexString = "(" + String.join("|", adjectiveStrings) + ")";
//                if (!regexString.equals("()"))
//                    adjectiveStringSetMap.put(adjectiveClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

    }

    private double stringSetCoverage(String phrase, Set<String> matchingStrings){
        double ans = 0.0;
        int adjustedLength = phrase.replace("any ","").replace("the ","").replace("some ","").trim().length();
        for (String matchingString : matchingStrings) {
            Pattern regexPattern = Pattern.compile("(.+ | |)" + matchingString + "( .+| |)");
            Matcher matcher = regexPattern.matcher(phrase);
            if (matcher.matches())
                ans = Doubles.max(ans, matchingString.length() * 1.0 / adjustedLength);
            }
        return Doubles.min(ans, 1.0);
    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
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
                entity1String = matcher.group(1);
                entity2String = matcher.group(3);
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

        Pair<Map<String, Object>, Double> entity1classAndAdjectives = getClassAndAdjectives(entity1String);
        entity1JSON.putAll(entity1classAndAdjectives.getLeft());
        entity1CoverageScore = entity1classAndAdjectives.getRight();
        if (entity2String!=null) {
            Pair<Map<String, Object>, Double> entity2classAndAdjectives = getClassAndAdjectives(entity2String);
            entity2JSON.putAll(entity2classAndAdjectives.getLeft());
            entity2CoverageScore = entity2classAndAdjectives.getRight();
        }

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

        // default to UnknownThingWithRoles
        if (!entity1JSON.containsKey("class")){
            entity1JSON.put("class", UnknownThingWithRoles.class.getSimpleName());
        }
        if (entity2String!=null && !entity2JSON.containsKey("class")){
            entity2JSON.put("class", UnknownThingWithRoles.class.getSimpleName());
        }
        return new ImmutablePair<>(entity1JSON, 1.0);
    }

    private Pair<Map<String, Object>, Double> getClassAndAdjectives(String entityString){
        Map<String,Object> ans = new HashMap<>();
        Double pronounCoverage = 0.0;
        Double bestNounCoverage = 0.0;
        Double totalAdjectiveCoverage = 0.0;

        Class<? extends Noun> nounClass = null;
        Set<Class<? extends Adjective>> adjectiveClasses = new HashSet<>();

        // get class from pronoun
        for (Class<? extends Noun> cls : pronounStringSetMap.keySet()){
            Double coverage = stringSetCoverage(entityString, pronounStringSetMap.get(cls));
            if (coverage > pronounCoverage){
                pronounCoverage = coverage;
                nounClass = cls;
                ans.put("refType", "pronoun");
            }
        }

        // get class from noun
        for (Class<? extends Noun> cls : nounStringSetMap.keySet()) {
            Double coverage = stringSetCoverage(entityString, nounStringSetMap.get(cls));
            if (coverage > bestNounCoverage){
                bestNounCoverage = coverage;
                nounClass = cls;
            }
        }

        // get adjective
        for (Class<? extends Adjective> cls : adjectiveStringSetMap.keySet()) {
            Double coverage = stringSetCoverage(entityString, adjectiveStringSetMap.get(cls));
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
        return new ImmutablePair<>(ans, pronounCoverage + bestNounCoverage + totalAdjectiveCoverage);
    }

}

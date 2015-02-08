package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.HasName;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.ontology.role.Role;
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
    static Map<Class<? extends Preposition>, String> prepositionSeparatorRegexStringMap = new HashMap<>();
    static Map<Class<? extends Noun>, String> pronounRegexStringMap = new HashMap<>();
    static Map<Class<? extends Noun>, String> nounRegexStringMap = new HashMap<>();
    static Map<Class<? extends Adjective>, String> adjectiveRegexStringMap = new HashMap<>();

    static {
        for (Class<? extends Preposition> prepositionClass : OntologyRegistry.prepositionClasses) {
            try {
                Set<String> relationalPhraseStrings = Lexicon.getPOSForClass(prepositionClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false);
                String regexString = "(" + String.join("|", relationalPhraseStrings) + ")";
                if (!regexString.equals("()"))
                    prepositionSeparatorRegexStringMap.put(prepositionClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

        for (Class<? extends Noun> nounClass : OntologyRegistry.nounClasses) {
            try {
                Set<String> pronounStrings = Lexicon.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false);
                String regexString = "(" + String.join("|", pronounStrings) + ")";
                if (!regexString.equals("()"))
                    pronounRegexStringMap.put(nounClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

        for (Class<? extends Noun> nounClass : OntologyRegistry.nounClasses) {
            Set<String> nounStrings = new HashSet<>();
            try {
                nounStrings.addAll(Lexicon.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            try {
                nounStrings.addAll(Lexicon.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            String regexString = "(" + String.join("|", nounStrings) + ")";
            if (!regexString.equals("()"))
                nounRegexStringMap.put(nounClass, regexString);
        }

        for (Class<? extends Adjective> adjectiveClass : OntologyRegistry.adjectiveClasses) {
            try {
                Set<String> adjectiveStrings = Lexicon.getPOSForClass(adjectiveClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, false);
                String regexString = "(" + String.join("|", adjectiveStrings) + ")";
                if (!regexString.equals("()"))
                    adjectiveRegexStringMap.put(adjectiveClass, regexString);
            } catch (Lexicon.NoLexiconEntryException e) {}
        }

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
                    Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor = OntologyRegistry.qualityDescriptors(qualityClass);
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

        entity1JSON.putAll(getClassAndAdjectives(entity1String));
        if (entity2String!=null)
            entity2JSON.putAll(getClassAndAdjectives(entity2String));


        // check for named entities
        if (entity1JSON.isEmpty()){
            String uri = yodaEnvironment.db.insertValue(entity1String);
            JSONObject namedEntity = SemanticsModel.parseJSON(OntologyRegistry.webResourceWrap(uri));
            entity1JSON.put("class", PointOfInterest.class.getSimpleName());
            entity1JSON.put(HasName.class.getSimpleName(), namedEntity);
        }
        if (entity2String!=null && entity2JSON.isEmpty()){
            String uri = yodaEnvironment.db.insertValue(entity2String);
            JSONObject namedEntity = SemanticsModel.parseJSON(OntologyRegistry.webResourceWrap(uri));
            entity2JSON.put("class", PointOfInterest.class.getSimpleName());
            entity2JSON.put(HasName.class.getSimpleName(), namedEntity);
        }

        // default to UnknownThingWithRoles
        if (!entity1JSON.containsKey("class")){
            entity1JSON.put("class", UnknownThingWithRoles.class.getSimpleName());
        }
        if (entity2String!=null && !entity2JSON.containsKey("class")){
            entity2JSON.put("class", UnknownThingWithRoles.class.getSimpleName());
        }

        return new ImmutablePair<>(entity1JSON, 1.0);
    }

    private Map<String, Object> getClassAndAdjectives(String entityString){
        Map<String,Object> ans = new HashMap<>();

        Class<? extends Noun> nounClass = null;
        Set<Class<? extends Adjective>> adjectiveClasses = new HashSet<>();

        // get class from pronoun
        for (Class<? extends Noun> cls : pronounRegexStringMap.keySet()) {
            Pattern regexPattern = Pattern.compile("(.+ | |)" + pronounRegexStringMap.get(cls) + "( .+| |)");
            Matcher matcher = regexPattern.matcher(entityString);
            if (matcher.matches()) {
                nounClass = cls;
                ans.put("refType", "pronoun");
                break;
            }
        }

        // get class from noun
        for (Class<? extends Noun> cls : nounRegexStringMap.keySet()) {
            Pattern regexPattern = Pattern.compile("(.+ | |)" + nounRegexStringMap.get(cls) + "( .+| |)");
            Matcher matcher = regexPattern.matcher(entityString);
            if (matcher.matches()) {
                nounClass = cls;
                break;
            }
        }

        // get adjective
        for (Class<? extends Adjective> cls : adjectiveRegexStringMap.keySet()) {
            Pattern regexPattern = Pattern.compile("(.+ | |)" + adjectiveRegexStringMap.get(cls) + "( .+| |)");
            Matcher matcher = regexPattern.matcher(entityString);
            if (matcher.matches()) {
                adjectiveClasses.add(cls);
            }
        }

        // create sub-JSON object for adjectives
        for (Class<? extends Adjective> cls : adjectiveClasses){
            try {
                Class<? extends TransientQuality> qualityClass = cls.newInstance().getQuality();
                Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor = OntologyRegistry.qualityDescriptors(qualityClass);
                ans.put(descriptor.getKey().getSimpleName(), SemanticsModel.parseJSON("{\"class\":\"" + cls.getSimpleName() + "\"}"));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (nounClass!=null)
            ans.put("class", nounClass.getSimpleName());

        return ans;
    }

}

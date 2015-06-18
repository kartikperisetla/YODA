package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology2.Role2;
import edu.cmu.sv.domain.ontology2.Verb2;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class CommandMultiInterpreter implements MiniMultiLanguageInterpreter {
    Verb2 verbClass;
    String verbRegexString = "()";
    String adjectiveRegexString = "()";
    Map<Role2, String> roleObj1PrefixPatterns = new HashMap<>();
    Map<Role2, String> roleObj2PrefixPatterns = new HashMap<>();
    Map<Role2, Boolean> r1HasBlankPrefix = new HashMap<>();
    Map<Role2, Boolean> r2HasBlankPrefix = new HashMap<>();
    YodaEnvironment yodaEnvironment;

    public CommandMultiInterpreter(Verb2 verbClass, YodaEnvironment yodaEnvironment) {
        this.verbClass = verbClass;
        this.yodaEnvironment = yodaEnvironment;

        // get noun forms
        Set<String> verbNounStrings = new HashSet<>();
        try {
            verbNounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        try{
            verbNounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        verbRegexString = "("+String.join("|",verbNounStrings)+")";

        // get adjective forms
        Set<String> verbAdjectiveStrings = new HashSet<>();
        try {
            verbAdjectiveStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        adjectiveRegexString = "("+String.join("|",verbAdjectiveStrings)+")";

        // get role prefixes
        for (Role2 roleClass : Ontology.roles) {
            if (Ontology.inDomain(roleClass, verbClass)) {
                Set<String> roleObj1PrefixStrings = new HashSet<>();
                Set<String> roleObj2PrefixStrings = new HashSet<>();
                try {
                    roleObj1PrefixStrings = this.yodaEnvironment.lex.getPOSForClass(roleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, true);
                } catch (Lexicon.NoLexiconEntryException e) {
                }
                try {
                    roleObj2PrefixStrings = this.yodaEnvironment.lex.getPOSForClass(roleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, true);
                } catch (Lexicon.NoLexiconEntryException e) {
                }
                r1HasBlankPrefix.put(roleClass, roleObj1PrefixStrings.contains(""));
                r2HasBlankPrefix.put(roleClass, roleObj2PrefixStrings.contains(""));
                roleObj1PrefixStrings.remove("");
                roleObj2PrefixStrings.remove("");
                String regexString = "(" + String.join("|", roleObj1PrefixStrings) + ")";
                String regexString2 = "(" + String.join("|", roleObj2PrefixStrings) + ")";
//                    System.err.println(regexString);
//                    System.err.println(regexString2);
                if (roleObj1PrefixStrings.size() != 0)
                    roleObj1PrefixPatterns.put(roleClass, regexString);
                if (roleObj2PrefixStrings.size() != 0)
                    roleObj2PrefixPatterns.put(roleClass, regexString2);
            }
        }
//        System.err.println("CommandMultiInterpreter: constructor: verb:"+verbClass.getSimpleName()+", roles in prefix patterns:" + roleObj1PrefixPatterns.keySet());
//        System.err.println("CommandMultiInterpreter: constructor: verb:"+verbClass.getSimpleName()+", roles in obj2 prefix patterns:" + roleObj2PrefixPatterns.keySet());
    }

    @Override
    public NBestDistribution<JSONObject> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        NBestDistribution<JSONObject> ans = new NBestDistribution<>();
        String utterance = String.join(" ", tokens);

        if (!adjectiveRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("(.* |)" +
                    MiniLanguageInterpreter.putInStateVerbRegexString + "(.+?)" + adjectiveRegexString + MiniLanguageInterpreter.endingPolitenessRegexString);
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                System.err.println("CommandMultiInterpreter: Adjective form initial match");
                String phraseIntroString = matcher.group(1).trim();
                System.err.println("phrase intro string:" + phraseIntroString);
                Pattern negationPattern = Pattern.compile("(.* |)" + MiniLanguageInterpreter.negationRegexString + "( .*|)");
                Matcher negationMatcher = negationPattern.matcher(phraseIntroString);
                if (!negationMatcher.matches()) {
                    String obj1String = matcher.group(3).trim();
                    System.err.println("obj1string:" + obj1String);
                    for (Role2 roleClass : r1HasBlankPrefix.keySet()) {
                        String rolePrefixRegexString = roleObj1PrefixPatterns.containsKey(roleClass) ? roleObj1PrefixPatterns.get(roleClass) : "()";
                        if (r1HasBlankPrefix.get(roleClass))
                            rolePrefixRegexString = new StringBuilder(rolePrefixRegexString).insert(rolePrefixRegexString.length() - 1, "|").toString();
                        Pattern obj1Pattern = Pattern.compile(rolePrefixRegexString + "(.+)");
                        Matcher matcher2 = obj1Pattern.matcher(obj1String);
                        if (matcher2.matches()) {
                            String npString = matcher2.group(2);
                            Pair<JSONObject, Double> npInterpretation;
                            if (roleClass.equals(YodaSkeletonOntologyRegistry.hasAtTime))
                                npInterpretation = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                        timeInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);
                            else
                                npInterpretation = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                        nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);

                            if (npInterpretation == null)
                                continue;

                            String jsonString = "{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"" + verbClass.name + "\"}}";
                            JSONObject hyp = SemanticsModel.parseJSON(jsonString);
                            ((JSONObject) hyp.get("verb")).put(roleClass.name, npInterpretation.getKey());
                            ans.put(hyp, RegexPlusKeywordUnderstander.regexInterpreterWeight);
                        }
                    }
                }
            }
        }



        if (!verbRegexString.equals("()")) {
            {
                // command with one role as the obj1
//                Pattern regexPattern = Pattern.compile(MiniLanguageInterpreter.startingPolitenessRegexString +
//                        "(could you |can you |will you please |)" +
//                        "(i'd like |i would like |i want |give me |give |can i get |could i get |make |set up |)(a |the |some |)" +
//                        verbRegexString + "(.+)" + MiniLanguageInterpreter.endingPolitenessRegexString);
                Pattern regexPattern = Pattern.compile("(.* |)" +
                        verbRegexString + "(.+?)" + MiniLanguageInterpreter.endingPolitenessRegexString);
                Matcher matcher = regexPattern.matcher(utterance);
                if (matcher.matches()) {
                    System.err.println("CommandMultiInterpreter: 1-role initial match");
                    String phraseIntroString = matcher.group(1).trim();
                    System.err.println("phrase intro string:" + phraseIntroString);
                    Pattern negationPattern = Pattern.compile("(.* |)" + MiniLanguageInterpreter.negationRegexString + "( .*|)");
                    Matcher negationMatcher = negationPattern.matcher(phraseIntroString);
                    if (!negationMatcher.matches()) {
                        String obj1String = matcher.group(3).trim();
                        System.err.println("obj1string:" + obj1String);
                        for (Role2 roleClass : r1HasBlankPrefix.keySet()) {
                            String rolePrefixRegexString = roleObj1PrefixPatterns.containsKey(roleClass) ? roleObj1PrefixPatterns.get(roleClass) : "()";
                            if (r1HasBlankPrefix.get(roleClass))
                                rolePrefixRegexString = new StringBuilder(rolePrefixRegexString).insert(rolePrefixRegexString.length() - 1, "|").toString();
                            Pattern obj1Pattern = Pattern.compile(rolePrefixRegexString + "(.+)");
                            Matcher matcher2 = obj1Pattern.matcher(obj1String);
                            if (matcher2.matches()) {
                                String npString = matcher2.group(2);
                                Pair<JSONObject, Double> npInterpretation;
                                if (roleClass.equals(YodaSkeletonOntologyRegistry.hasAtTime))
                                    npInterpretation = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                            timeInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);
                                else
                                    npInterpretation = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                            nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);

                                if (npInterpretation == null)
                                    continue;

                                String jsonString = "{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"" + verbClass.name + "\"}}";
                                JSONObject hyp = SemanticsModel.parseJSON(jsonString);
                                ((JSONObject) hyp.get("verb")).put(roleClass.name, npInterpretation.getKey());
                                ans.put(hyp, RegexPlusKeywordUnderstander.regexInterpreterWeight);
                            }
                        }
                    }
                }
            }

            {
                // command with two roles as obj1 and obj2
//                Pattern regexPattern = Pattern.compile(MiniLanguageInterpreter.startingPolitenessRegexString +
//                        "(could you |can you |will you please |)" +
//                        "(i'd like |i would like |i want |give me |give |can i get |could i get |make |set up |)(a |the |some |)" +
//                        verbRegexString + "(.+)" + MiniLanguageInterpreter.endingPolitenessRegexString);
                Pattern regexPattern = Pattern.compile("(.* |)" +
                        verbRegexString + "(.+?)" + MiniLanguageInterpreter.endingPolitenessRegexString);
                Matcher matcher = regexPattern.matcher(utterance);
                if (matcher.matches()) {
                    System.err.println("CommandMultiInterpreter: 2-role initial match");
                    String phraseIntroString = matcher.group(1).trim();
                    System.err.println("phrase intro string:" + phraseIntroString);
                    Pattern negationPattern = Pattern.compile("(.* |)" + MiniLanguageInterpreter.negationRegexString + "( .*|)");
                    Matcher negationMatcher = negationPattern.matcher(phraseIntroString);
                    if (!negationMatcher.matches()) {

                        String twoRoleString = matcher.group(3).trim();
                        System.err.println("twoRoleString:" + twoRoleString);

                        for (Role2 roleClass1 : roleObj1PrefixPatterns.keySet()) {
                            for (Role2 roleClass2 : roleObj2PrefixPatterns.keySet()) {
                                Pattern multiRolePattern = Pattern.compile(roleObj1PrefixPatterns.get(roleClass1) +
                                        " (.+) " +
                                        roleObj2PrefixPatterns.get(roleClass2) +
                                        " (.+)");
                                Matcher matcher2 = multiRolePattern.matcher(twoRoleString);
                                if (matcher2.matches()) {
//                                System.err.println("Match groups:");
//                                for (int i = 0; i <= matcher2.groupCount(); i++) {
//                                    System.err.println(i + " : " + matcher2.group(i));
//                                }
                                    String objString1 = matcher2.group(2);
                                    String objString2 = matcher2.group(4);
                                    Pair<JSONObject, Double> npInterpretation1;
                                    if (roleClass1.equals(YodaSkeletonOntologyRegistry.hasAtTime))
                                        npInterpretation1 = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                                timeInterpreter.interpret(Tokenizer.tokenize(objString1), yodaEnvironment);
                                    else
                                        npInterpretation1 = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                                nounPhraseInterpreter.interpret(Tokenizer.tokenize(objString1), yodaEnvironment);

                                    Pair<JSONObject, Double> npInterpretation2;
                                    if (roleClass2.equals(YodaSkeletonOntologyRegistry.hasAtTime))
                                        npInterpretation2 = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                                timeInterpreter.interpret(Tokenizer.tokenize(objString2), yodaEnvironment);
                                    else
                                        npInterpretation2 = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                                nounPhraseInterpreter.interpret(Tokenizer.tokenize(objString2), yodaEnvironment);

                                    if (npInterpretation1 == null || npInterpretation2 == null)
                                        continue;

                                    String jsonString = "{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"" + verbClass.name + "\"}}";
                                    JSONObject hyp = SemanticsModel.parseJSON(jsonString);
                                    ((JSONObject) hyp.get("verb")).put(roleClass1.name, npInterpretation1.getKey());
                                    ((JSONObject) hyp.get("verb")).put(roleClass2.name, npInterpretation2.getKey());
                                    ans.put(hyp, RegexPlusKeywordUnderstander.regexInterpreterWeight);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ans;
    }
}

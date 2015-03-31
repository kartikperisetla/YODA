package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class CommandRegexInterpreter implements MiniLanguageInterpreter {
    Class<? extends Verb> verbClass;
    String verbRegexString = "()";
    Map<Class<? extends Role>, String> roleObj1PrefixPatterns = new HashMap<>();
    YodaEnvironment yodaEnvironment;

    public CommandRegexInterpreter(Class<? extends Verb> verbClass, YodaEnvironment yodaEnvironment) {
        this.verbClass = verbClass;
        this.yodaEnvironment = yodaEnvironment;
        Set<String> verbNounStrings = new HashSet<>();
        try {
            verbNounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        try{
            verbNounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        verbRegexString = "("+String.join("|",verbNounStrings)+")";
        for (Class<? extends Role> roleClass : Ontology.roleClasses) {
            if (Ontology.inDomain(roleClass, verbClass)) {
                try {
                    Set<String> roleObj1PrefixStrings = this.yodaEnvironment.lex.getPOSForClass(roleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true);
                    String regexString = "("+String.join("|",roleObj1PrefixStrings)+")";
                    if (roleObj1PrefixStrings.size() != 0)
                        roleObj1PrefixPatterns.put(roleClass, regexString);
                } catch (Lexicon.NoLexiconEntryException e) {}
            }
        }
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        String utterance = String.join(" ", tokens);
        if (!verbRegexString.equals("()")) {
            // command with one role as the obj1

            Pattern regexPattern = Pattern.compile(startingPolitenessRegexString + "(could you |can you |will you please |)" +
                    "(i'd like |i would like |i want |give me |give |can i get |could i get |make |set up |)(a |the |some |)" + verbRegexString + "(.+)" + endingPolitenessRegexString);
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String obj1String = matcher.group(6);
                for (Class<? extends Role> roleClass : roleObj1PrefixPatterns.keySet()) {
                    Pattern obj1Pattern = Pattern.compile(roleObj1PrefixPatterns.get(roleClass) + "(.+)");
                    Matcher matcher2 = obj1Pattern.matcher(obj1String);
                    if (matcher2.matches()) {
                        String npString = matcher2.group(2);
                        Pair<JSONObject, Double> npInterpretation = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                                nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);
                        String jsonString = "{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"" + verbClass.getSimpleName() + "\"}}";
                        JSONObject ans = SemanticsModel.parseJSON(jsonString);
                        ((JSONObject) ans.get("verb")).put(roleClass.getSimpleName(), npInterpretation.getKey());
                        return new ImmutablePair<>(ans, RegexPlusKeywordUnderstander.regexInterpreterWeight);
                    }
                }

            }

        }
        return null;
    }
}

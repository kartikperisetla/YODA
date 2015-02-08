package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class CommandRegexInterpreter implements MiniLanguageInterpreter {
    Class<? extends Verb> verbClass;
    String verbRegexString = "()";
    Map<Class<? extends Role>, String> roleObj1PrefixPatterns = new HashMap<>();

    public CommandRegexInterpreter(Class<? extends Verb> verbClass) {
        this.verbClass = verbClass;
        try {
            Set<String> verbNounStrings = Lexicon.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true);
            verbNounStrings.addAll(Lexicon.getPOSForClass(verbClass, Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true));
            this.verbRegexString = "("+String.join("|",verbNounStrings)+")";
        } catch (Lexicon.NoLexiconEntryException e) {}
        for (Class<? extends Role> roleClass : OntologyRegistry.roleClasses) {
            if (OntologyRegistry.inDomain(roleClass, verbClass)) {
                try {
                    Set<String> roleObj1PrefixStrings = Lexicon.getPOSForClass(roleClass, Lexicon.LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true);
                    String regexString = "("+String.join("|",roleObj1PrefixStrings)+")";
                    if (!regexString.equals("()"))
                        roleObj1PrefixPatterns.put(roleClass, regexString);
                } catch (Lexicon.NoLexiconEntryException e) {}
            }
        }
    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        if (!verbRegexString.equals("()")) {
            // command with no roles provided
            {
                Pattern regexPattern = Pattern.compile(startingPolitenessRegexString + "(could you |can you |will you please |)" +
                        "(i want |give me |give |can i get |could i get |)(the |some |)" + verbRegexString + endingPolitenessRegexString);
                Matcher matcher = regexPattern.matcher(utterance);
                if (matcher.matches()) {
                    String jsonString = "{\"dialogAct\":\"Command\",\"verb\":{\"class\":\""+verbClass.getSimpleName()+"\"}}";
                    return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), 1.0);
                }
            }
            // command with one role as the obj1
            {
                Pattern regexPattern = Pattern.compile(startingPolitenessRegexString + "(could you |can you |will you please |)" +
                        "(i want |give me |give |can i get |could i get |)(the |some |)" + verbRegexString + "(.+)" +endingPolitenessRegexString);
                Matcher matcher = regexPattern.matcher(utterance);
                if (matcher.matches()) {
                    String obj1String = matcher.group(6);
                    for (Class<? extends Role> roleClass : roleObj1PrefixPatterns.keySet()){
                        Pattern obj1Pattern = Pattern.compile(roleObj1PrefixPatterns.get(roleClass)+"(.+)");
                        Matcher matcher2 = obj1Pattern.matcher(obj1String);
                        if (matcher2.matches()) {
                            String npString = matcher2.group(2);
                            Pair<JSONObject, Double> npInterpretation =
                                    RegexPlusKeywordUnderstander.nounPhraseInterpreter.interpret(npString, yodaEnvironment);
                            String jsonString = "{\"dialogAct\":\"Command\",\"verb\":{\"class\":\""+verbClass.getSimpleName()+"\"}}";
                            JSONObject ans = SemanticsModel.parseJSON(jsonString);
                            ((JSONObject)ans.get("verb")).put(roleClass.getSimpleName(),npInterpretation.getKey());
                            return new ImmutablePair<>(ans, 1.0);
                        }
                    }

                }
            }

        }
        return null;
    }
}

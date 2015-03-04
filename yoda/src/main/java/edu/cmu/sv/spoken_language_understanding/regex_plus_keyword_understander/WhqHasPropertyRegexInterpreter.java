package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.ontology.Ontology;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 1/21/15.
 */
public class WhqHasPropertyRegexInterpreter implements MiniLanguageInterpreter {
    Class<? extends TransientQuality> qualityClass;
    Class<? extends Role> hasQualityRole;
    String adjectiveRegexString = "()";
    String qualityNounRegexString = "()";
    YodaEnvironment yodaEnvironment;

    public WhqHasPropertyRegexInterpreter(Class<? extends TransientQuality> qualityClass, YodaEnvironment yodaEnvironment) {
        this.qualityClass = qualityClass;
        this.yodaEnvironment = yodaEnvironment;
        Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor = Ontology.qualityDescriptors(qualityClass);
        this.hasQualityRole = descriptor.getKey();
        Set<Class<? extends Adjective>> adjectiveClasses = descriptor.getRight().stream().
                filter(Adjective.class::isAssignableFrom).
                map(x -> (Class<? extends Adjective>) x).
                collect(Collectors.toSet());

        Set<String> adjectiveStrings = new HashSet<>();
        for (Class<? extends Adjective> adjectiveClass : adjectiveClasses) {
            try {
                adjectiveStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(adjectiveClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true));
            } catch (Lexicon.NoLexiconEntryException e) {}
        }
        this.adjectiveRegexString = "(" + String.join("|", adjectiveStrings) + ")";

        Set<String> nounStrings = new HashSet<>();
        try {
            nounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(qualityClass, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        this.qualityNounRegexString = "(" + String.join("|", nounStrings) + ")";

    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        if (!adjectiveRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("how " + adjectiveRegexString + "( is | are | )(.+)");
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String npString = matcher.group(3);
                Pair<JSONObject, Double> npInterpretation =
                        ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(npString, yodaEnvironment);

                String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":"+
                        npInterpretation.getKey().toJSONString()+
                        ",\"Patient\":" +
                        "{\"class\":\"Requested\",\"HasValue\":{\"class\":\"" +
                        qualityClass.getSimpleName() + "\"}},\"class\":\"HasProperty\"}}";
                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), 1.0);
            }
        }
        if (!qualityNounRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("(what |how much |what's |what're )(is |are |)(the |)" +
                    qualityNounRegexString +
                    " (is |are |)"+MiniLanguageInterpreter.possessivePrepositionRegexString+"(.+)");
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String npString = matcher.group(7);
                Pair<JSONObject, Double> npInterpretation =
                        ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(npString, yodaEnvironment);

                String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":"+
                        npInterpretation.getKey().toJSONString()+", \"Patient\":" +
                        "{\"class\":\"Requested\",\"HasValue\":{\"class\":\"" +
                        qualityClass.getSimpleName() + "\"}},\"class\":\"HasProperty\"}}";
                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), 1.0);
            }
        }
        return null;
    }
}

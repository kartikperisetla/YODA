package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology2.Quality2;
import edu.cmu.sv.domain.ontology2.QualityDegree;
import edu.cmu.sv.domain.ontology2.Role2;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 1/21/15.
 */
public class WhqHasPropertyRegexInterpreter implements MiniLanguageInterpreter {
    Quality2 qualityClass;
    Role2 hasQualityRole;
    String adjectiveRegexString = "()";
    String qualityNounRegexString = "()";
    YodaEnvironment yodaEnvironment;

    public WhqHasPropertyRegexInterpreter(Quality2 qualityClass, YodaEnvironment yodaEnvironment) {
        this.qualityClass = qualityClass;
        this.yodaEnvironment = yodaEnvironment;
        Pair<Role2, Set<QualityDegree>> descriptor = Ontology.qualityDescriptors(qualityClass);
        this.hasQualityRole = descriptor.getKey();
        Set<QualityDegree> adjectiveClasses = descriptor.getRight().stream().
                filter(x -> x.getQuality().secondArgumentClassConstraint == null).
                collect(Collectors.toSet());

        Set<String> adjectiveStrings = new HashSet<>();
        for (QualityDegree adjectiveClass : adjectiveClasses) {
            try {
                adjectiveStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(adjectiveClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, true));
            } catch (Lexicon.NoLexiconEntryException e) {}
        }
        this.adjectiveRegexString = "(" + String.join("|", adjectiveStrings) + ")";

        Set<String> nounStrings = new HashSet<>();
        try {
            nounStrings.addAll(this.yodaEnvironment.lex.getPOSForClass(qualityClass, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, true));
        } catch (Lexicon.NoLexiconEntryException e) {}
        this.qualityNounRegexString = "(" + String.join("|", nounStrings) + ")";

    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        String utterance = String.join(" ", tokens);
        if (!adjectiveRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("how " + adjectiveRegexString + "( is | are | )(.+)");
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String npString = matcher.group(3);
                Pair<JSONObject, Double> npInterpretation =
                        ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);

                String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":"+
                        npInterpretation.getKey().toJSONString()+
                        ",\"Patient\":" +
                        "{\"class\":\"Requested\",\"HasValue\":{\"class\":\"" +
                        qualityClass.name + "\"}},\"class\":\"HasProperty\"}}";
                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), RegexPlusKeywordUnderstander.regexInterpreterWeight);
            }
        }
        if (!qualityNounRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("(what |how much |what 's |what are )(is |are |)(the |)" +
                    qualityNounRegexString +
                    " (is |are |)"+MiniLanguageInterpreter.possessivePrepositionRegexString+"(.+)");
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String npString = matcher.group(7);
                Pair<JSONObject, Double> npInterpretation =
                        ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);

                String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":"+
                        npInterpretation.getKey().toJSONString()+", \"Patient\":" +
                        "{\"class\":\"Requested\",\"HasValue\":{\"class\":\"" +
                        qualityClass.name + "\"}},\"class\":\"HasProperty\"}}";
                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), RegexPlusKeywordUnderstander.regexInterpreterWeight);
            }
        }
        return null;
    }
}

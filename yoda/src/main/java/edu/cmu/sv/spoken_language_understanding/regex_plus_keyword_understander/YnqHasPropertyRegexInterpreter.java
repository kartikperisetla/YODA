package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology.Quality;
import edu.cmu.sv.domain.ontology.QualityDegree;
import edu.cmu.sv.domain.ontology.Role;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class YnqHasPropertyRegexInterpreter implements MiniLanguageInterpreter {
    QualityDegree adjectiveClass;
    Quality qualityClass;
    Role hasQualityRole;
    String adjectiveRegexString = "()";
    YodaEnvironment yodaEnvironment;

    public YnqHasPropertyRegexInterpreter(QualityDegree adjectiveClass, YodaEnvironment yodaEnvironment) {
        this.adjectiveClass = adjectiveClass;
        this.yodaEnvironment = yodaEnvironment;
        try {
            this.qualityClass = adjectiveClass.getQuality();
            this.hasQualityRole = Ontology.qualityDescriptors(qualityClass).getKey();
            Set<String> adjectiveStrings = this.yodaEnvironment.lex.getPOSForClass(adjectiveClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, true);
            this.adjectiveRegexString = "("+String.join("|",adjectiveStrings)+")";
//            System.err.println(this.adjectiveRegexString);
        } catch (Lexicon.NoLexiconEntryException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        String utterance = String.join(" ", tokens);
//        if (!adjectiveRegexString.equals("()")) {
//            Pattern regexPattern = Pattern.compile("(is |are )(the |)?(.+)" + adjectiveRegexString);
//            Matcher matcher = regexPattern.matcher(utterance);
//            if (matcher.matches()) {
//                String npString = matcher.group(3);
//                Pair<JSONObject, Double> npInterpretation =
//                        ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);
//
//                String jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":"+
//                        npInterpretation.getKey().toJSONString()+
//                        ",\"Patient\":{\"class\":\"UnknownThingWithRoles\",\"" +
//                        hasQualityRole.getSimpleName()+
//                        "\":{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}},\"class\":\"HasProperty\"}}";
//                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString),
//                        RegexPlusKeywordUnderstander.regexInterpreterWeight);
//            }
//        }

        if (!adjectiveRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("(is |are )(.*)" + adjectiveRegexString);
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String npString = matcher.group(2).trim();
                Pair<JSONObject, Double> npInterpretation = null;
                if (npString.length() > 0)
                    npInterpretation = ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);

                String jsonString;
                if (npInterpretation==null){
                    jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{" +
                            "\"Patient\":{\"class\":\"UnknownThingWithRoles\",\"" +
                            hasQualityRole.name +
                            "\":{\"class\":\"" + adjectiveClass.name + "\"}},\"class\":\"HasProperty\"}}";
                }
                else {
                    jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":" +
                            npInterpretation.getKey().toJSONString() +
                            ",\"Patient\":{\"class\":\"UnknownThingWithRoles\",\"" +
                            hasQualityRole.name +
                            "\":{\"class\":\"" + adjectiveClass.name + "\"}},\"class\":\"HasProperty\"}}";
                }

                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString),
                        RegexPlusKeywordUnderstander.regexInterpreterWeight);
            }
        }



        return null;
    }
}

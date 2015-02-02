package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class YnqAdjectiveRegexInterpreter implements MiniLanguageInterpreter {
    Class<? extends Adjective> adjectiveClass;
    Class<? extends TransientQuality> qualityClass;
    Class<? extends Role> hasQualityRole;
    String adjectiveRegexString = "()";

    public YnqAdjectiveRegexInterpreter(Class<? extends Adjective> adjectiveClass) {
        this.adjectiveClass = adjectiveClass;
        try {
            this.qualityClass = adjectiveClass.newInstance().getQuality();
            this.hasQualityRole = OntologyRegistry.qualityDescriptors(qualityClass).getKey();
            Set<String> adjectiveStrings = Lexicon.getPOSForClass(adjectiveClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES);
            this.adjectiveRegexString = "("+String.join("|",adjectiveStrings)+")";
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (Lexicon.NoLexiconEntryException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        if (!adjectiveRegexString.equals("()")) {
            Pattern regexPattern = Pattern.compile("(is |are )(the |)?(.+)" + adjectiveRegexString);
            Matcher matcher = regexPattern.matcher(utterance);
            if (matcher.matches()) {
                String npString = matcher.group(3);
                Pair<JSONObject, Double> npInterpretation =
                        RegexPlusKeywordUnderstander.nounPhraseInterpreter.interpret(npString, yodaEnvironment);

                String jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":"+
                        npInterpretation.getKey().toJSONString()+
                        ",\"Patient\":{\"class\":\"UnknownThingWithRoles\",\"" +
                        hasQualityRole.getSimpleName()+
                        "\":{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}},\"class\":\"HasProperty\"}}";
                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), 1.0);
            }
        }
        return null;
    }
}

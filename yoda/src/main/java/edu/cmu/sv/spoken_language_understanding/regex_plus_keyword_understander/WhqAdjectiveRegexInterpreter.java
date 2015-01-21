package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.ontology.OntologyRegistry;
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
public class WhqAdjectiveRegexInterpreter implements MiniLanguageInterpreter {
    Class<? extends TransientQuality> qualityClass;
    Class<? extends Role> hasQualityRole;
    String adjectiveRegexString;
    String qualityNounRegexString;

    public WhqAdjectiveRegexInterpreter(Class<? extends TransientQuality> qualityClass) {
        this.qualityClass = qualityClass;
        Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor = OntologyRegistry.qualityDescriptors(qualityClass);
        this.hasQualityRole = descriptor.getKey();
        Set<Class<? extends Adjective>> adjectiveClasses = descriptor.getRight().stream().
                filter(Adjective.class::isAssignableFrom).
                map(x -> (Class<? extends Adjective>) x).
                collect(Collectors.toSet());
        Set<String> adjectiveStrings = new HashSet<>();
        for (Class<? extends Adjective> adjectiveClass : adjectiveClasses) {
            try {
                adjectiveStrings.addAll(Lexicon.getPOSForClass(adjectiveClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, Grammar.EXHAUSTIVE_GENERATION_PREFERENCES));
            } catch (Lexicon.NoLexiconEntryException e) {
//                e.printStackTrace();
            }
        }
        this.adjectiveRegexString = "(" + String.join("|", adjectiveStrings) + ")";

    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        //todo: finish implementing
        Pattern regexPattern = Pattern.compile("(is |are )(the |)?(.+)"+adjectiveRegexString);
        Matcher matcher = regexPattern.matcher(utterance);
        if (matcher.matches()) {
            String PoiName = matcher.group(3);
            String uri = yodaEnvironment.db.insertValue(PoiName);
            String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":{\"HasName\":{\"HasURI\":\"" +
                    uri + "\",\"class\":\"WebResource\"},\"class\":\"PointOfInterest\"},\"Patient\":{\"class\":\"UnknownThingWithRoles\",\""+
                    hasQualityRole.getSimpleName()+"\":{\"class\":\""+adjectiveClass.getSimpleName()+"\"}},\"class\":\"HasProperty\"}}";
            return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), 1.0);
        } else
            return null;
    }
}

package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import com.google.common.primitives.Doubles;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology.Noun;
import edu.cmu.sv.domain.ontology.Quality;
import edu.cmu.sv.domain.ontology.QualityDegree;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Utils;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 7/21/15.
 *
 * used to determine semantics for phrases such as "x's y" and sentences like "does y have any xes?"
 *
 */
public class RelationInterpreter {
    Map<Noun, Set<String>> nounStringMap;
    Map<QualityDegree, Set<String>> qualityDegreeStringMap;
    YodaEnvironment yodaEnvironment;

    public RelationInterpreter(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
        for (QualityDegree degree : Ontology.qualityDegrees){
            try {
                Set<String> qualityDegreesAsNoun = yodaEnvironment.lex.getPOSForClass(degree, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, true);
                qualityDegreesAsNoun.addAll(yodaEnvironment.lex.getPOSForClass(degree, Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, true));
                if (qualityDegreesAsNoun.size() > 0)
                    qualityDegreeStringMap.put(degree, qualityDegreesAsNoun);
            } catch (Lexicon.NoLexiconEntryException e) {
                e.printStackTrace();
            }
        }

        for (Noun noun : Ontology.nouns){
            try {
                Set<String> asNoun = yodaEnvironment.lex.getPOSForClass(noun, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, true);
                asNoun.addAll(yodaEnvironment.lex.getPOSForClass(noun, Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, true));
                if (asNoun.size() > 0)
                    nounStringMap.put(noun, asNoun);
            } catch (Lexicon.NoLexiconEntryException e) {
                e.printStackTrace();
            }
        }
    }

    NBestDistribution<JSONObject> interpret(List<String> relationTokens, List<String> npTokens){
        //// determine constraints set by NP
        Pair<JSONObject, Double> npInterpretation =
                ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(npTokens, yodaEnvironment);
        Noun domainNoun = Ontology.nounNameMap.get(npInterpretation.getLeft().get("class"));


        NBestDistribution<QualityDegree> relationDistribution = new NBestDistribution<>();

        //// determine relation distribution based on lexicon and semantic constraints
        // get relation from quality degree lexicon
        for (QualityDegree cls : qualityDegreeStringMap.keySet()) {
            Noun qualityDomainConstraint = cls.getQuality().firstArgumentClassConstraint;
            double constraintMatch = Ontology.semanticConstraintMatch(domainNoun, qualityDomainConstraint);
            if (constraintMatch < .0001)
                continue;
            Double coverage = Utils.stringSetBestCoverage(String.join(" ", relationTokens), qualityDegreeStringMap.get(cls));
            if (coverage > 0) {
                relationDistribution.put(cls, Double.max(relationDistribution.get(cls), coverage*constraintMatch));
            }
        }

//        // get relation from noun / any quality degree which can have that role as its second element
//        for (Noun cls : nounStringMap.keySet()){
//            Double coverage = Utils.stringSetBestCoverage(String.join(" ", relationTokens), nounStringMap.get(cls));
//            for (Quality quality : Ontology.qualities){
//                if (coverage > 0) {
//                    relationDistribution.put(cls, Double.max(relationDistribution.get(cls), coverage));
//                }
//
//            }
//        }

        relationDistribution.normalize();
        NBestDistribution<JSONObject> ans = new NBestDistribution<>();
        for (QualityDegree qualityDegree : relationDistribution.keySet()){
            ans.put(SemanticsModel.parseJSON("{}"));
        }

    }
}

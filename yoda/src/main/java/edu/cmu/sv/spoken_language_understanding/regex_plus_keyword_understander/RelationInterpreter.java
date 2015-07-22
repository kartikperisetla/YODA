package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology.Noun;
import edu.cmu.sv.domain.ontology.QualityDegree;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Utils;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 7/21/15.
 *
 * used to determine semantics for phrases such as "x's y", "the y (of/for/belonging to/which belongs to) x"
 * and sentences like "does x have any ys?", "what y's does x have?"
 * In these cases, the NP generated should have Y-inverse as the role connecting the referrent to x
 *
 */
public class RelationInterpreter {
    Map<Noun, Set<String>> nounStringMap = new HashMap<>();
    Map<QualityDegree, Set<String>> qualityDegreeStringMap = new HashMap<>();
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
//                e.printStackTrace();
            }
        }

        for (Noun noun : Ontology.nouns){
            Set<String> asNoun = new HashSet<>();
            try {
                asNoun.addAll(yodaEnvironment.lex.getPOSForClass(noun, Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, true));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            try {
                asNoun.addAll(yodaEnvironment.lex.getPOSForClass(noun, Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, true));
            } catch (Lexicon.NoLexiconEntryException e) {
            }
            if (asNoun.size() > 0)
                nounStringMap.put(noun, asNoun);
        }
    }

    NBestDistribution<JSONObject> interpret(List<String> relationTokens, List<String> npTokens){
        //// determine constraints set by NP
        Pair<JSONObject, Double> npInterpretation =
                ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(npTokens, yodaEnvironment);
        Noun domainNoun = Ontology.nounNameMap.get(npInterpretation.getLeft().get("class"));

        NBestDistribution<Pair<QualityDegree, Noun>> relationDistribution = new NBestDistribution<>();

        //// determine relation distribution based on lexicon and semantic constraints
        // get relation from quality degree lexicon
        for (QualityDegree cls : qualityDegreeStringMap.keySet()) {
            Noun qualityDomainConstraint = cls.getQuality().firstArgumentClassConstraint;
            double constraintMatch = Ontology.semanticConstraintMatch(domainNoun, qualityDomainConstraint);
            if (constraintMatch < .0001)
                continue;
            Double coverage = Utils.stringSetBestCoverage(String.join(" ", relationTokens), qualityDegreeStringMap.get(cls));
            if (coverage > 0) {
                relationDistribution.put(new ImmutablePair<>(cls, cls.getQuality().secondArgumentClassConstraint),
                        Double.max(relationDistribution.get(new ImmutablePair<>(cls, cls.getQuality().secondArgumentClassConstraint)),
                                coverage*constraintMatch));
            }
        }

        // get relation from noun / any quality degree which can have that role as its second element
        for (Noun cls : nounStringMap.keySet()){
            Double coverage = Utils.stringSetBestCoverage(String.join(" ", relationTokens), nounStringMap.get(cls));
            for (QualityDegree qualityDegree : Ontology.qualityDegrees){
                if (qualityDegree.getQuality().secondArgumentClassConstraint==null)
                    continue;
                Noun qualityDomainConstraint = qualityDegree.quality.firstArgumentClassConstraint;
                double constraintMatch = Ontology.semanticConstraintMatch(domainNoun, qualityDomainConstraint);
                if (constraintMatch < .0001)
                    continue;
                if (coverage > 0) {
                    relationDistribution.put(new ImmutablePair<>(qualityDegree, cls),
                            Double.max(relationDistribution.get(new ImmutablePair<>(qualityDegree, cls)),coverage));
                }
            }
        }

        relationDistribution.normalize();
        NBestDistribution<JSONObject> ans = new NBestDistribution<>();
        for (Pair<QualityDegree, Noun> relationInfo : relationDistribution.keySet()){
            ans.put(SemanticsModel.parseJSON("{\"class\":\""+ relationInfo.getRight().name+"\"," +
                    "\"InverseHas"+relationInfo.getLeft().getQuality().name+"\":{\"class\":\""+relationInfo.getLeft().name+"\"," +
                    "\""+YodaSkeletonOntologyRegistry.inRelationTo.name+"\":"+npInterpretation.getLeft()+"}}"),
                    relationDistribution.get(relationInfo));
        }
        return ans;
    }
}

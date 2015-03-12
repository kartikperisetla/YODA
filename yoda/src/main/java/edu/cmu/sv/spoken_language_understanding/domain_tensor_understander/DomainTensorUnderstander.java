package edu.cmu.sv.spoken_language_understanding.domain_tensor_understander;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.spoken_language_understanding.SpokenLanguageUnderstander;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 3/11/15.
 */
public class DomainTensorUnderstander implements SpokenLanguageUnderstander{
    YodaEnvironment yodaEnvironment;

    public DomainTensorUnderstander(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    @Override
    public void process1BestAsr(String asrResult) {
        // todo: apply lexicon transform
        // todo: run domain-general model
        // todo: apply domain transform
    }

    @Override
    public void processNBestAsr(StringDistribution asrNBestResult) {

    }

    /*
    * Transform a token into a k-dimensional domain-agnostic
    * */
    public List<Double> lexiconTransform(String token){
        List<Double> ans = new LinkedList<>();
        for (int i = 0; i < Lexicon.LexicalEntry.PART_OF_SPEECH.values().length; i++) {
            double val = 0.0;
            for (Class<? extends Thing> thingCls : Ontology.thingNameMap.values()){
                try {
                    if (yodaEnvironment.lex.getPOSForClass(thingCls,
                            Lexicon.LexicalEntry.PART_OF_SPEECH.values()[i],
                            Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true).contains(token)){
                        val += 1.0;
                    }
                } catch (Lexicon.NoLexiconEntryException e) {}
            }
            ans.add(val);
        }
        return ans;
    }

    ///// The following transforms are for tagging a single word with a role

//    public Class<? extends Role> roleTransform(List<Double> outputVector, String token){
//
//    }
//
//    public List<Double> inverseRTransform(Class<? extends Role> roleClass, String token){
//
//    }


    ///// The following transforms are for classifying a role with a corresponding concept


    public Pair<Class<? extends Role>, Class<? extends Thing>> roleClassTransform(List<Double> outputVector, List<String> tokenWindow){
        Map<Class<? extends Thing>, Double> conceptScores = new HashMap<>();
        Map<Class< ? extends Role>, Double> roleScores = new HashMap<>();

        for (Class<? extends Thing> thingCls : Ontology.thingNameMap.values()){
            conceptScores.put(thingCls, 0.0);
        }
        for (Class<? extends Role> roleCls : Ontology.roleNameMap.values()){
            roleScores.put(roleCls, 0.0);
        }

        // weight concept selection
        for (int i = 0; i < tokenWindow.size(); i++) {
            String token = tokenWindow.get(i);
            for (int j = 0; j < Lexicon.LexicalEntry.PART_OF_SPEECH.values().length; j++) {
                for (Class<? extends Thing> thingCls : Ontology.thingNameMap.values()) {
                    try {
                        if (yodaEnvironment.lex.getPOSForClass(thingCls,
                                Lexicon.LexicalEntry.PART_OF_SPEECH.values()[j],
                                Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true).contains(token)) {
                            conceptScores.put(thingCls, conceptScores.get(thingCls) +
                                    outputVector.get(i*Lexicon.LexicalEntry.PART_OF_SPEECH.values().length +j));
                        }
                    } catch (Lexicon.NoLexiconEntryException e) {
                    }
                }
            }
        }

        // weight role selection
        for (int i = 0; i < tokenWindow.size(); i++) {
            String token = tokenWindow.get(i);
            for (int j = 0; j < Lexicon.LexicalEntry.PART_OF_SPEECH.values().length; j++) {
                for (Class<? extends Role> roleCls : Ontology.roleNameMap.values()) {
                    try {
                        if (yodaEnvironment.lex.getPOSForClass(roleCls,
                                Lexicon.LexicalEntry.PART_OF_SPEECH.values()[j],
                                Grammar.EXHAUSTIVE_GENERATION_PREFERENCES, true).contains(token)) {
                            roleScores.put(roleCls, roleScores.get(roleCls) +
                                    outputVector.get(tokenWindow.size() * Lexicon.LexicalEntry.PART_OF_SPEECH.values().length
                                            + i * Lexicon.LexicalEntry.PART_OF_SPEECH.values().length + j));
                        }
                    } catch (Lexicon.NoLexiconEntryException e) {
                    }
                }
            }
        }

        // combine marginal concept and role scores to form joint scores when the pairs make sense given the ontology
        Map<Pair<Class<? extends Role>, Class<? extends Thing>>, Double> pairScores = new HashMap<>();
        for (Class<? extends Role> roleClass : roleScores.keySet()){
            for (Class<? extends Thing> conceptClass : conceptScores.keySet()){
                if (Ontology.inRange(roleClass, conceptClass)){
                    pairScores.put(new ImmutablePair<>(roleClass, conceptClass),
                            roleScores.get(roleClass) * conceptScores.get(conceptClass));
                }
            }
        }

        return HypothesisSetManagement.keepNBestBeam(pairScores, 1).get(0).getKey();
    }

    public List<Double> inverseRCTransform(Class<? extends Role> roleClass, Class<? extends Thing> conceptClass, List<String> tokenWindow){
        List<Double> ans = new LinkedList<>();

        // generate an output vector which describes how to get the desired role / concept pair from the given token window
        // return a number describing how effective the output vector is at generating the pair

        // for each POS & token, see if that POS corresponds to either role or the concept, and accumulate points in the target vector accordingly
        return ans;
    }



}

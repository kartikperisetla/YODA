package edu.cmu.sv.utils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/4/14.
 *
 * Functions for managing sets of hypotheses.
 *
 */
public class HypothesisSetManagement {

    public static <T> List<Pair<T, Double>> keepNBestBeam(Map<T, Double> asMap, int beamSize){
        Set<Pair<T, Double>> ans = asMap.keySet().stream().
                map(key -> new ImmutablePair<>(key, asMap.get(key))).
                collect(Collectors.toSet());
        return keepNBestBeam(ans, beamSize);
    }

    public static <T> List<Pair<T, Double>> keepNBestBeam(Set<Pair<T, Double>> fullSet, int beamSize){
        List<Pair<T, Double>> ans = fullSet.stream().
                sorted(Comparator.comparing((Function<Pair<T, Double>, Double>) Pair::getValue).reversed()).
                collect(Collectors.toList());

        if (ans.size()> beamSize){
            ans = ans.subList(0,beamSize);
        }
        return ans;
    }


    public static <T> List<Pair<T, Double>> keepRatioBeam(Map<T, Double> asMap, double ratio, int beamSize){
        Set<Pair<T, Double>> ans = asMap.keySet().stream().
                map(key -> new ImmutablePair<>(key, asMap.get(key))).
                collect(Collectors.toSet());
        return keepRatioBeam(ans, ratio, beamSize);
    }

    public static <T> List<Pair<T, Double>> keepRatioBeam(Set<Pair<T, Double>> fullSet, double ratio, int maxBeamSize){
        assert ratio <= 1;
        double maxWeight = fullSet.stream().map(Pair::getRight).max(Double::compare).get();

        List<Pair<T, Double>> ans = fullSet.stream().
                sorted(Comparator.comparing((Function<Pair<T, Double>, Double>) Pair::getValue).reversed()).
                filter(x -> x.getRight() >= ratio * maxWeight).
                collect(Collectors.toList());

        if (ans.size()> maxBeamSize){
            ans = ans.subList(0,maxBeamSize);
        }
        return ans;
    }

    public static StringDistribution keepRatioDistribution(StringDistribution fullSet, double ratio, int maxBeamSize){
        List<Pair<String, Double>> nBest = keepRatioBeam(fullSet.getInternalDistribution(), ratio, maxBeamSize);
        StringDistribution ans = new StringDistribution();
        nBest.forEach(x -> ans.put(x.getLeft(), x.getRight()));
        return ans;
    }

    public static StringDistribution keepNBestDistribution(StringDistribution fullSet, int beamSize){
        List<Pair<String, Double>> nBest = keepNBestBeam(fullSet.getInternalDistribution(), beamSize);
        StringDistribution ans = new StringDistribution();
        nBest.forEach(x -> ans.put(x.getLeft(), x.getRight()));
        return ans;
    }

    public static Pair<StringDistribution, Map<String, Map<String, String>>>
    getJointFromMarginals(Map<String, StringDistribution> marginals, int beamSize){
        Map<String, Set<String>> combinationsInput = new HashMap<>();
        marginals.entrySet().stream().forEach(x -> combinationsInput.put(x.getKey(), new HashSet<>(x.getValue().keySet())));
        StringDistribution jointDistribution = new StringDistribution();
        Map<String, Map<String, String>> jointAssignments = new HashMap<>();
        int i=0;
        for (Map<String, String> jointAssignment : Combination.possibleBindings(combinationsInput)){
            Double probability = 1.0;
            String assignmentID = "assignmentID_"+i;
            for (String key : jointAssignment.keySet()){
                probability*=marginals.get(key).get(jointAssignment.get(key));
            }
            jointDistribution.put(assignmentID, probability);
            jointAssignments.put(assignmentID, jointAssignment);
            i++;
        }

        Map<String, Map<String, String>> beamOfAssignments = new HashMap<>();
        jointDistribution = keepNBestDistribution(jointDistribution, beamSize);
        final StringDistribution finalJointDistribution = jointDistribution;
        jointAssignments.entrySet().stream().filter(x -> finalJointDistribution.containsKey(x.getKey())).
                forEach(x -> beamOfAssignments.put(x.getKey(), x.getValue()));
        return new ImmutablePair<>(jointDistribution, beamOfAssignments);
    }
}

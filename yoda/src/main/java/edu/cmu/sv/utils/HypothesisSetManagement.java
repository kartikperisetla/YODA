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

    public static <T> Map<T, Double> putOrIncrement(Map<T, Double> destination, T obj, Double amount){
        if (destination.containsKey(obj)){
            destination.put(obj, destination.get(obj) + amount);
        } else {
            destination.put(obj, amount);
        }
        return destination;
    }

}

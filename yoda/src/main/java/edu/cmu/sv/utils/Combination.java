package edu.cmu.sv.utils;

import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/2/14.
 */
public class Combination {

    static RandomData randomData = new RandomDataImpl();

    public static <T> Set<T> randomSubset(Collection<T> items, int k){
        if (items.size() > k){
            return Arrays.asList(NaturalLanguageGenerator.randomData.nextSample(
                    items, k)).stream().
                    map(x -> (T)x).
                    collect(Collectors.toSet());
        }
        else
            return new HashSet<>(items);
    }

    public static <T> Set<List<T>> combinations(Set<T> items){
        Set<List<T>> ans = new HashSet<>();
        if (items.size()==1) {
            ans.add(new LinkedList<T>(items));
            return ans;
        } else if (items.size()==0){
            return ans;
        }
        T item = new LinkedList<>(items).get(0);
        items.remove(item);
        for (List<T> subList : combinations(items)){
            subList.add(item);
            ans.add(subList);
        }
        return ans;
    }

    public static <S, T> Set<Map<S, T>> possibleBindings(Map<S, Set<T>> keysAndValues){
        Set<Map<S, T>> ans = new HashSet<>();
        if (keysAndValues.size()==0){
            ans.add(new HashMap<S, T>());
            return ans;
        }
        S key = new LinkedList<>(keysAndValues.keySet()).get(0);
        Set<T> values = keysAndValues.get(key);
        keysAndValues.remove(key);
        for (Map<S, T> partialBinding : possibleBindings(keysAndValues)){
            for (T value : values){
                Map<S, T> extendedBinding = new HashMap<>(partialBinding);
                extendedBinding.put(key, value);
                ans.add(extendedBinding);
            }
        }
        return ans;
    }


}

package edu.cmu.sv.utils;

import java.util.*;

/**
 * Created by David Cohen on 9/2/14.
 */
public class Combination {

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

    public static <T> Set<Map<String, T>> possibleBindings(Map<String, Set<T>> keysAndValues){
        Set<Map<String, T>> ans = new HashSet<>();
        if (keysAndValues.size()==0){
            return ans;
        }
        String key = new LinkedList<>(keysAndValues.keySet()).get(0);
        Set<T> values = keysAndValues.get(key);
        keysAndValues.remove(key);
        for (Map<String, T> partialBinding : possibleBindings(keysAndValues)){
            for (T value : values){
                Map<String, T> extendedBinding = new HashMap<>(partialBinding);
                extendedBinding.put(key, value);
                ans.add(extendedBinding);
            }
        }
        return ans;
    }
}

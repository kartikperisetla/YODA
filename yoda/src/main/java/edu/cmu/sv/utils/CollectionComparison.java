package edu.cmu.sv.utils;

import java.util.Map;

/**
 * Created by David Cohen on 9/5/14.
 */
public class CollectionComparison {
    public static <T, S> boolean MapEquals(Map<T,S> a, Map<T,S> b){
        if (!a.keySet().equals(b.keySet()))
            return false;
        for (T key : a.keySet()){
            if (!a.get(key).equals(b.get(key)))
                return false;
        }
        return true;
    }
}

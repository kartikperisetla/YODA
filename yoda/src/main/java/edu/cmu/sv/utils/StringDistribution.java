package edu.cmu.sv.utils;

import java.util.Map;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Represent a probability distribution over filler Strings
 *
 */
public class StringDistribution{
    private Map<String, Double> internalDistribution;

    public boolean containsKey(String key){
        return internalDistribution.containsKey(key);
    }

    public Double get(String key){
        return internalDistribution.get(key);
    }

    public void normalize(){
        Double total = internalDistribution.values().stream().reduce(0.0, (x, y) -> x + y);
        if (total!=1.0) {
            for (String key : internalDistribution.keySet()) {
                internalDistribution.put(key, internalDistribution.get(key) / total);
            }
        }
    }

    /*
    * If the key is already in the distribution, add value to it
    * Otherwise, create the key and set it to value
    * */
    public void extend(String key, Double value){
        if (internalDistribution.containsKey(key))
            internalDistribution.put(key, internalDistribution.get(key)+value);
        else
            internalDistribution.put(key,value);
    }

}

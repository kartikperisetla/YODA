package edu.cmu.sv.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Represent a probability distribution over filler Strings
 *
 */
public class StringDistribution{
    private Map<String, Double> internalDistribution;

    public String getTopHypothesis(){
        String ans = null;
        Double maxProb = -1.0;
        for (String key : internalDistribution.keySet()){
            if (internalDistribution.get(key) > maxProb){
                maxProb = internalDistribution.get(key);
                ans = key;
            }
        }
        return ans;
    }

    public boolean containsKey(String key){
        return internalDistribution.containsKey(key);
    }

    public Double get(String key){
        if (internalDistribution.containsKey(key))
            return internalDistribution.get(key);
        return 0.0;
    }

    public void normalize(){
        Double total = internalDistribution.values().stream().reduce(0.0, (x, y) -> x + y);
        if (total!=1.0) {
            for (String key : internalDistribution.keySet()) {
                internalDistribution.put(key, internalDistribution.get(key) / total);
            }
        }
    }

    public Collection<String> keySet() {return internalDistribution.keySet();}
    public Collection<Double> values(){
        return internalDistribution.values();
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

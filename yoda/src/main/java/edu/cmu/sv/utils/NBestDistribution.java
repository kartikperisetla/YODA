package edu.cmu.sv.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 3/27/15.
 */
public class NBestDistribution<T> {
    public Map<T, Double> internalDistribution;

    public NBestDistribution() {
        internalDistribution = new HashMap<>();
    }

    public void remove(T key){
        internalDistribution.remove(key);
    }

    public T getTopHypothesis(){
        T ans = null;
        Double maxProb = -1.0;
        for (T key : internalDistribution.keySet()){
            if (internalDistribution.get(key) > maxProb){
                maxProb = internalDistribution.get(key);
                ans = key;
            }
        }
        return ans;
    }

    public boolean containsKey(T key){
        return internalDistribution.containsKey(key);
    }

    public Double get(T key){
        if (internalDistribution.containsKey(key))
            return internalDistribution.get(key);
        return 0.0;
    }

    public void normalize(){
        Double total = internalDistribution.values().stream().reduce(0.0, (x, y) -> x + y);
        if (total!=1.0) {
            for (T key : internalDistribution.keySet()) {
                internalDistribution.put(key, internalDistribution.get(key) / total);
            }
        }
    }

    public double information(){
        double ans = 0.0;
        for (T key : internalDistribution.keySet()){
            if (internalDistribution.get(key)<=0.00001)
                continue;
            ans -= internalDistribution.get(key)*Math.log(internalDistribution.get(key))/Math.log(2.0);
        }
        return ans;
    }

    public Collection<T> keySet() {return internalDistribution.keySet();}
    public Collection<Double> values(){
        return internalDistribution.values();
    }

    /*
    * If the key is already in the distribution, add value to it
    * Otherwise, create the key and set it to value
    * */
    public void put(T key, Double value){
        internalDistribution.put(key,value);
    }

    public Map<T, Double> getInternalDistribution() {
        return internalDistribution;
    }

    @Override
    public String toString() {
        return "NBestDistribution{" +
                "internalDistribution=" + internalDistribution +
                '}';
    }


}

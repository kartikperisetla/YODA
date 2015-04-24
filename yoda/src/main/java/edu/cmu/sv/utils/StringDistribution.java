package edu.cmu.sv.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Represent a probability distribution over filler Strings
 *
 */
public class StringDistribution{
    private Map<String, Double> internalDistribution;

    public StringDistribution() {
        internalDistribution = new HashMap<>();
    }

    public StringDistribution deepCopy(){
        StringDistribution ans = new StringDistribution();
        for (String key : internalDistribution.keySet())
            ans.internalDistribution.put(key, internalDistribution.get(key));
        return ans;
    }

    /*
    * Return the keys ordered from highest probability to lowest
    * */
    public List<String> sortedHypotheses(){
        return internalDistribution.keySet().stream().
                sorted(Comparator.comparing((Function<String, Double>)
                        internalDistribution::get).reversed()).
                        collect(Collectors.toList());
    }

    public void remove(String key){
       internalDistribution.remove(key);
    }

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


    public void filterZeroEntries(){
        internalDistribution.keySet().stream().
                filter(key -> internalDistribution.get(key) <= 0).
                forEach(internalDistribution::remove);
    }

    public void normalize(){
        filterZeroEntries();
        if (internalDistribution.size()==0)
            return;

        Double total = internalDistribution.values().stream().reduce(0.0, (x, y) -> x + y);
        if (total<=0) {
            throw new Error("not a valid set of values to be normalized");
        }
        if (total!=1.0) {
            for (String key : internalDistribution.keySet()) {
                internalDistribution.put(key, internalDistribution.get(key) / total);
            }
        }
    }

    public double information(){
        double ans = 0.0;
        for (String key : internalDistribution.keySet()){
            if (internalDistribution.get(key)<=0.00001)
                continue;
            ans -= internalDistribution.get(key)*Math.log(internalDistribution.get(key))/Math.log(2.0);
        }
        return ans;
    }

    public Collection<String> keySet() {return internalDistribution.keySet();}
    public Collection<Double> values(){
        return internalDistribution.values();
    }

    /*
    * If the key is already in the distribution, add value to it
    * Otherwise, create the key and set it to value
    * */
    public void put(String key, Double value){
        internalDistribution.put(key,value);
    }

    public void increment(String key, Double value){
        internalDistribution.put(key, internalDistribution.get(key) + value);
    }

    public Map<String, Double> getInternalDistribution() {
        return internalDistribution;
    }

    @Override
    public String toString() {
        return "StringDistribution{" +
                "internalDistribution=" + internalDistribution +
                '}';
    }
}

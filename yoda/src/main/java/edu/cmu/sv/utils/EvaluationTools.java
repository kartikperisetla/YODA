package edu.cmu.sv.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 9/12/14.
 */
public class EvaluationTools {

    public static class ConfusionCounter<T>{
        Map<T, Map<T, Integer>> matrixContent;

        public ConfusionCounter(List<Pair<T, T>> confusionList){
            matrixContent = new HashMap<>();
            for (Pair<T,T> confusion : confusionList){
                if (confusion==null)
                    continue;
                if (!matrixContent.containsKey(confusion.getKey()))
                    matrixContent.put(confusion.getKey(), new HashMap<>());
                if (!matrixContent.get(confusion.getKey()).containsKey(confusion.getValue()))
                    matrixContent.get(confusion.getKey()).put(confusion.getValue(),0);
                matrixContent.get(confusion.getKey()).put(
                        confusion.getValue(),
                        matrixContent.get(confusion.getKey()).get(confusion.getValue()) + 1);
            }
        }

        @Override
        public String toString() {
            String ans = "";
            for (T key : matrixContent.keySet()){
                ans += key + " -> " + matrixContent.get(key) + "\n";
            }
            return ans;
        }
    }

}

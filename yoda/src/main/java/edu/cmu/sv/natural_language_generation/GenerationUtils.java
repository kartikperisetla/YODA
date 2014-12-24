package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Combination;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by David Cohen on 10/30/14.
 */
public class GenerationUtils {

    public static class NoLexiconEntryException extends Exception {};

    public static Set<String> getPOSForClass(Class<? extends Thing> cls, String partOfSpeech, YodaEnvironment yodaEnvironment) throws NoLexiconEntryException {
        Set<String> ans = new HashSet<>();
        if (Modifier.isAbstract(cls.getModifiers()))
            return ans;
        try {
            Thing tmp = cls.newInstance();
            for (LexicalEntry lexicalEntry : tmp.getLexicalEntries()){
                boolean posFound = false;
                for (Field field : lexicalEntry.getClass().getDeclaredFields()){
                    if (field.getName().equals(partOfSpeech)) {
                        ans.addAll((Collection) field.get(lexicalEntry));
                        posFound = true;
                        break;
                    }
                }
                if (!(posFound))
                    throw new NoLexiconEntryException();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (ans.size()<=yodaEnvironment.nlg.grammarPreferences.maxWordForms)
            return ans;
        return new HashSet<>(Arrays.asList(
                Arrays.copyOf(
                        NaturalLanguageGenerator.randomData.nextSample(ans, yodaEnvironment.nlg.grammarPreferences.maxWordForms),
                        yodaEnvironment.nlg.grammarPreferences.maxWordForms, String[].class)));
    }


    public static Set<String> getPOSForClassHierarchy(Class cls, String partOfSpeech, YodaEnvironment yodaEnvironment) throws NoLexiconEntryException {
        if (! (Thing.class.isAssignableFrom(cls)))
            throw new NoLexiconEntryException();
        try {
            Set<String> ans = getPOSForClass((Class<? extends Thing>)cls, partOfSpeech, yodaEnvironment);
            if (ans.size()==0){
                throw new NoLexiconEntryException();
            }
            return ans;
        } catch (NoLexiconEntryException e){
            return getPOSForClassHierarchy(cls.getSuperclass(), partOfSpeech, yodaEnvironment);
        }
    }



    /*
    * Return all combinations of strings and composed semantics objects,
    * maintaining the order given as input
    * */
    public static Map<String, JSONObject> simpleOrderedCombinations(
            List<Map<String, JSONObject>> chunks,
            Function<List<JSONObject>, JSONObject> compositionFunction,
            Map<String, Pair<Integer, Integer>> childNodeChunks,
            YodaEnvironment yodaEnvironment){
        Map<String, JSONObject> ans = new HashMap<>();

        Map<Integer, Set<Map.Entry<String, JSONObject>>> possibleBindingsInput = new HashMap<>();
        IntStream.range(0, chunks.size()).forEach(x -> possibleBindingsInput.put(x, chunks.get(x).entrySet()));

        for (Map<Integer, Map.Entry<String, JSONObject>> binding : Combination.possibleBindings(possibleBindingsInput)){
            String combinedString = "";
            List<String> subStrings = new LinkedList<>();
            List<JSONObject> subContents = new LinkedList<>();
            for (int i = 0; i < chunks.size(); i++) {
                if (!(binding.get(i).getKey().trim().equals(""))) {
                    if (i!=0)
                        combinedString += " ";
                    combinedString += binding.get(i).getKey().trim();
                }
                subStrings.add(binding.get(i).getKey());
                subContents.add(binding.get(i).getValue());
            }
            JSONObject combinedMeaning = compositionFunction.apply(subContents);
            for (String childRole : childNodeChunks.keySet()){
                addChunkIndices(combinedMeaning, subStrings, childNodeChunks.get(childRole), childRole);
            }

            ans.put(combinedString, combinedMeaning);
        }
        if (ans.size()<=yodaEnvironment.nlg.grammarPreferences.maxCombinations)
            return ans;

        Map<String, JSONObject> newAns = new HashMap<>();
        Arrays.asList(Arrays.copyOf(
                NaturalLanguageGenerator.randomData.nextSample(ans.entrySet(),
                        yodaEnvironment.nlg.grammarPreferences.maxCombinations),
                yodaEnvironment.nlg.grammarPreferences.maxCombinations, Map.Entry[].class)).
                stream().
                forEach(x -> newAns.put((String)x.getKey(), (JSONObject)x.getValue()));
        return newAns;
    }

    /*
    * A convenience function used for corpus generation
    * It sets up chunk indices in the child inside pathToChild at the appropriate indices
    * given the particular ordered list of chunks and the indices of the chunks contributing to that child
    *
    * The chunk start is the index of the start, the chunk end is the index at the end
    *
    * */
    public static void addChunkIndices(JSONObject composedContent,
                                             List<String> stringChunks,
                                             Pair<Integer, Integer> selectedChunks,
                                             String pathToChild){

//        System.out.println("GenerationUtils.addChunkIndices stringChunks:"+stringChunks + ", selectedChunks:"+selectedChunks);
//        System.out.println(composedContent);
//        System.out.println(pathToChild);
        Integer startingIndex = 0;
        for (int i = 0; i < selectedChunks.getKey(); i++) {
            if (!(stringChunks.get(i).trim().equals("")))
                startingIndex += stringChunks.get(i).split(" ").length;
        }
        Integer endingIndex = startingIndex - 1;
        for (int i = selectedChunks.getKey(); i <= selectedChunks.getValue(); i++) {
            if (!(stringChunks.get(i).trim().equals("")))
                endingIndex += stringChunks.get(i).split(" ").length;
        }
        JSONObject tmp = (JSONObject) new SemanticsModel(composedContent).newGetSlotPathFiller(pathToChild);
        tmp.put("chunk-start", startingIndex);
        tmp.put("chunk-end", endingIndex);
    }


}

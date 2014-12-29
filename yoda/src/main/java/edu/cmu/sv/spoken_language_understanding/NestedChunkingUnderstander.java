package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/27/14.
 *
 * Perform SLU via nested chunking / node classification steps
 *
 */
public class NestedChunkingUnderstander implements SpokenLanguageUnderstander{
    private static Logger logger = Logger.getLogger("yoda.spoken_language_understanding.NestedChunkingUnderstander");
    private static FileHandler fh;
    static {
        try {
            fh = new FileHandler("NestedChunkingUnderstander.log");
            fh.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.addHandler(fh);
    }


    public static class PartialUnderstandingState{
        public JSONObject structure;
        public Map<String, ChunkingProblem> pathChunkingProblemMap = new HashMap<>();
        public Set<String> remainingClassificationProblemNodes = new HashSet<>();

        public PartialUnderstandingState deepCopy(){
            PartialUnderstandingState ans = new PartialUnderstandingState();
            ans.structure = SemanticsModel.parseJSON(structure.toJSONString());
            pathChunkingProblemMap.keySet().stream().forEach(x -> ans.pathChunkingProblemMap.put(x, pathChunkingProblemMap.get(x)));
            return ans;
        }

        public Pair<Map<String, PartialUnderstandingState>, StringDistribution> extendChunk(String pathToChunkingProblem){
            Map<String, PartialUnderstandingState> updatedUnderstandingStates = new HashMap<>();
            StringDistribution understandingStateDistribution = new StringDistribution();

            ChunkingProblem currentChunkingProblem = pathChunkingProblemMap.get(pathToChunkingProblem);
            currentChunkingProblem.runChunker();

            int i=0;
            for (String chunkingResultKey : currentChunkingProblem.outputDistribution.keySet()){
                String updatedUnderstandingStateId = "understanding_state_"+i++;
                PartialUnderstandingState newUnderstandingState = this.deepCopy();
                for (ChunkingProblem childChunkingProblem : currentChunkingProblem.outputChildChunks.get(chunkingResultKey)) {
                    String totalPath = (pathToChunkingProblem.equals("")) ?
                            childChunkingProblem.inputContext :
                            pathToChunkingProblem + "." + childChunkingProblem.inputContext;
                    extendStructureWithPath(newUnderstandingState.structure, totalPath);
                    newUnderstandingState.pathChunkingProblemMap.put(totalPath, childChunkingProblem);
                }
                newUnderstandingState.pathChunkingProblemMap.remove(pathToChunkingProblem);
                updatedUnderstandingStates.put(updatedUnderstandingStateId, newUnderstandingState);
                understandingStateDistribution.put(updatedUnderstandingStateId, currentChunkingProblem.outputDistribution.get(chunkingResultKey));
            }


            return new ImmutablePair<>(updatedUnderstandingStates, understandingStateDistribution);
        }

        public Pair<Map<String, PartialUnderstandingState>, StringDistribution> classifyAtPath(String pathToClassificationProblem){
            Map<String, PartialUnderstandingState> updatedUnderstandingStates = new HashMap<>();
            StringDistribution understandingStateDistribution = new StringDistribution();

            NodeClassificationProblem currentClassificationProblem = new NodeClassificationProblem();
            currentClassificationProblem.extractFeatures(structure, pathToClassificationProblem);
            currentClassificationProblem.runClassifier();

            int i=0;
            for (String classificationResultKey : currentClassificationProblem.outputDistribution.keySet()){
                String updatedUnderstandingStateId = "understanding_state_"+i++;
                PartialUnderstandingState newUnderstandingState = this.deepCopy();
                for (String slotString : currentClassificationProblem.outputRolesAndFillers.get(classificationResultKey).keySet()) {
                    SemanticsModel tmpSemanticsModel = new SemanticsModel(newUnderstandingState.structure);
                    Object filler = currentClassificationProblem.outputRolesAndFillers.get(classificationResultKey).get(slotString);
                    if (filler instanceof JSONObject){
                        ((JSONObject)tmpSemanticsModel.newGetSlotPathFiller(pathToClassificationProblem)).
                                put(slotString, SemanticsModel.parseJSON(((JSONObject) filler).toJSONString()));
                    } else if (filler instanceof String) {
                        ((JSONObject)tmpSemanticsModel.newGetSlotPathFiller(pathToClassificationProblem)).
                                put(slotString, filler);
                    } else {
                        throw new Error("nonsense classification results");
                    }
                }
                newUnderstandingState.remainingClassificationProblemNodes.remove(pathToClassificationProblem);
                updatedUnderstandingStates.put(updatedUnderstandingStateId, newUnderstandingState);
                understandingStateDistribution.put(updatedUnderstandingStateId, currentClassificationProblem.outputDistribution.get(classificationResultKey));
            }
            return new ImmutablePair<>(updatedUnderstandingStates, understandingStateDistribution);
        }

        static void extendStructureWithPath(JSONObject inputObject, String path){
            SemanticsModel tmp = new SemanticsModel(inputObject);
            String[] roles = path.split("\\.");
            String fillerPath = "";
            String previousPath = fillerPath;
            for (int i = 0; i < roles.length; i++) {
                fillerPath += roles[i];
                if (tmp.newGetSlotPathFiller(fillerPath)==null){
                    ((JSONObject) tmp.newGetSlotPathFiller(previousPath)).put(roles[i], new JSONObject());
                }
                previousPath = fillerPath;
            }
        }

    }

    public static class NodeClassificationProblem{
        public static Set<NodeClassificationProblem> cache = new HashSet<>();
        public List<String> inputFeatures;
        Map<String, Map<String, Object>> outputRolesAndFillers;
        StringDistribution outputDistribution;

        public void extractFeatures(JSONObject inputStructure, String problemPath){

        }

        public void runClassifier(){
            Map<String, Map<String, Object>> ans = new HashMap<>();
            StringDistribution outputDistr = new StringDistribution();
            // extract features
            // check cache
            // call external classification program
            // read results
            // interpret as new roles / values for answer map
            // cache results
            outputRolesAndFillers = ans;
            outputDistribution = outputDistr;
        }

    }

    public static class ChunkingProblem{
        // this cache doesn't permit fast lookup, but we expect <100 chunking problems per utterance, so iterating through should be fine
        public static Set<ChunkingProblem> cache = new HashSet<>();

        public String inputString;
        public String inputContext;
        public Map<String, Set<ChunkingProblem>> outputChildChunks;
        public StringDistribution outputDistribution;


        public ChunkingProblem(String inputString, String inputContext){
            this.inputString = inputString;
            this.inputContext = inputContext;
        }

        // todo: implement
        public void runChunker(){
            // check cache
            for (ChunkingProblem cachedProblem : cache){}
            // tokenize
            // create features
            // call external chunking program
            // read results
            // interpret as new chunking problems
            // cache results
        }

        public void assembleJSON(){
            JSONObject root = new JSONObject();
            for (String chunkingHypothesisKey : outputDistribution.keySet()){

            }
        }

    }

    public Pair<Map<String, JSONObject>, StringDistribution> understand(String utterance){
        // recursively build structure while chunking
        StringDistribution partialStructureDistribution = new StringDistribution();
        Map<String, PartialUnderstandingState> partialStructures = new HashMap<>();
        PartialUnderstandingState root = new PartialUnderstandingState();
        root.structure = new JSONObject();
        root.pathChunkingProblemMap.put("", new ChunkingProblem(utterance, ""));
        partialStructureDistribution.put("initial", 1.0);
        partialStructures.put("initial", root);
        int partialHypothesisNameIndex=0;
        while (true){
            // select a partial structure with a remaining chunking problem
            String currentPartialStructureKey = null;
            for (String key : partialStructures.keySet()){
                if (partialStructures.get(key).pathChunkingProblemMap.size()>0) {
                    currentPartialStructureKey = key;
                    break;
                }
            }
            if (currentPartialStructureKey==null)
                break;
            double currentWeight = partialStructureDistribution.get(currentPartialStructureKey);
            PartialUnderstandingState currentUnderstandingState = partialStructures.get(currentPartialStructureKey);
            partialStructureDistribution.remove(currentPartialStructureKey);
            partialStructures.remove(currentPartialStructureKey);

            String childAbsolutePath = new LinkedList<>(currentUnderstandingState.pathChunkingProblemMap.keySet()).get(0);
            Pair<Map<String, PartialUnderstandingState>, StringDistribution> extensions = currentUnderstandingState.extendChunk(childAbsolutePath);
            for (String extendedPartialStateKey : extensions.getLeft().keySet()){
                String updatedUnderstandingStateId = "understanding_state_"+partialHypothesisNameIndex++;
                partialStructures.put(updatedUnderstandingStateId, extensions.getLeft().get(extendedPartialStateKey));
                partialStructureDistribution.put(updatedUnderstandingStateId, currentWeight * extensions.getRight().get(extendedPartialStateKey));
            }
        }

        // todo: prune

        // initialize remainingClassificationPaths
        partialStructures.values().stream().forEach(x -> x.remainingClassificationProblemNodes = new SemanticsModel(x.structure).getAllInternalNodePaths());

        // perform classification at every node
        while (true){
            // select a partial structure with a remaining classification problem
            String currentPartialStructureKey = null;
            for (String key : partialStructures.keySet()){
                if (partialStructures.get(key).remainingClassificationProblemNodes.size()>0) {
                    currentPartialStructureKey = key;
                    break;
                }
            }
            if (currentPartialStructureKey==null)
                break;
            double currentWeight = partialStructureDistribution.get(currentPartialStructureKey);
            PartialUnderstandingState currentUnderstandingState = partialStructures.get(currentPartialStructureKey);
            partialStructureDistribution.remove(currentPartialStructureKey);
            partialStructures.remove(currentPartialStructureKey);

            // perform classification problems bottom-up
            // do this by selecting the longest path string ... kinda dumb, but it works
            int maxPathStringLength = currentUnderstandingState.remainingClassificationProblemNodes.stream().
                    map(x -> x.length()).max(Integer::compare).orElseGet(() -> 0);
            String classificationProblemPath = currentUnderstandingState.remainingClassificationProblemNodes.stream().
                    filter(x -> x.length()==maxPathStringLength).collect(Collectors.toList()).get(0);
            Pair<Map<String, PartialUnderstandingState>, StringDistribution> extensions = currentUnderstandingState.classifyAtPath(classificationProblemPath);
            for (String extendedPartialStateKey : extensions.getLeft().keySet()){
                String updatedUnderstandingStateId = "understanding_state_"+partialHypothesisNameIndex++;
                partialStructures.put(updatedUnderstandingStateId, extensions.getLeft().get(extendedPartialStateKey));
                partialStructureDistribution.put(updatedUnderstandingStateId, currentWeight * extensions.getRight().get(extendedPartialStateKey));
            }
        }

        Map<String, JSONObject> outputStructures = new HashMap<>();
        partialStructures.entrySet().stream().forEach(x -> outputStructures.put(x.getKey(), x.getValue().structure));
        return new ImmutablePair<>(outputStructures, partialStructureDistribution);
    }



    //todo: implement
    @Override
    public void process1BestAsr(String asrResult) {
    }


    //todo: implement
    @Override
    public void processNBestAsr(StringDistribution asrNBestResult) {
        process1BestAsr(asrNBestResult.getTopHypothesis());
    }

    YodaEnvironment yodaEnvironment;
    public NestedChunkingUnderstander(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }
}

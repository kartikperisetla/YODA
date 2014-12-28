package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
        Map<String, JSONObject> outputStructures = new HashMap<>();
        StringDistribution outputStructureDistribution = new StringDistribution();


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

            String childRelativePath = new LinkedList<>(currentUnderstandingState.pathChunkingProblemMap.keySet()).get(0);
            Pair<Map<String, PartialUnderstandingState>, StringDistribution> extensions = currentUnderstandingState.extendChunk(childRelativePath);
            for (String extendedPartialStateKey : extensions.getLeft().keySet()){
                String updatedUnderstandingStateId = "understanding_state_"+partialHypothesisNameIndex++;
                partialStructures.put(updatedUnderstandingStateId, extensions.getLeft().get(extendedPartialStateKey));
                partialStructureDistribution.put(updatedUnderstandingStateId, currentWeight * extensions.getRight().get(extendedPartialStateKey));
            }
        }

        //todo: implement
        // perform classification at every node

        return new ImmutablePair<>(outputStructures, outputStructureDistribution);
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

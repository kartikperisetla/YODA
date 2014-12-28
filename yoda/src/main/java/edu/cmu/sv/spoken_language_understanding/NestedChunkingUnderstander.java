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


    public static class ChunkingProblem{
        public String inputString;
        public List<String> inputContextFeatures;
        public List<String> inputTokens;
        public Map<String, List<String>> outputLabels;
        public StringDistribution outputDistribution;

        public ChunkingProblem(String inputString, List<String> inputContextFeatures){
            this.inputString = inputString;
            this.inputContextFeatures = inputContextFeatures;
        }

        // todo: implement
        public void runChunker(){
            // check cache
            // tokenize
            // create features
            // call external chunking program
            // read results
            // cache results
        }

        // todo: implement
        public Set<ChunkingProblem> nestedChunkingProblems(){
            Set<ChunkingProblem> ans = new HashSet<>();
            // read through output labels and select chunks
            // create and collect new chunking problems with this.inputContextFeatures appended with the new chunking info
            // verify that none of the nested chunks are the same as this chunk?
            // verify that the nested chunks are either smaller, or a HasName?
            return ans;
        }

    }

    public Pair<Map<String, JSONObject>, StringDistribution> Understand(String utterance){
        Map<String, JSONObject> outputStructures = new HashMap<>();
        StringDistribution outputStructureDistribution = new StringDistribution();

        JSONObject root = SemanticsModel.parseJSON("{}");

        // perform nested chunking
        Set<ChunkingProblem> activeChunks = new HashSet<>();
        activeChunks.add(new ChunkingProblem(utterance, Arrays.asList("")));
        while (!activeChunks.isEmpty()){
            ChunkingProblem currentProblem = new LinkedList<>(activeChunks).get(0);
            activeChunks.remove(currentProblem);
            currentProblem.runChunker();
            activeChunks.addAll(currentProblem.nestedChunkingProblems());
        }

        //todo: implement
        // assemble chunks into JSON Objects with attached inputString and inputContextFeatures

        //todo: implement
        // perform bottom-up classification

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

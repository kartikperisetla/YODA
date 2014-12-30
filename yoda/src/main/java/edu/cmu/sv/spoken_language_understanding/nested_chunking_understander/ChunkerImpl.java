package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by David Cohen on 12/29/14.
 */
public class ChunkerImpl implements Chunker {
    static ChunkingFeatureExtractor featureExtractor;

    public ChunkerImpl() {
        featureExtractor = new ChunkingFeatureExtractorImpl();
    }

    @Override
    public void chunk(ChunkingProblem chunkingProblem) {
        Pair<List<Double>, List<List<String>>> features = featureExtractor.generateFeatures(chunkingProblem);

        //todo: pack features as raw input for theano model

        //todo: run model given input

        //todo: read generated result

        //todo:
    }
}

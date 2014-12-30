package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by David Cohen on 12/29/14.
 */
public interface ChunkingFeatureExtractor {
    public Pair<List<Double>, List<List<String>>> generateFeatures(ChunkingProblem chunkingProblem);
}

package edu.cmu.sv.spoken_language_understanding;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 12/29/14.
 */
public interface ChunkingFeatureExtractor {
    public Pair<List<Double>, List<List<String>>> generateFeatures(String inputString, JSONObject surroundingStructure, String contextPathInStructure);
}

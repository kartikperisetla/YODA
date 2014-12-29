package edu.cmu.sv.spoken_language_understanding;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 12/29/14.
 */
public interface ClassificationFeatureExtractor {
    public List<Double> generateFeatures(String inputString, JSONObject surroundingStructure, String contextPathInStructure);
}

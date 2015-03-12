package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.semantics.SemanticsModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 3/11/15.
 *
 * This class is meant to include a dataset of SLU examples (string, Semantics model pairs)
 * model training and evaluation scripts will make heavy use of this class
 *
 * There is no alignment between the words and parts of the result.
 * It is assumed that any learning algorithm will either figure out the alignment, or not require it.
 *
 */
public class SLUDataset {
    Map<String, SemanticsModel> dataSet = new HashMap<>();
}

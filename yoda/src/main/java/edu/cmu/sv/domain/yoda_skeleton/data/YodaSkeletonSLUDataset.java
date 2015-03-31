package edu.cmu.sv.domain.yoda_skeleton.data;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SLUDataset;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 3/11/15.
 */
public class YodaSkeletonSLUDataset extends SLUDataset {
    public YodaSkeletonSLUDataset() {
        add(new ImmutablePair<>("2:35",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasTenMinute\":3, \"HasSingleMinute\":5}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("two p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
    }
}

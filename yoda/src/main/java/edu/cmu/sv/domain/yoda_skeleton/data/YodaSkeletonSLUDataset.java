package edu.cmu.sv.domain.yoda_skeleton.data;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SLUDataset;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 3/11/15.
 */
public class YodaSkeletonSLUDataset extends SLUDataset {
    public YodaSkeletonSLUDataset() {
        add(new ImmutablePair<>("yeah sure",
                new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
        add(new ImmutablePair<>("no that's not what i said",
                new SemanticsModel("{\"dialogAct\": \"Reject\"}")));
        add(new ImmutablePair<>("2:35",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasTenMinute\":3, \"HasSingleMinute\":5}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("two p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("three pm",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 3, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("2a.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"AM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("3 thirty p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 3, \"HasTenMinute\": 3, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("3 thirty 3 p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 3, \"HasTenMinute\": 3, \"HasSingleMinute\": 3, \"HasAmPm\": \"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("eight nineteen p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 8, \"HasTenMinute\": 1, \"HasSingleMinute\": 9, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("eleven pm",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 11, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("twelve p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 12, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("two p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("two p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("two p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
        add(new ImmutablePair<>("two p.m.",
                new SemanticsModel("{\"topic\": {\"class\": \"Time\", \"HasHour\": 2, \"HasAmPm\":\"PM\"}, \"dialogAct\": \"Fragment\"}")));
    }
}

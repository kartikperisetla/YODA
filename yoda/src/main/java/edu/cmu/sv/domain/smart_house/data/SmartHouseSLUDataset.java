package edu.cmu.sv.domain.smart_house.data;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SLUDataset;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 3/11/15.
 */
public class SmartHouseSLUDataset extends SLUDataset {
    public SmartHouseSLUDataset() {
        add(new ImmutablePair<>("is the air conditioner on",
                new SemanticsModel("{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":{\"class\":\"AirConditioner\"},\"Patient\":{\"HasPowerState\":{\"class\":\"On\"},\"class\":\"UnknownThingWithRoles\"},\"class\":\"HasProperty\"}}")));
        add(new ImmutablePair<>("turn on the air conditioner",
                new SemanticsModel("{\"dialogAct\":\"Command\",\"verb\":{\"Component\":{\"class\":\"AirConditioner\"},\"class\":\"TurnOnAppliance\"}}")));
        add(new ImmutablePair<>("turn it on",
                new SemanticsModel("{\"dialogAct\":\"Command\",\"verb\":{\"Component\":{\"refType\":\"pronoun\",\"class\":\"Noun\"},\"class\":\"TurnOnAppliance\"}}")));
        add(new ImmutablePair<>("switch it on",
                new SemanticsModel("{\"dialogAct\":\"Command\",\"verb\":{\"Component\":{\"refType\":\"pronoun\",\"class\":\"Noun\"},\"class\":\"TurnOnAppliance\"}}")));
        add(new ImmutablePair<>("is the security system on",
                new SemanticsModel("{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":{\"class\":\"SecuritySystem\"},\"Patient\":{\"HasPowerState\":{\"class\":\"On\"},\"class\":\"UnknownThingWithRoles\"},\"class\":\"HasProperty\"}}")));
    }
}

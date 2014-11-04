package edu.cmu.sv.ontology.quality;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.modifier.Modifier;
import edu.cmu.sv.ontology.role.Role;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 */
public abstract class TransientQuality {
    /*
 * Return the query that calculates the transient quality at a particular time
 * (based on some non-transient information)
 * */
    public abstract java.util.function.Function<List<String>, String> getQualityCalculatorSPARQLQuery();

}

package edu.cmu.sv.ontology.quality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 *
 * Create instances of Quality classes, to be added to the database
 *
 */
public class QualityRegistry {
    public static Map<String, Quality> qualityInstances = new HashMap<>();
    static Expensiveness expensive = new Expensiveness();
    static Expensiveness cheap = new Expensiveness();
    static {
        expensive.lexicalEntry.adjectives.add("expensive");
        cheap.lexicalEntry.adjectives.add("cheap");

        qualityInstances.put("expensive", expensive);
        qualityInstances.put("cheap", cheap);
    }

}

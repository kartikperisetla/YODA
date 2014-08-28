package edu.cmu.sv.database;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cohend on 6/29/14.
 */
public class ReferenceDatabase {

    private Set<String> classes;

    // these variables define the supported mapping operations from the domain ontology to the reference database
    // predicateObjectToPredicateObject map takes precedence over URItoURImap.
    // we don't allow rdf:type to be translated
    private Map<ImmutablePair<String, String>, ImmutablePair<String, String>> predicateObjectToPredicateObjectMap;
    private Map<String, String> URItoURIMap;
    private Database db;

    

    private Database.Operation translate(Database.Operation operation){
        Database.Operation newOperation = new Database.Operation(operation.getTransactionType(), operation.getVariables(), operation.getClassConstraints(), operation.getTriples());

        // translate class constraints
        Map<String, String> newClassConstraints = new HashMap<>();
        for (String key : operation.getClassConstraints().keySet()){
            if (URItoURIMap.containsKey(operation.getClassConstraints().get(key))){
                newClassConstraints.put(key, URItoURIMap.get(operation.getClassConstraints().get(key)));
            } else {
                newClassConstraints.put(key, operation.getClassConstraints().get(key));
            }
        }
        newOperation.setClassConstraints(newClassConstraints);

        // translate triples
        Set<ImmutableTriple<String, String, String>> newTriples = new HashSet<>();
        for (ImmutableTriple<String, String, String> oldTriple : operation.getTriples()){
            String newSubj = oldTriple.getLeft();
            String newPredicate = oldTriple.getMiddle();
            String newObj = oldTriple.getRight();
            // pred / object translations first
            if (predicateObjectToPredicateObjectMap.containsKey(new ImmutablePair<>(newPredicate, newObj))){
                ImmutablePair<String, String> dummy = predicateObjectToPredicateObjectMap.get(new ImmutablePair<>(newPredicate, newObj));
                newPredicate = dummy.getLeft();
                newObj = dummy.getRight();
            }
            // URI translations
            if (URItoURIMap.containsKey(newSubj)){
                newSubj = URItoURIMap.get(newSubj);
            }
            if (URItoURIMap.containsKey(newPredicate)){
                newSubj = URItoURIMap.get(newPredicate);
            }
            if (URItoURIMap.containsKey(newObj)){
                newSubj = URItoURIMap.get(newObj);
            }
            newTriples.add(new ImmutableTriple<>(newSubj, newPredicate, newObj));
        }
        newOperation.setTriples(newTriples);

        return newOperation;
    }


}

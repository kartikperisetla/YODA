package edu.cmu.sv.domain.ontology2.query_fragments;

import edu.cmu.sv.domain.ontology2.QueryFragment;

import java.util.List;

/**
 * Created by David Cohen on 6/16/15.
 *
 * A parameterizable sparql fragment for computing a quality assuming that quality is encoded in the database as
 * one of several possible strings, which is reinterpreted as evenly spaced numbers.
 *
 * Example:
 * ordered values = ["none", "low", "medium", "high", "all"]
 * These are reinterpreted to [0, .25, .5, .75, 1.0]
 *
 * This must be the exhaustive list of values that will appear, other values will be interpreted as 1.0
 *
 */
public class OrderedStringsQueryFragment implements QueryFragment{
    List<String> orderedValues;
    String databaseProperty;

    /*
    * ordered values are from 0 to 1
    * */
    public OrderedStringsQueryFragment(String databaseProperty, List<String> orderedValues) {
        this.databaseProperty = databaseProperty;
        this.orderedValues = orderedValues;
    }

    @Override
    public String getSparqlQueryFragment(String firstArgument, String secondArgument, String resultVariable) {
        String ans = firstArgument+" base:"+databaseProperty+" ?i_"+databaseProperty+" . ";
        ans += "BIND( ";
        String ending = " AS "+resultVariable+") ";
        for (int i = 0; i < orderedValues.size(); i++) {
            String value = orderedValues.get(i);
            ans += "IF ( ?i_"+databaseProperty+" = \""+value+"\", "+1.0*i / (orderedValues.size()-1)+", ";
            if (i==orderedValues.size()-1){
                ans += " 1.0)";
            } else {
                ending = ")"+ending;
            }
        }
        return ans + ending;
    }

//// OLD EXAMPLE from PowerState
//public class PowerState extends TransientQuality {
//    static List<Class <? extends Thing>> arguments = Arrays.asList();
//
//    @Override
//    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
//        java.util.function.Function<List<String>, String> queryGen = (List<String> entityURIs) ->
//                entityURIs.get(0)+" base:power_state ?i_power_state . "+
//                        "BIND( IF(?i_power_state = \"on\", 1.0, 0.0) AS "+entityURIs.get(1)+") ";
//        return queryGen;
//
//    }


}

package edu.cmu.sv.domain.ontology.query_fragments;

import edu.cmu.sv.domain.ontology.QueryFragment;

/**
 * Created by David Cohen on 6/16/15.
 *
 * A parameterizable sparql fragment for computing a quality assuming that quality is encoded in the database as a
 * single numeric value with a known, limited range.
 */
public class ScaledShiftedSingleValueQueryFragment implements QueryFragment{
    String databaseProperty;
    double minInput;
    double maxInput;
    boolean flip;

    double scale;

    public ScaledShiftedSingleValueQueryFragment(String databaseProperty, double minInput, double maxInput, boolean flip) {
        this.databaseProperty = databaseProperty;
        this.minInput = minInput;
        this.maxInput = maxInput;
        this.flip = flip;
        scale = 1.0 / (maxInput - minInput);
    }

    @Override
    public String getSparqlQueryFragment(String firstArgument, String secondArgument, String resultVariable) {
        String ans = firstArgument+" base:"+databaseProperty+" ?i_"+databaseProperty+" . ";
        ans += "BIND( ";
        if (flip)
            ans += "1.0 - ";
        ans += "(";
        ans += "( ?i_"+databaseProperty+" - "+minInput+" ) * "+scale+" )";
        ans += " AS "+resultVariable;
        ans += ") ";
        return ans;
    }

//// OLD EXAMPLE from Cleanliness:
//    public class Cleanliness extends TransientQuality {
//        static List<Class <? extends Thing>> arguments = Arrays.asList();
//
//        @Override
//        public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
//            Function<List<String>, String> queryGen = (List<String> entityURIs) ->
//                    entityURIs.get(0)+" base:dust_level ?i_dust_level . "+
//                            "BIND( 1.0 - ( ?i_dust_level * 0.2 ) AS "+entityURIs.get(1)+") ";
//            return queryGen;
//
//        }

}

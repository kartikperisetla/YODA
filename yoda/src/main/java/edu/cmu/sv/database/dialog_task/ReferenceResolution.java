package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.database.Product;
import edu.cmu.sv.database.StringSimilarity;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Email;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.HasName;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasQualityRole;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;

import java.util.*;

/**
 * Created by David Cohen on 11/17/14.
 */
public class ReferenceResolution {

    /*
    * return a distribution over URI's that this JSONObject may refer to
    * */
    public static StringDistribution resolveReference(YodaEnvironment yodaEnvironment, JSONObject reference){
        String queryString = Database.prefixes + "SELECT DISTINCT ?x0 ?score0 WHERE {\n";
        queryString += referenceResolutionHelper(reference, 0).getKey();
        queryString += "} \nORDER BY DESC(?score0) \nLIMIT 10";

        yodaEnvironment.db.log(queryString);
        Database.getLogger().info("Reference resolution query:\n"+queryString);
        StringDistribution ans = new StringDistribution();
        try {
            TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();

            while (result.hasNext()){
                BindingSet bindings = result.next();
                ans.put(bindings.getValue("x0").stringValue(),
                        Double.parseDouble(bindings.getValue("score0").stringValue()));
            }
            result.close();
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
        }

        // insert all possible references into the dst focus (no salience for now)
        String insertString = Database.prefixes + "INSERT DATA {";
        for (String uri : ans.keySet()){
            insertString += "<"+uri+"> rdf:type dst:InFocus .\n";
        }
        insertString +="}";
        Database.getLogger().info("DST update insert:\n"+insertString);
        try {
            Update update = yodaEnvironment.db.connection.prepareUpdate(
                    QueryLanguage.SPARQL, insertString, Database.dstFocusURI);
            update.execute();
        } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
            e.printStackTrace();
        }
        return ans;
    }


    /*
    * Return a partial query string and an updated tmpVarIndex for the reference JSONObject
    * tmpVarIndex is used so that temporary variables within the query don't have naming conflicts
    * */
    private static Pair<String, Integer> referenceResolutionHelper(JSONObject reference, Integer tmpVarIndex){
        try {
            int referenceIndex = tmpVarIndex;
            tmpVarIndex ++;
            String ans = "";
//            String ans = "{ SELECT ?x" + referenceIndex + " ?score" + referenceIndex + " WHERE {\n";
            if (Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get((String) reference.get("class")))) {
                ans += "?x" + referenceIndex + " rdf:type base:" + reference.get("class") + " .\n";
            }
            List<String> scoresToAccumulate = new LinkedList<>();
            for (Object key : reference.keySet()) {

                if (key.equals("class")) {
                    continue;
                } else if (HasQualityRole.class.isAssignableFrom(OntologyRegistry.roleNameMap.get((String) key))) {
                    double center;
                    double slope;
                    Class<? extends TransientQuality> qualityClass;
                    Class<? extends Thing> qualityDegreeClass = OntologyRegistry.thingNameMap.
                            get((String) ((JSONObject) reference.get(key)).get("class"));
                    List<String> entityURIs = new LinkedList<>();
                    entityURIs.add("?x" + referenceIndex);
                    if (Preposition.class.isAssignableFrom(qualityDegreeClass)) {
                        Preposition preposition = (Preposition) qualityDegreeClass.newInstance();
                        center = preposition.getCenter();
                        slope = preposition.getSlope();
                        qualityClass = preposition.getQuality();
                        //recursively resolve the child to this PP, add the child's variable to entityURIs
                        tmpVarIndex++;
                        entityURIs.add("?x" + tmpVarIndex);
                        scoresToAccumulate.add("?score" + tmpVarIndex);
                        Pair<String, Integer> updates = referenceResolutionHelper(
                                (JSONObject) ((JSONObject) reference.get(key)).get(InRelationTo.class.getSimpleName()),
                                tmpVarIndex);
                        ans += "{\nSELECT ?x" + tmpVarIndex + " ?score" + tmpVarIndex + " WHERE {\n";
                        ans += updates.getKey();
                        ans += "}\nORDER BY DESC(?score" + tmpVarIndex+ ")\n" + "LIMIT 30\n} .\n";
                        tmpVarIndex = updates.getRight();
                    } else if (Adjective.class.isAssignableFrom(qualityDegreeClass)) {
                        Adjective adjective = (Adjective) qualityDegreeClass.newInstance();
                        center = adjective.getCenter();
                        slope = adjective.getSlope();
                        qualityClass = adjective.getQuality();
                    } else {
                        throw new Error("degreeClass is neither an Adjective nor a Preposition class");
                    }
                    scoresToAccumulate.add("?score" + tmpVarIndex);
                    entityURIs.add("?transient_quality" + tmpVarIndex);
                    ans += qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(entityURIs) +
                            "BIND(base:LinearFuzzyMap(" + center + ", " + slope + ", ?transient_quality" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                } else if (HasName.class.equals(OntologyRegistry.roleNameMap.get((String) key))) {
                    ans += "?x" + referenceIndex + " rdfs:label ?tmp" + tmpVarIndex + " . \n" +
                            "base:" + ((JSONObject)reference.get(HasName.class.getSimpleName())).
                            get(HasURI.class.getSimpleName()) + " rdf:value ?tmpV" + tmpVarIndex + " . \n" +
                            "BIND( base:" + StringSimilarity.class.getSimpleName() +
                            "(?tmp" + tmpVarIndex + ", ?tmpV" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                    scoresToAccumulate.add("?score"+tmpVarIndex);
                } else {
                    throw new Error("this role isn't handled:" + key);
                }
                tmpVarIndex++;
            }
            ans += "BIND(base:" + Product.class.getSimpleName() + "(";
            ans += String.join(", ", scoresToAccumulate);
            ans += ") AS ?score" + referenceIndex + ")\n";

            return new ImmutablePair<>(ans, tmpVarIndex);

        } catch (InstantiationException | IllegalAccessException e){
            e.printStackTrace();
            throw new Error();
        }
    }

    /*
    * return the truth with which the description describes the grounded individual
    * Any nested noun phrases in the description must be grounded in advance (WebResources)
    * */
    public static Double descriptionMatch(YodaEnvironment yodaEnvironment, JSONObject individual, JSONObject description){
        try {
            String queryString = yodaEnvironment.db.prefixes + "SELECT ?score WHERE {\n";
            String individualURI = (String) individual.get(HasURI.class.getSimpleName());
            int tmpVarIndex = 0;
            List<String> scoresToAccumulate = new LinkedList<>();
            for (Object key : description.keySet()) {
                if (key.equals("class")) {
                    if (description.get(key).equals(UnknownThingWithRoles.class.getSimpleName()))
                        continue;
                    queryString += "<"+individualURI+"> rdf:type base:"+description.get(key)+" . \n";
//                    queryString += "BIND(IF({<" + individualURI + "> rdf:type base:" + description.get(key) + "}, 1.0, 0.0) AS ?score"+tmpVarIndex+")\n";
//                    System.out.println("requiring individual to have type: base:"+description.get(key));
                } else if (HasQualityRole.class.isAssignableFrom(OntologyRegistry.roleNameMap.get((String) key))) {
                    double center;
                    double slope;
                    Class<? extends TransientQuality> qualityClass;
                    Class<? extends Thing> qualityDegreeClass = OntologyRegistry.thingNameMap.
                            get((String) ((JSONObject) description.get(key)).get("class"));
                    List<String> entityURIs = new LinkedList<>();
                    entityURIs.add("<"+individualURI+">");
                    if (Preposition.class.isAssignableFrom(qualityDegreeClass)) {
                        Preposition preposition = (Preposition) qualityDegreeClass.newInstance();
                        center = preposition.getCenter();
                        slope = preposition.getSlope();
                        qualityClass = preposition.getQuality();
                        String nestedURI = ((String) ((JSONObject) ((JSONObject) description.get(key)).
                                get(InRelationTo.class.getSimpleName())).get(HasURI.class.getSimpleName()));
                        entityURIs.add("<"+nestedURI+">");
                    } else if (Adjective.class.isAssignableFrom(qualityDegreeClass)) {
                        Adjective adjective = (Adjective) qualityDegreeClass.newInstance();
                        center = adjective.getCenter();
                        slope = adjective.getSlope();
                        qualityClass = adjective.getQuality();
                    } else {
                        throw new Error("degreeClass is neither an Adjective nor a Preposition class");
                    }
                    entityURIs.add("?transient_quality"+tmpVarIndex);
                    queryString += qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(entityURIs) +
                            "BIND(base:LinearFuzzyMap(" + center + ", " + slope + ", ?transient_quality" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                    scoresToAccumulate.add("?score"+tmpVarIndex);
                }
                tmpVarIndex++;
            }

            queryString += "BIND(base:" + Product.class.getSimpleName() + "(";
            queryString += String.join(", ", scoresToAccumulate);
            queryString += ") AS ?score)\n";
            queryString += "}";

            yodaEnvironment.db.log(queryString);
            Database.getLogger().info("Description match query:\n"+queryString);

            Double ans = null;
            try {
                TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString, Database.baseURI);
                TupleQueryResult result = query.evaluate();

                if (result.hasNext()){
                    BindingSet bindings = result.next();
                    ans = Double.parseDouble(bindings.getValue("score").stringValue());
                    result.close();
                    Database.getLogger().info("Description match result:"+ans);
                }
                else {
                    // answer is unknown / question doesn't make sense
                    Database.getLogger().info("Description match result is unknown / question doesn't make sense: "+ans);
                }
            } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
                e.printStackTrace();
                System.exit(0);
            }

            return ans;

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error();
        }
    }

}

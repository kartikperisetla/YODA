package edu.cmu.sv.database;

import com.google.common.primitives.Doubles;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.WebResource;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Time;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.*;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.has_quality_subroles.HasQualityRole;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 11/17/14.
 */
public class ReferenceResolution {
    public static final double minFocusSalience = .002;
    public static final double missingRoleNotInferredPenalty = .3;
    private static final String unfilledJunkString = "UNFILLED JUNK STRING@@234";
    public static final boolean PRINT_CACHING_DEBUG_OUTPUT = false;

    private static Map<SemanticsModel, StringDistribution> refResCache = new HashMap<>();
    private static Map<SemanticsModel, StringDistribution> refResCacheInFocus = new HashMap<>();

    public static void clearCache(){
        if (PRINT_CACHING_DEBUG_OUTPUT)
            System.err.println("ReferenceResolution: resetting ref res cache");
        refResCache = new HashMap<>();
        refResCacheInFocus = new HashMap<>();
    }

    public static StringDistribution checkCache(JSONObject reference, boolean requireReferentInFocus){
        if (PRINT_CACHING_DEBUG_OUTPUT)
            System.err.println("RefRes.checkCache: reference:"+reference.toJSONString() + ", " + requireReferentInFocus);
        if (requireReferentInFocus) {
            for (SemanticsModel model : refResCacheInFocus.keySet()) {
                if (SemanticsModel.contentEquivalenceComparisonAndReport(model, new SemanticsModel(reference)).getLeft()) {
                    if (PRINT_CACHING_DEBUG_OUTPUT)
                        System.err.println("ReferenceResolution: cache hit");
                    return refResCacheInFocus.get(model).deepCopy();
                }
            }
        } else {
            for (SemanticsModel model : refResCache.keySet()){
                if (SemanticsModel.contentEquivalenceComparisonAndReport(model, new SemanticsModel(reference)).getLeft()){
                    if (PRINT_CACHING_DEBUG_OUTPUT)
                        System.err.println("ReferenceResolution: cache hit");
                    return refResCache.get(model).deepCopy();
                }
            }
        }
        if (PRINT_CACHING_DEBUG_OUTPUT)
            System.err.println("ReferenceResolution: XXX cache miss XXX");
        return null;
    }

    public static StringDistribution inferRole(YodaEnvironment yodaEnvironment,
                                               Class<? extends Role> roleClass){
        StringDistribution ans = new StringDistribution();
        try {
            // find out what classes are acceptable to fill this role
            Set<Class<? extends Thing>> range = roleClass.newInstance().getRange();

            // query the most salient objects of that class (only look for DST in focus fillers)
            String queryString = Database.prefixes + "SELECT DISTINCT ?x0 ?score0 WHERE {\n";
            queryString += "?x0 rdf:type dst:InFocus .\n";
            queryString += "{ " + String.join(" UNION ", range.stream().map(x -> "{ ?x0 rdf:type base:"+x.getSimpleName()+" } ").collect(Collectors.toList()))+ "} \n";
            queryString += "?x0 dst:salience ?score0 . \n";
            queryString += "} \nORDER BY DESC(?score0) \nLIMIT 10";

            yodaEnvironment.db.log(queryString);
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("role inference query", queryString).toJSONString());
//            Database.getLogger().info("role inference query:\n"+queryString);
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
                System.exit(0);
            }

            ans.put(unfilledJunkString, missingRoleNotInferredPenalty);
            ans.normalize();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
//        System.out.printf("role inference marginal:" + ans);
        return ans;
    }

    /*
    * return a distribution over URI's that this JSONObject may refer to
    * */
    public static StringDistribution resolveReference(YodaEnvironment yodaEnvironment,
                                                      JSONObject reference,
                                                      boolean requireReferentInFocus,
                                                      boolean normalizeResult){
        StringDistribution cachedAns = checkCache(reference, requireReferentInFocus);
        if (cachedAns!=null)
            return cachedAns;

        StringDistribution ans = new StringDistribution();
        if (reference.get("class").equals(Time.class.getSimpleName())){
            LocalDateTime referencePoint = LocalDateTime.now();
//            System.err.println("now:" + referencePoint.toString());
            referencePoint = referencePoint.with(new NextTimeAdjuster(reference));
//            System.err.println("after time adjustment:" + referencePoint);
            String newUri = yodaEnvironment.db.insertValue(referencePoint);
            ans.put(newUri, 1.0);

        } else {

//        System.err.println("resolveReference: reference:" + reference);
            String queryString = Database.prefixes + "SELECT DISTINCT ?x0 ?score0 WHERE {\n";
            if (requireReferentInFocus)
                queryString += "?x0 rdf:type dst:InFocus .\n";
            queryString += referenceResolutionHelper(reference, 0).getKey();
            queryString += "} \nORDER BY DESC(?score0) \nLIMIT 10";

            yodaEnvironment.db.log(queryString);
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("reference resolution query", queryString).toJSONString());
//        Database.getLogger().info("Reference resolution query:\n" + queryString);
            try {
                TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = query.evaluate();

                while (result.hasNext()) {
                    BindingSet bindings = result.next();
                    if (bindings.getValue("score0") == null)
                        continue;
                    String key = bindings.getValue("x0").stringValue();
                    Double score = Double.parseDouble(bindings.getValue("score0").stringValue());
                    ans.put(key, Doubles.max(score, ans.get(key)));
                }
                result.close();

            } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
                e.printStackTrace();
                System.exit(0);
            }

        }
//        System.err.println(ans);
        if (normalizeResult)
            ans.normalize();
        if (requireReferentInFocus){
            refResCacheInFocus.put(new SemanticsModel(reference.toJSONString()), ans.deepCopy());
        } else {
            refResCache.put(new SemanticsModel(reference.toJSONString()), ans.deepCopy());
        }
        return ans;
    }


    private static class NextTimeAdjuster implements TemporalAdjuster {
        JSONObject timeDescription;
        String AmPm = null;
        Long hour = null;
        Long tenMinute = null;
        Long singleMinute = null;
        Long minuteOfHour = (long) 0;

        public NextTimeAdjuster(JSONObject timeDescription) {
            this.timeDescription = timeDescription;
            if (timeDescription.containsKey(HasAmPm.class.getSimpleName()))
                AmPm = (String) timeDescription.get(HasAmPm.class.getSimpleName());
            if (timeDescription.containsKey(HasHour.class.getSimpleName()))
                hour = (long) timeDescription.get(HasHour.class.getSimpleName());
            if (timeDescription.containsKey(HasTenMinute.class.getSimpleName())) {
                tenMinute = (long) timeDescription.get(HasTenMinute.class.getSimpleName());
                minuteOfHour += 10*tenMinute;
            }
            if (timeDescription.containsKey(HasSingleMinute.class.getSimpleName())) {
                singleMinute = (long) timeDescription.get(HasSingleMinute.class.getSimpleName());
                minuteOfHour += singleMinute;
            }
        }

        @Override
        public Temporal adjustInto(Temporal temporal) {
            Temporal ans = temporal.plus(0, ChronoUnit.SECONDS);

            boolean alternateAmPm = false;
            boolean incrementDay = false;

            if (AmPm == null) {
//                System.err.println("refres: adjustInto: AmPm==null");
                alternateAmPm =
                        (hour != null && temporal.get(ChronoField.HOUR_OF_AMPM) > hour) ||
                                (hour != null && temporal.get(ChronoField.HOUR_OF_AMPM) == hour && minuteOfHour != 0);
                incrementDay = alternateAmPm && temporal.get(ChronoField.HOUR_OF_DAY) >= 12;
            } else {
//                System.err.println("refres: adjustInto: AmPm!=null");
                if (temporal.get(ChronoField.AMPM_OF_DAY) != (AmPm.equals("AM") ? 0 : 1))
                    alternateAmPm = true;

                if (temporal.get(ChronoField.AMPM_OF_DAY) > (AmPm.equals("AM") ? 0 : 1))
                    incrementDay = true;
                if (temporal.get(ChronoField.AMPM_OF_DAY) == (AmPm.equals("AM") ? 0 : 1) &&
                        hour != null &&
                        temporal.get(ChronoField.HOUR_OF_AMPM) >= hour)
                    incrementDay = true;
            }

//            System.err.println("refres: alternateAmPm"+alternateAmPm+", incrementDay:"+incrementDay);

            if (alternateAmPm)
                ans = ans.with(ChronoField.AMPM_OF_DAY, temporal.get(ChronoField.AMPM_OF_DAY)==1 ? 0 : 1);
            if (incrementDay)
                ans = ans.with(ChronoField.EPOCH_DAY, temporal.getLong(ChronoField.EPOCH_DAY) + 1);
            if (hour != null)
                ans = ans.with(ChronoField.HOUR_OF_AMPM, hour);
            ans = ans.with(ChronoField.MINUTE_OF_HOUR, minuteOfHour);
            ans = ans.with(ChronoField.SECOND_OF_MINUTE, 0);
            ans = ans.with(ChronoField.NANO_OF_SECOND, 0);
            return ans;
        }
    }


    /*
    * Return a partial query string and an updated tmpVarIndex for the reference JSONObject
    * tmpVarIndex is used so that temporary variables within the query don't have naming conflicts
    * */
    private static Pair<String, Integer> referenceResolutionHelper(JSONObject reference,
                                                                   Integer tmpVarIndex){
        try {
            int referenceIndex = tmpVarIndex;
            tmpVarIndex ++;
            String ans = "";

            if (Noun.class.isAssignableFrom(Ontology.thingNameMap.get((String) reference.get("class")))) {
                ans += "?x" + referenceIndex + " rdf:type base:" + reference.get("class") + " .\n";
            }
            List<String> scoresToAccumulate = new LinkedList<>();

            // if not a named entity, weight by salience
            if (! reference.keySet().contains(HasName.class.getSimpleName())) {
                scoresToAccumulate.add("?score"+tmpVarIndex);
                ans += "{{OPTIONAL { ?x" + referenceIndex + " dst:salience ?score" + tmpVarIndex + " }}\n" +
                        "UNION\n" +
                        "{OPTIONAL { FILTER NOT EXISTS { ?x" + referenceIndex + " dst:salience ?score" + tmpVarIndex + " } " +
                        "BIND(" + minFocusSalience + " AS ?score" + tmpVarIndex + " ) }}}\n";
                tmpVarIndex++;
            }

            for (Object key : reference.keySet()) {
                if (key.equals("class")) {
                    continue;
                } else if (key.equals(HasURI.class.getSimpleName())){
//                    ans += "FILTER (?x"+referenceIndex+" = <"+reference.get(HasURI.class.getSimpleName())+"> ) .\n";
//                    ans += "FILTER ( sameTerm (?x"+referenceIndex+", <"+reference.get(HasURI.class.getSimpleName())+">) ) .\n";
//                    ans += "BIND (<"+reference.get(HasURI.class.getSimpleName())+"> AS ?x"+referenceIndex+")\n";
                } else if (key.equals("refType")){
                    if (reference.get(key).equals("pronoun")){
                        ans += "?x rdf:type dst:InFocus . \n";
                    } else {
                        throw new Error("unknown / unhandled reference type"+ reference.get(key));
                    }
                } else if (HasQualityRole.class.isAssignableFrom(Ontology.roleNameMap.get((String) key))) {
                    double center;
                    double slope;
                    Class<? extends TransientQuality> qualityClass;
                    Class<? extends Thing> qualityDegreeClass = Ontology.thingNameMap.
                            get((String) ((JSONObject) reference.get(key)).get("class"));
                    List<String> entityURIs = new LinkedList<>();
                    entityURIs.add("?x" + referenceIndex);
                    if (Adjective.class.isAssignableFrom(qualityDegreeClass)) {
                        Adjective adjective = (Adjective) qualityDegreeClass.newInstance();
                        center = adjective.getCenter();
                        slope = adjective.getSlope();
                        qualityClass = adjective.getQuality();
                    } else {
                        continue;
                    }
                    scoresToAccumulate.add("?score" + tmpVarIndex);
                    entityURIs.add("?transient_quality" + tmpVarIndex);
                    ans += qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(entityURIs) +
                            "BIND(base:LinearFuzzyMap(" + center + ", " + slope + ", ?transient_quality" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                    ans += "FILTER(?score"+tmpVarIndex+" > "+.5+")\n";
                } else if (HasName.class.equals(Ontology.roleNameMap.get((String) key))) {
                    String similarityString = null;
                    if (!(reference.get(HasName.class.getSimpleName()) instanceof String)){
                        ans += "base:" + ((JSONObject)reference.get(HasName.class.getSimpleName())).
                                get(HasURI.class.getSimpleName()) + " rdf:value ?tmpV" + tmpVarIndex + " . \n";
                        similarityString = "?tmpV"+tmpVarIndex;
                    } else {
                        similarityString = (String)reference.get(HasName.class.getSimpleName());
                    }

                    ans += "?x" + referenceIndex + " rdfs:label ?tmp" + tmpVarIndex + " . \n" +
                            "BIND(base:" + StringSimilarity.class.getSimpleName() +
                            "(?tmp" + tmpVarIndex + ", \""+ similarityString + "\") AS ?score" + tmpVarIndex + ")\n";
                    ans += "FILTER(?score"+tmpVarIndex+" > "+StringSimilarity.possibleMatchThreshold+")\n";

                    scoresToAccumulate.add("?score"+tmpVarIndex);
                } else {
                    throw new Error("this role isn't handled:" + key);
                }
                tmpVarIndex++;
            }

            for (Object key : reference.keySet()) {
                if (key.equals("class") || key.equals("refType") || key.equals(HasURI.class.getSimpleName()))
                    continue;
                if (HasQualityRole.class.isAssignableFrom(Ontology.roleNameMap.get((String) key))) {
                    double center;
                    double slope;
                    Class<? extends TransientQuality> qualityClass;
                    Class<? extends Thing> qualityDegreeClass = Ontology.thingNameMap.
                            get((String) ((JSONObject) reference.get(key)).get("class"));
                    List<String> entityURIs = new LinkedList<>();
                    entityURIs.add("?x" + referenceIndex);
                    if (Preposition.class.isAssignableFrom(qualityDegreeClass)) {
                        Preposition preposition = (Preposition) qualityDegreeClass.newInstance();
                        center = preposition.getCenter();
                        slope = preposition.getSlope();
                        qualityClass = preposition.getQuality();
                        //recursively resolveDiscourseUnit the child to this PP, add the child's variable to entityURIs
                        JSONObject nestedNP = (JSONObject) ((JSONObject) reference.get(key)).get(InRelationTo.class.getSimpleName());
                        if (nestedNP.containsKey(HasURI.class.getSimpleName())){
                            String nestedUri = (String) nestedNP.get(HasURI.class.getSimpleName());
                            tmpVarIndex++;
//                            entityURIs.add("?x" + tmpVarIndex);
                            entityURIs.add("<"+nestedUri+">");
                            entityURIs.add("?transient_quality" + tmpVarIndex);
                            scoresToAccumulate.add("?score" + tmpVarIndex);
                            ans += qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(entityURIs) +
                                    "BIND(base:LinearFuzzyMap(" + center + ", " + slope + ", ?transient_quality" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                            ans += "FILTER(?score"+tmpVarIndex+" > "+.5+")\n";
                        } else {
                            tmpVarIndex++;
                            entityURIs.add("?x" + tmpVarIndex);
                            scoresToAccumulate.add("?score" + tmpVarIndex);
                            Pair<String, Integer> updates = referenceResolutionHelper(nestedNP,tmpVarIndex);
                            ans += "{\nSELECT DISTINCT ?x" + tmpVarIndex + " ?score" + tmpVarIndex + " WHERE {\n";
                            ans += updates.getKey();
                            ans += "}\nORDER BY DESC(?score" + tmpVarIndex+ ")\n" + "LIMIT 5\n} .\n";
                            tmpVarIndex = updates.getRight();
                            scoresToAccumulate.add("?score" + tmpVarIndex);
                            entityURIs.add("?transient_quality" + tmpVarIndex);
                            ans += qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(entityURIs) +
                                    "BIND(base:LinearFuzzyMap(" + center + ", " + slope + ", ?transient_quality" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                            ans += "FILTER(?score"+tmpVarIndex+" > "+.5+")\n";
                        }
                    } else {
                        continue;
                    }
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
    * Salience is not part of this computation
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
                    queryString += "<" + individualURI + "> rdf:type base:" + description.get(key) + " . \n";
//                    queryString += "BIND(IF({<" + individualURI + "> rdf:type base:" + description.get(key) + "}, 1.0, 0.0) AS ?score"+tmpVarIndex+")\n";
//                    System.out.println("requiring individual to have type: base:"+description.get(key));
                } else if (key.equals("refType")){
                    continue;
                } else if (HasQualityRole.class.isAssignableFrom(Ontology.roleNameMap.get((String) key))) {
                    double center;
                    double slope;
                    Class<? extends TransientQuality> qualityClass;
                    Class<? extends Thing> qualityDegreeClass = Ontology.thingNameMap.
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
                        throw new Error("degreeClass is neither an adjective nor a Preposition class");
                    }
                    entityURIs.add("?transient_quality"+tmpVarIndex);
                    queryString += qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(entityURIs) +
                            "BIND(base:LinearFuzzyMap(" + center + ", " + slope + ", ?transient_quality" + tmpVarIndex + ") AS ?score" + tmpVarIndex + ")\n";
                    scoresToAccumulate.add("?score"+tmpVarIndex);
                } else if (HasName.class.equals(Ontology.roleNameMap.get((String) key))) {
                    String similarityString = null;
                    if (!(description.get(HasName.class.getSimpleName()) instanceof String)){
                        queryString += "base:" + ((JSONObject)description.get(HasName.class.getSimpleName())).
                                get(HasURI.class.getSimpleName()) + " rdf:value ?tmpV" + tmpVarIndex + " . \n";
                        similarityString = "?tmpV"+tmpVarIndex;
                    } else {
                        similarityString = (String)description.get(HasName.class.getSimpleName());
                    }

                    queryString += "<"+individualURI+"> rdfs:label ?tmp" + tmpVarIndex + " . \n" +
                            "BIND(base:" + StringSimilarity.class.getSimpleName() +
                            "(?tmp" + tmpVarIndex + ", \"" + similarityString + "\") AS ?score" + tmpVarIndex + ")\n";
                    scoresToAccumulate.add("?score"+tmpVarIndex);
                }
                tmpVarIndex++;
            }

            queryString += "BIND(base:" + Product.class.getSimpleName() + "(";
            queryString += String.join(", ", scoresToAccumulate);
            queryString += ") AS ?score)\n";
            queryString += "}";

            yodaEnvironment.db.log(queryString);
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("description match query", queryString).toJSONString());
//            Database.getLogger().info("Description match query:\n"+queryString);

            Double ans = null;
            try {
                TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString, Database.baseURI);
                TupleQueryResult result = query.evaluate();

                if (result.hasNext()){
                    BindingSet bindings = result.next();
                    ans = Double.parseDouble(bindings.getValue("score").stringValue());
//                    Database.getLogger().info("Description match result:"+ans);
                }
                result.close();
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

    public static Pair<Map<String, DiscourseUnit>, StringDistribution> resolveDiscourseUnit(DiscourseUnit hypothesis, YodaEnvironment yodaEnvironment) {
        // get grounded hypotheses / corresponding weights
        Pair<Map<String, DiscourseUnit>, StringDistribution> groundingHypotheses = resolveDiscourseUnitHelper(hypothesis, yodaEnvironment);
        Map<String, DiscourseUnit> discourseUnits = groundingHypotheses.getLeft();
        StringDistribution discourseUnitDistribution = groundingHypotheses.getRight();
        discourseUnitDistribution.normalize();
        return new ImmutablePair<>(discourseUnits, discourseUnitDistribution);
    }

    private static Pair<Map<String, DiscourseUnit>, StringDistribution> resolveDiscourseUnitHelper(DiscourseUnit targetDiscourseUnit, YodaEnvironment yodaEnvironment) {

        Triple<Set<String>, Set<String>, Set<String>> resolutionInformation = Utils.resolutionInformation(targetDiscourseUnit);
        Set<String> slotPathsToResolve = resolutionInformation.getLeft();
        Set<String> slotPathsToInfer = resolutionInformation.getMiddle();
        Set<String> alreadyResolvedPaths = resolutionInformation.getRight();

        SemanticsModel currentGroundedInterpretation = targetDiscourseUnit.getGroundInterpretation();

        Map<String, StringDistribution> resolutionMarginals = new HashMap<>();
        for (String slotPathToResolve : slotPathsToResolve) {
            resolutionMarginals.put(slotPathToResolve,
                    resolveReference(yodaEnvironment,
                            (JSONObject) targetDiscourseUnit.getSpokenByThem().newGetSlotPathFiller(slotPathToResolve),
                            false, true));
        }

        // add inferred required roles to reference marginals
        for (String pathToInfer : slotPathsToInfer) {
            resolutionMarginals.put(pathToInfer,
                    inferRole(yodaEnvironment,
                            Ontology.roleNameMap.get(pathToInfer.split("\\.")[pathToInfer.split("\\.").length - 1])));
        }


        Pair<StringDistribution, Map<String, Map<String, String>>> resolutionJoint =
                HypothesisSetManagement.getJointFromMarginals(resolutionMarginals, 10);
        Map<String, DiscourseUnit> discourseUnits = new HashMap<>();

        for (String jointHypothesisID : resolutionJoint.getKey().keySet()) {
            DiscourseUnit groundedDiscourseUnit = targetDiscourseUnit.deepCopy();
            SemanticsModel groundedModel = targetDiscourseUnit.getSpokenByThem().deepCopy();
            Map<String, String> assignment = resolutionJoint.getValue().get(jointHypothesisID);
            // add new bindings
            for (String slotPathVariable : assignment.keySet()) {
                if (assignment.get(slotPathVariable).equals(unfilledJunkString))
                    continue;
                if (groundedModel.newGetSlotPathFiller(slotPathVariable) == null) {
                    SemanticsModel.putAtPath(groundedModel.getInternalRepresentation(), slotPathVariable,
                            SemanticsModel.parseJSON(Ontology.webResourceWrap(assignment.get(slotPathVariable))));
                } else {
                    SemanticsModel.overwrite((JSONObject) groundedModel.newGetSlotPathFiller(slotPathVariable),
                            SemanticsModel.parseJSON(Ontology.webResourceWrap(assignment.get(slotPathVariable))));
                }
            }
            // include previously grounded paths
            for (String path : alreadyResolvedPaths) {
                SemanticsModel.overwrite((JSONObject) groundedModel.newGetSlotPathFiller(path),
                        (JSONObject) currentGroundedInterpretation.newGetSlotPathFiller(path));
            }
            groundedDiscourseUnit.setGroundInterpretation(groundedModel);
            discourseUnits.put(jointHypothesisID, groundedDiscourseUnit);
        }

        return new ImmutablePair<>(discourseUnits, resolutionJoint.getLeft());


    }

    public static void updateSalience(YodaEnvironment yodaEnvironment,
                                      NBestDistribution<DialogState> dialogStateDistribution){
        synchronized (yodaEnvironment.db.connection) {
            // compute salience from the active dialog state hypotheses
            Map<String, Double> salienceFromDialogState = new HashMap<>();
            for (DialogState currentDialogState : dialogStateDistribution.keySet()) {
                Map<String, DiscourseUnit> discourseUnits = currentDialogState.getDiscourseUnitHypothesisMap();
                for (String duIdentifier : discourseUnits.keySet()) {
                    DiscourseUnit currentDiscourseUnit = discourseUnits.get(duIdentifier);
                    double salienceBoost = dialogStateDistribution.get(currentDialogState) *
                            Utils.discourseUnitContextProbability(currentDialogState, currentDiscourseUnit);
                    Set<String> individualsInGroundedDiscourseUnit = new HashSet<>();
                    Set<String> patientsInGroundedDiscourseUnit = new HashSet<>();
                    if (currentDiscourseUnit.getGroundInterpretation() != null) {
                        Set<String> pathsToGroundedIndividuals =
                                currentDiscourseUnit.getGroundInterpretation().findAllPathsToClass(WebResource.class.getSimpleName());
                        pathsToGroundedIndividuals.forEach(x ->
                                individualsInGroundedDiscourseUnit.add((String) currentDiscourseUnit.
                                        getGroundInterpretation().
                                        newGetSlotPathFiller(x + "." + HasURI.class.getSimpleName())));

                        pathsToGroundedIndividuals.stream().filter(x -> x.contains("Patient")).
                                forEach(x -> patientsInGroundedDiscourseUnit.add((String) currentDiscourseUnit.
                                        getGroundInterpretation().
                                        newGetSlotPathFiller(x + "." + HasURI.class.getSimpleName())));

                    }
                    if (currentDiscourseUnit.getGroundTruth() != null) {
                        Set<String> pathsToGroundedIndividuals =
                                currentDiscourseUnit.getGroundTruth().findAllPathsToClass(WebResource.class.getSimpleName());
                        pathsToGroundedIndividuals.forEach(x ->
                                individualsInGroundedDiscourseUnit.add((String) currentDiscourseUnit.
                                        getGroundTruth().
                                        newGetSlotPathFiller(x + "." + HasURI.class.getSimpleName())));

                        pathsToGroundedIndividuals.stream().filter(x -> x.contains("Patient")).
                                forEach(x -> patientsInGroundedDiscourseUnit.add((String) currentDiscourseUnit.
                                        getGroundTruth().
                                        newGetSlotPathFiller(x + "." + HasURI.class.getSimpleName())));

                    }
                    for (String key : individualsInGroundedDiscourseUnit) {
                        if (!salienceFromDialogState.containsKey(key))
                            salienceFromDialogState.put(key, 0.0);
                        double weight = 1.0;
                        if (patientsInGroundedDiscourseUnit.contains(key))
                            weight = .5;

                        salienceFromDialogState.put(key, salienceFromDialogState.get(key) + weight*salienceBoost);
                    }
                }
            }

            // todo: retain / collect salience for objects not in the immediate discourse history

            // clear dst focus
            String deleteString = Database.prefixes + "DELETE {?x rdf:type dst:InFocus} WHERE {?x rdf:type dst:InFocus . }";
//            Database.getLogger().info("DST delete:\n" + deleteString);
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("DST salience delete", deleteString).toJSONString());
            try {
                Update update = yodaEnvironment.db.connection.prepareUpdate(
                        QueryLanguage.SPARQL, deleteString, Database.dstFocusURI);
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                e.printStackTrace();
                System.exit(0);
            }

            // new salience / dst focus
            String insertString = Database.prefixes + "INSERT DATA {";
            for (String uri : salienceFromDialogState.keySet()) {
                if (salienceFromDialogState.get(uri) < minFocusSalience)
                    continue;
                insertString += "<" + uri + "> rdf:type dst:InFocus .\n";
                insertString += "<" + uri + "> dst:salience " + salienceFromDialogState.get(uri) + ".\n";
            }
            insertString += "}";
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("DST salience update", insertString).toJSONString());
//            Database.getLogger().info("DST salience update:\n" + insertString);
            try {
                Update update = yodaEnvironment.db.connection.prepareUpdate(
                        QueryLanguage.SPARQL, insertString, Database.dstFocusURI);
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

    }

}

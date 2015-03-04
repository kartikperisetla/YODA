package edu.cmu.sv.database;


import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import java.io.*;
import java.lang.Object;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 6/21/14.
 */
public class Database {
    private static Logger logger = Logger.getLogger("yoda.database.Database");
    static {
        try {
            if (YodaEnvironment.mongoLoggingActive){
                MongoLogHandler handler = new MongoLogHandler();
                logger.addHandler(handler);
            } else {
                FileHandler fh;
                fh = new FileHandler("Database.log");
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public YodaEnvironment yodaEnvironment;
    // a counter used to create new URIs
    private long URICounter = 0;
    public final RepositoryConnection connection;
    public final static String baseURI = "http://sv.cmu.edu/yoda#";
    public final static String dstFocusURI = "http://sv.cmu.edu/dst_focus#";
    public final static String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX base: <"+baseURI+">\n"+
            "PREFIX dst: <"+dstFocusURI+">\n";

    public static Logger getLogger() {
        return logger;
    }

    public void log(String interaction){
//        System.out.println(interaction);
    }

    public void updateOntology(){
        try{
            // generate the class hierarchy
            Set<Class> databaseClasses = new HashSet<>(Ontology.nounClasses);
            databaseClasses.addAll(Ontology.verbClasses);
            databaseClasses.addAll(Ontology.qualityClasses);
            Set<Class> databaseProperties = new HashSet<>(Ontology.roleClasses);
            databaseProperties.addAll(Ontology.roleClasses);
            generateClassHierarchy(databaseClasses, databaseProperties);

            // generate the special individuals
            addIndividuals(Ontology.individualNameMap);

        } catch (RepositoryException | MalformedQueryException | UpdateExecutionException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void addDatabaseRegistry(DatabaseRegistry registry){
        try {
            // load the registered databases
            for (String filename : registry.turtleDatabaseSources) {
                connection.add(new InputStreamReader(new FileInputStream(filename), "UTF-8"),
                        baseURI, RDFFormat.TURTLE);
            }

            // load the non-ontology relations
            addNonOntologyProperties(registry.nonOntologyRelations);

        } catch (UpdateExecutionException | RDFParseException | RepositoryException | UnsupportedEncodingException | MalformedQueryException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Database(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
        // inferencing rdf database

        Repository repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));

        Object tmpConnection = null;
        try {
            repository.initialize();
            tmpConnection = repository.getConnection();
        } catch (RepositoryException e) {
            System.exit(0);
        }
        connection = (RepositoryConnection) tmpConnection;

    }

    private void addIndividuals(Map<String, Thing> individuals)
            throws UpdateExecutionException, MalformedQueryException, RepositoryException {
        String insertString = prefixes+"INSERT DATA\n{\n";
        for (String key : individuals.keySet()){
            Class<? extends Thing> cls = individuals.get(key).getClass();
            insertString += "base:"+key+" rdf:type base:"+cls.getSimpleName()+" .\n";
        }
        insertString+="}";
        log(insertString);
        Update update = connection.prepareUpdate(QueryLanguage.SPARQL, insertString);
        update.execute();
    }

    private void addNonOntologyProperties(Set<String> properties)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {

        String insertString = prefixes+"INSERT DATA\n{\n";
        for (String prop : properties){
            insertString += "base:"+prop+" rdf:type rdf:Property . \n";
        }
        insertString += "}";
        log(insertString);
        Update update = connection.prepareUpdate(QueryLanguage.SPARQL, insertString);
        update.execute();

    }


    /*
    * Insert all the classes and properties of an ontology into the database.
    * Insert all direct parent-child relationships (assume that the database does its own class hierarchy inference)
    * Insert rdfs:class and rdfs:property as required
    * */
    private void generateClassHierarchy(Set<Class> classes, Set<Class> properties)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {

        String insertString = prefixes+"INSERT DATA {\n";
        for (Class cls : classes){
            insertString += generateTriple(cls, "rdf:type", "rdfs:Class")+".\n";
            if (cls != Noun.class && cls != Verb.class) {
                insertString += generateTriple(cls, "rdfs:subClassOf", cls.getSuperclass()) + ".\n";
            }
        }
        for (Class prop : properties){
            insertString += generateTriple(prop, "rdf:type", "rdf:Property")+".\n";
            if (prop != Role.class) {
                insertString += generateTriple(prop, "rdfs:subPropertyOf", prop.getSuperclass()) + ".\n";
            }
        }
        insertString += "}";
//        System.out.println("Creating ontology, insert string:\n" + insertString);
        log(insertString);
        Update update = connection.prepareUpdate(QueryLanguage.SPARQL, insertString);
        update.execute();
    }




    /*
    * Create a SPARQL triple for a given java triple of referents
    *
    * Each referent can be either:
    *  - a string, which means it is already grounded to a URI in the database,
    *  - a Class <? extends ontology.Thing>, which means it needs to have a string created for it
    * and needs a prefix.
    *
    * */
    private String generateTriple(Object subject, Object predicate, Object obj) {
        assert (subject instanceof String || subject instanceof Class);
        assert (predicate instanceof String || predicate instanceof Class);
        assert (obj instanceof String || obj instanceof Class);

        String ans = "";
        if (subject instanceof String) {
            ans += subject + " ";
        } else {
            ans += "base:" + ((Class) subject).getSimpleName() + " ";
        }

        if (predicate instanceof String) {
            ans += predicate + " ";
        } else {
            ans += "base:" + ((Class) predicate).getSimpleName() + " ";
        }

        if (obj instanceof String) {
            ans += obj + " ";
        } else {
            ans += "base:"+((Class)obj).getSimpleName()+" ";
        }

        return ans;
    }

    /*
    * Insert a data value to the triple store, return the unique identifier
    * */
    public String insertValue(Object obj){
        try {
            String newURI = "auto_generated_value_URI" + URICounter++;
            if (obj instanceof String) {
                String updateString = prefixes + "INSERT DATA \n{ base:" + newURI + " rdf:value \"" + obj + "\"^^xsd:string}";
//            System.out.println("Database.insertValue: updateString:"+updateString);
                synchronized (connection) {
                    log(updateString);
                    Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
                    update.execute();
                }
            } else if (obj instanceof Integer) {
                String updateString = prefixes + "INSERT DATA \n{ base:" + newURI + " rdf:value \"" + obj + "\"^^xsd:int}";
//            System.out.println("Database.insertValue: updateString:"+updateString);
                synchronized (connection) {
                    log(updateString);
                    Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
                    update.execute();
                }
            } else {
                throw new Error("Can't insertValue of that type");
            }
            return newURI;
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    /*
    * Run a sparql query on the database and return all values for the variable x
    * */
    public Set<String> runQuerySelectX(String queryString){
        log(queryString);
        Set<String> ans = new HashSet<>();
        try {
            synchronized (connection) {
                TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = query.evaluate();

                while (result.hasNext()) {
                    BindingSet bindings = result.next();
                    ans.add(bindings.getValue("x").stringValue());
                }
                result.close();
            }
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return ans;
    }

    /*
    * Run a sparql query on the database and return all value pairs for the variables x and y
    * */
    public Set<Pair<String, String>> runQuerySelectXAndY(String queryString){
        log(queryString);
        Set<Pair<String, String>> ans = new HashSet<>();
        try {
            synchronized (connection) {
                TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = query.evaluate();

                while (result.hasNext()) {
                    BindingSet bindings = result.next();
                    ans.add(new ImmutablePair<>(bindings.getValue("x").stringValue(),
                            bindings.getValue("y").stringValue()));
                }
                result.close();
            }
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static String getLocalName(String fullName){
        return fullName.split("#")[1];
    }

    public String mostSpecificClass(String entityName){
        String queryString = prefixes + "SELECT ?x WHERE { <"+entityName+"> rdf:type ?x . }";
        Set<Class> classes = runQuerySelectX(queryString).stream().
                map(Database::getLocalName).
                filter(x -> Ontology.thingNameMap.containsKey(x)).
                map(Ontology.thingNameMap::get).
                collect(Collectors.toSet());
        for (Class cls : classes){
            boolean anyChildren = false;
            for (Class cls2 : classes){
                if (cls==cls2)
                    continue;
                if (cls.isAssignableFrom(cls2)){
                    anyChildren = true;
                    break;
                }
            }
            if (!anyChildren)
                return cls.getSimpleName();
        }
        return null;
    }

    public Set<List<String>> possibleBindings(List<Class<? extends Thing>> classConstraints){
        Set<List<String>> ans = new HashSet<>();
        List<String> variables = new LinkedList<>();
        try {
            String graph = "";
            for (int i = 0; i < classConstraints.size(); i++) {
                variables.add("binding" + i);
                graph += "?binding" + i + " rdf:type base:" + classConstraints.get(i).getSimpleName() + " . ";
            }
            String queryString = prefixes + "SELECT " +String.join(" ", variables.stream().map(x -> "?"+x).collect(Collectors.toList()))
                    + " WHERE { " + graph + "}";
//            System.out.println("Database.possibleBindings. qQuerystring:\n"+queryString);
            synchronized (connection) {
                log(queryString);
                TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = query.evaluate();

                while (result.hasNext()) {
                    BindingSet bindings = result.next();
                    ans.add(variables.stream().map(bindings::getValue).map(Value::stringValue).collect(Collectors.toList()));
                }
                result.close();
            }
        } catch (MalformedQueryException | RepositoryException | QueryEvaluationException e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return ans;

    }

    public Double evaluateQualityDegree(List<String> entityURIs, Class<? extends ThingWithRoles> degreeClass){
        try {
            double center;
            double slope;
            Class<? extends TransientQuality> qualityClass;
            if (Preposition.class.isAssignableFrom(degreeClass)) {
                Preposition preposition = (Preposition) degreeClass.newInstance();
                center = preposition.getCenter();
                slope = preposition.getSlope();
                qualityClass = preposition.getQuality();
            } else if (Adjective.class.isAssignableFrom(degreeClass)) {
                Adjective adjective = (Adjective) degreeClass.newInstance();
                center = adjective.getCenter();
                slope = adjective.getSlope();
                qualityClass = adjective.getQuality();
            } else {
                throw new Error("degreeClass is neither an adjective nor a Preposition class:" + degreeClass.getSimpleName());
            }

            List<String> params = entityURIs.stream().map(x -> "<"+x+">").collect(Collectors.toList());
            params.add("?transient_quality");

            String queryString = prefixes + "SELECT ?fuzzy_mapped_quality WHERE {" +
                    qualityClass.newInstance().getQualityCalculatorSPARQLQuery().apply(params) +
                    "BIND(base:LinearFuzzyMap("+center+", "+slope+", ?transient_quality) AS ?fuzzy_mapped_quality)}";
            Double ans = null;
            synchronized (connection) {
                log(queryString);
                TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = query.evaluate();
                if (result.hasNext()) {
                    BindingSet bindings = result.next();
                    ans = Double.parseDouble(bindings.getValue("fuzzy_mapped_quality").stringValue());
                }
                result.close();
            }
            return ans;

        } catch (InstantiationException | IllegalAccessException | MalformedQueryException | RepositoryException | QueryEvaluationException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }


}

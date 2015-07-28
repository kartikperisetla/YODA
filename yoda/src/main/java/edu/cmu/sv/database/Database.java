package edu.cmu.sv.database;


import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.ontology.Noun;
import edu.cmu.sv.domain.ontology.Quality;
import edu.cmu.sv.domain.ontology.QualityDegree;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
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
    public Set<Sensor> sensors = new HashSet<>();
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
            generateClassHierarchy(Ontology.nouns);

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

            sensors.addAll(registry.sensors);

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
    private void generateClassHierarchy(Set<Noun> nouns)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {

        String insertString = prefixes+"INSERT DATA {\n";
        for (Noun nounClass : nouns) {
            insertString += "base:" + nounClass.name + " rdf:type rdfs:Class .\n";
            if (nounClass.directParent != null)
                insertString += "base:" + nounClass.name + " rdfs:subClassOf base:" + nounClass.directParent.name + " .\n";
        }
//        for (Class prop : properties){
//            insertString += generateTriple(prop, "rdf:type", "rdf:Property")+".\n";
//            if (prop != Role.class) {
//                insertString += generateTriple(prop, "rdfs:subPropertyOf", prop.getSuperclass()) + ".\n";
//            }
//        }
        insertString += "}";
//        System.out.println("Creating ontology, insert string:\n" + insertString);
        log(insertString);
        Update update = connection.prepareUpdate(QueryLanguage.SPARQL, insertString);
        update.execute();
    }

    /*
    * Insert a data value to the triple store, return the unique identifier
    * */
    public String insertValue(Object obj){
        try {
            String newURI = "auto_generated_value_URI" + URICounter++;
            if (obj instanceof String) {
                String updateString = prefixes + "INSERT DATA \n{ base:" + newURI + " rdf:value \"" + obj + "\"^^xsd:string}";
                synchronized (connection) {
                    log(updateString);
                    Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
                    update.execute();
                }
            } else if (obj instanceof Integer) {
                String updateString = prefixes + "INSERT DATA \n{ base:" + newURI + " rdf:value \"" + obj + "\"^^xsd:int}";
                synchronized (connection) {
                    log(updateString);
                    Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
                    update.execute();
                }
            } else if (obj instanceof LocalDateTime){
                String xsdString = "\""+((LocalDateTime)obj).format(DateTimeFormatter.ISO_DATE_TIME) + "\"^^xsd:dateTime";
                String updateString = prefixes + "INSERT DATA \n{ base:" + newURI + " rdf:value " + xsdString + "}";
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
        Set<Noun> nounClasses = runQuerySelectX(queryString).stream().
                map(Database::getLocalName).
                filter(x -> Ontology.nounNameMap.containsKey(x)).
                map(Ontology.nounNameMap::get).
                collect(Collectors.toSet());
        for (Noun nounClass : nounClasses){
            boolean anyChildren = false;
            for (Noun nounClass2 : nounClasses){
                if (nounClass==nounClass2)
                    continue;
                if (Ontology.nounInherits(nounClass, nounClass2)){
                    anyChildren = true;
                    break;
                }
            }
            if (!anyChildren)
                return nounClass.name;
        }
        return null;
    }

//    public Set<List<String>> possibleBindings(List<Class<? extends Thing>> classConstraints){
//        Set<List<String>> ans = new HashSet<>();
//        List<String> variables = new LinkedList<>();
//        try {
//            String graph = "";
//            for (int i = 0; i < classConstraints.size(); i++) {
//                variables.add("binding" + i);
//                graph += "?binding" + i + " rdf:type base:" + classConstraints.get(i).getSimpleName() + " . ";
//            }
//            String queryString = prefixes + "SELECT " +String.join(" ", variables.stream().map(x -> "?"+x).collect(Collectors.toList()))
//                    + " WHERE { " + graph + "}";
////            System.out.println("Database.possibleBindings. qQuerystring:\n"+queryString);
//            synchronized (connection) {
//                log(queryString);
//                TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
//                TupleQueryResult result = query.evaluate();
//
//                while (result.hasNext()) {
//                    BindingSet bindings = result.next();
//                    ans.add(variables.stream().map(bindings::getValue).map(Value::stringValue).collect(Collectors.toList()));
//                }
//                result.close();
//            }
//        } catch (MalformedQueryException | RepositoryException | QueryEvaluationException e) {
//            e.printStackTrace();
//            throw new Error(e);
//        }
//        return ans;
//    }

    public Double evaluateQualityDegree(String firstArgument, String secondArgument, QualityDegree degreeClass){
        try {
            double center;
            double slope;
            Quality qualityClass = degreeClass.getQuality();
            center = degreeClass.getCenter();
            slope = degreeClass.getSlope();

            String queryString = prefixes + "SELECT ?fuzzy_mapped_quality WHERE {" +
                    qualityClass.queryFragment.getResolutionSparqlQueryFragment("<" + firstArgument + ">", "<" + secondArgument + ">", "?transient_quality") +
                    "BIND(base:LinearFuzzyMap("+center+", "+slope+", ?transient_quality) AS ?fuzzy_mapped_quality)}";
//            System.err.println("Database.evaluateQualityDegree: queryString:\n"+queryString);
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

        } catch (MalformedQueryException | RepositoryException | QueryEvaluationException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }


}

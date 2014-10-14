package edu.cmu.sv.database;


import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.role.Role;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import java.lang.Object;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 6/21/14.
 */
public class Database {

//    Repository repository;
    // a counter used to create new URIs
    private long URICounter = 0;
    RepositoryConnection connection;
    public final String baseURI = "http://sv.cmu.edu/yoda#";
    public final String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX base: <"+baseURI+">\n";
//    File ontologyFile = new File("/home/cohend/yoda/yoda_ontology.owl");

    public Database() {
//        // non-inferencing triple store
//        repository = new SailRepository(new MemoryStore());
        // inferencing rdf database
        Repository repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
        try {
            repository.initialize();
            connection = repository.getConnection();
//            connection.add(ontologyFile, baseURI, RDFFormat.TURTLE); //load up some turtle file
        } catch (RepositoryException e) {
            e.printStackTrace();
        } /*catch (RDFParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /*
    * Insert all the classes and properties of an ontology into the database.
    * Insert all direct parent-child relationships (assume that the database does its own class hierarchy inference)
    * Insert rdfs:class and rdfs:property as required
    * */
    public void generateClassHierarchy(Set<Class> classes, Set<Class> properties)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {

        String insertString = prefixes+"INSERT DATA\n{\n";
        for (Class cls : classes){
            insertString += generateTriple(cls, "rdf:type", "rdfs:Class")+".\n";
            if (cls != edu.cmu.sv.ontology.object.Object.class && cls != Verb.class) {
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
        System.out.println("Creating ontology, insert string:\n" + insertString);
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
    public String generateTriple(Object subject, Object predicate, Object obj) {
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

    public void insertTriple(String subject, String predicate, String object)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {

        String updateString = prefixes+"INSERT DATA \n{ base:"+subject+" "+predicate+" base:"+object+" }";
//        System.out.println("attempting the following sparql update string:\n"+updateString);
        Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
        update.execute();
    }

    /*
    * Insert a data value to the triple store, return the unique identifier
    * */
    public String insertValue(Object obj) throws MalformedQueryException, RepositoryException, UpdateExecutionException {
        String newURI = "auto_generated_value_URI"+URICounter++;
        if (obj instanceof String){
            String updateString = prefixes+"INSERT DATA \n{ base:"+newURI+" rdf:value \""+obj+"\"^^xsd:string}";
//            System.out.println("Database.insertValue: updateString:"+updateString);
            Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
            update.execute();
        } else if (obj instanceof Integer){
            String updateString = prefixes+"INSERT DATA \n{ base:"+newURI+" rdf:value \""+obj+"\"^^xsd:int}";
//            System.out.println("Database.insertValue: updateString:"+updateString);
            Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
            update.execute();
        } else {
            throw new Error("Can't insertValue of that type");
        }
        return newURI;
    }

    /*
    * Run a sparql query on the database and return all values for the variable x
    * */
    public Set<String> runQuerySelectX(String queryString){
        Set<String> ans = new HashSet<>();
        try {
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();

            while (result.hasNext()){
                BindingSet bindings = result.next();
                ans.add(bindings.getValue("x").stringValue());
            }

        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public void outputEntireDatabase(){
        System.out.println("Outputting Entire Database");
        String queryString = "SELECT ?x ?y ?z WHERE {?x ?y ?z} ";
        try {
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();


            while (result.hasNext()){
                BindingSet bindings = result.next();
                Value valueOfX = bindings.getValue("x");
                Value valueOfY = bindings.getValue("y");
                Value valueOfZ = bindings.getValue("z");
                System.out.println(valueOfX.stringValue() + " " + valueOfY.stringValue() + " " + valueOfZ.stringValue());
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
            e.printStackTrace();
        }
    }

}

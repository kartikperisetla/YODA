package edu.cmu.sv.database;


import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 6/21/14.
 */
public class Database {

    Repository repository;
    RepositoryConnection connection;
    public static String baseURI = "http://sv.cmu.edu/yoda#";
//    File ontologyFile = new File("/home/cohend/yoda/yoda_ontology.owl");

    public Database() {
        repository = new SailRepository(new MemoryStore());
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



    public void insertTriple(String subject, String predicate, String object)
            throws MalformedQueryException, RepositoryException, UpdateExecutionException {
        String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX base: <"+baseURI+">\n";
        String updateString = prefixes+"INSERT DATA \n{ base:"+subject+" "+predicate+" base:"+object+" }";
//        System.out.println("attempting the following sparql update string:\n"+updateString);
        Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString, baseURI);
        update.execute();
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

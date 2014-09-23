package edu.cmu.sv.database;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.query.algebra.InsertData;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cohend on 6/21/14.
 */
public class Database {

    public static class Operation {
        String transactionType;
        Set<String> variables;
        Map<String, String> classConstraints;
        Set<ImmutableTriple<String, String, String>> triples;

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public Set<String> getVariables() {
            return variables;
        }

        public void setVariables(Set<String> variables) {
            this.variables = variables;
        }

        public Map<String, String> getClassConstraints() {
            return classConstraints;
        }

        public void setClassConstraints(Map<String, String> classConstraints) {
            this.classConstraints = classConstraints;
        }

        public Set<ImmutableTriple<String, String, String>> getTriples() {
            return triples;
        }

        public void setTriples(Set<ImmutableTriple<String, String, String>> triples) {
            this.triples = triples;
        }

        public Operation(String transactionType, Set<String> variables, Map<String, String> classConstraints, Set<ImmutableTriple<String, String, String>> triples) {
            this.transactionType = transactionType;
            this.variables = variables;
            this.classConstraints = classConstraints;
            this.triples = triples;
        }

        public String getSPARQL(){
            String ans = "";
            if (transactionType.equals("query")) {
                ans += "SELECT ";
                for (String v : variables){
                    ans += "?"+v+" ";
                }
                ans += "WHERE {";
            }
            for (String key: classConstraints.keySet()){
                ans += key+" rdf:type "+classConstraints.get(key)+" ";
            }
            for (ImmutableTriple triple : triples){
                ans += triple.getLeft() + " " + triple.getMiddle() + " " + triple.getRight() + " ";
            }
            ans += "}";
            return ans;
        }

    }


    Repository repository;
    RepositoryConnection connection;
    String baseURI = "http://sv.cmu.edu/yoda";
    File ontologyFile = new File("/home/cohend/yoda/yoda_ontology.owl");

    public Database() {
        repository = new SailRepository(new MemoryStore());
        try {
            repository.initialize();
            connection = repository.getConnection();
            connection.add(ontologyFile, baseURI, RDFFormat.TURTLE); //load up some turtle file
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        return ans;
    }

}

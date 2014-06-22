package edu.cmu.sv.database;

import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by cohend on 6/21/14.
 */
public class Database {

    Repository repository;
    RepositoryConnection connection;
    String baseURI = "http://sv.cmu.edu/yoda";
    File ontologyFile = new File("/home/cohend/yoda/demo.turtle");

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

    public void doSomething(){
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

        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
    }

}

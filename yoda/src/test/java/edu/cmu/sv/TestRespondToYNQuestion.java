package edu.cmu.sv;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_task.RespondToYNQuestionTask;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/21/14.
 *
 * Tests out RespondToYNQuestion:
 *  - start up new Dialogs and sample databases and ask the system YN questions, verify that it responds correctly
 *
 */
public class TestRespondToYNQuestion {

    @Test
    public void test() throws RepositoryException, MalformedQueryException, UpdateExecutionException {
        // Set up sample database
        Database db = new Database();
        Set<Class> databaseClasses = new HashSet<>(OntologyRegistry.objectClasses);
        databaseClasses.addAll(OntologyRegistry.verbClasses);
        Set<Class> databaseProperties = new HashSet<>(OntologyRegistry.roleClasses);
        databaseProperties.addAll(OntologyRegistry.roleClasses);
        db.generateClassHierarchy(databaseClasses, databaseProperties);
        addTestContent(db);
        db.outputEntireDatabase();

        // example 0
        SemanticsModel taskSpec = new SemanticsModel();
        taskSpec.getSlots().put("dialogAct", "YNQuestion");
        taskSpec.getSlots().put("verb", "Exist");
        taskSpec.getSlots().put("Patient", "X");
        SemanticsModel child = new SemanticsModel();
        taskSpec.getChildren().put("X", child);
        child.getSlots().put("class", "Meeting");

        RespondToYNQuestionTask task = new RespondToYNQuestionTask(db);
        task.setTaskSpec(taskSpec);
        task.execute();


        // example 1
        taskSpec = new SemanticsModel();
        taskSpec.getSlots().put("dialogAct", "YNQuestion");
        taskSpec.getSlots().put("verb", "Create");

        taskSpec.getSlots().put("Agent", "Y");
        child = new SemanticsModel();
        taskSpec.getChildren().put("Y", child);
        child.getSlots().put("class", "Person");

        taskSpec.getSlots().put("Patient", "X");
        child = new SemanticsModel();
        taskSpec.getChildren().put("X", child);
        child.getSlots().put("class", "Meeting");

        task = new RespondToYNQuestionTask(db);
        task.setTaskSpec(taskSpec);
        task.execute();

    }

    public void addTestContent(Database db){
        try {
            db.insertTriple("meeting0000", "rdf:type", "Meeting");
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }
    }

}

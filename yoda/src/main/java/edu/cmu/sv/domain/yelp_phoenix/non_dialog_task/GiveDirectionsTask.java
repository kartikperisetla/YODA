package edu.cmu.sv.domain.yelp_phoenix.non_dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by David Cohen on 12/19/14.
 */
public class GiveDirectionsTask extends NonDialogTask {
    private static Integer instanceCounter = 0;
    private static Map<String, TaskStatus> executionStatus = new HashMap<>();
    private static NonDialogTaskPreferences preferences =
            new NonDialogTaskPreferences(false, 1, 20, 15,
                    new HashSet<>(Arrays.asList()));

    @Override
    public NonDialogTaskPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void execute(YodaEnvironment yodaEnvironment) {
        super.execute(yodaEnvironment);
        String destinationUri = (String) new SemanticsModel(taskSpec.toJSONString()).newGetSlotPathFiller("Destination.HasURI");
        String destinationName = null;
        String gps_lat = null;
        String gps_lon = null;
        String queryString = Database.prefixes + "SELECT DISTINCT ?name ?gps_lat ?gps_lon WHERE {\n";
        queryString += "<" + destinationUri + "> rdfs:label ?name .\n";
        queryString += "<" + destinationUri + "> base:gps_lat ?gps_lat .\n";
        queryString += "<" + destinationUri + "> base:gps_lon ?gps_lon .\n";
        queryString += "}";

        synchronized (yodaEnvironment.db.connection) {
            try {
                TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString, Database.baseURI);
                TupleQueryResult result = query.evaluate();

                if (result.hasNext()){
                    BindingSet bindings = result.next();
                    destinationName = bindings.getValue("name").stringValue();
                    gps_lat = bindings.getValue("gps_lat").stringValue();
                    gps_lon = bindings.getValue("gps_lon").stringValue();
                    result.close();
                }
                else {
                    // answer is unknown / question doesn't make sense
//                    Database.getLogger().info("Description match result is unknown / question doesn't make sense: "+ans);
                }
            } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        JSONObject content = new JSONObject();
        content.put("destinationName", destinationName);
        content.put("gps_lat", gps_lat);
        content.put("gps_lon", gps_lon);
        content.put("mode", "");
        taskSpec = content;
    }



    @Override
    public double assessExecutability() {
        return 0.0;
    }

    @Override
    public JSONObject getTaskSpec() {
        return taskSpec;
    }

    @Override
    public TaskStatus status(String taskID) {
        return executionStatus.get(taskID);
    }
}

package edu.cmu.sv.yoda_environment;

import com.mongodb.*;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by David Cohen on 1/9/15.
 */
public class MongoLogHandler extends Handler {
    static MongoClient mongoClient;
    static DB db;
    static DBCollection coll;
    public static void start(){
        try {
            MongoClientURI uri = new MongoClientURI(System.getenv().get("YODA_MONGO_LOG_CONNECTION_STRING"));
            mongoClient = new MongoClient(uri);
            db = mongoClient.getDB("scotty_logs");
            coll = db.getCollection("yoda_logs");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new Error();
        }
    }

    @Override
    public void publish(LogRecord record) {
        try {
            // test that the record is a valid json object
//        System.out.println("record message:"+record.getMessage());
            JSONObject recordAsJSON = SemanticsModel.parseJSON(record.getMessage());
            JSONObject augmentedRecordObject = SemanticsModel.parseJSON("{}");

            // add metadata
//        augmentedRecordObject.put("time", new Date(record.getMillis()).toString());
            augmentedRecordObject.put("logger_name", record.getLoggerName());
            augmentedRecordObject.put("source_class", record.getSourceClassName());
            augmentedRecordObject.put("record", recordAsJSON);

            // write to mongo
            BasicDBObject dbObject = (BasicDBObject) com.mongodb.util.JSON.parse(augmentedRecordObject.toJSONString());
            dbObject.put("time", new Date(record.getMillis()));
            coll.insert(dbObject);
        } catch (Error error){
            if (!error.getMessage().startsWith("failed to parse json string:"))
                throw new Error("error while logging");
        } catch (Exception e){
            e.printStackTrace();
            throw new Error("...");
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    public static JSONObject createEventRecord(String eventType){
        return SemanticsModel.parseJSON("{\"eventType\":\""+eventType+"\"}");
    }

}

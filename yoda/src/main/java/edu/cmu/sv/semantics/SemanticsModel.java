package edu.cmu.sv.semantics;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.lang.Object;
import java.util.*;

/**
 * Created by David Cohen on 8/27/14.
 *
 * A hierarchical slot-filling model.
 * Used to represent utterance / discourse unit meaning.
 * Uses a JSON object as the internal representation.
 *
 */
public class SemanticsModel {
    public static JSONParser parser = new JSONParser();
    JSONObject internalRepresentation;

    public SemanticsModel(String jsonSource) throws ParseException {
        internalRepresentation = (JSONObject)parser.parse(jsonSource);
    }

    public SemanticsModel(){
        internalRepresentation = new JSONObject();
    }

    public SemanticsModel deepCopy(){
        SemanticsModel ans = new SemanticsModel();
        ans.internalRepresentation = new JSONObject(internalRepresentation);
        return ans;
    }

    public Object extendAndOverwrite(Object obj1, Object obj2){
        if (obj1 instanceof JSONObject){}
        if (obj1 instanceof JSONArray){}
        return null;
//        JSONObject ans = new JSONObject(obj1);
//        for (Object key: obj2.keySet()){
//            if (ans.containsKey(key)){
//
//            }else{
//                ans.put(key, obj2.get(key));
//            }
//        }
//        return ans;
    }

    /*
    * Extend this model with other, overwriting values as necessary
    * */
    public void extendAndOverwrite(SemanticsModel other){
//        for (Object key : other.internalRepresentation.keySet()){
//            if (internalRepresentation.containsKey(key)){
//
//            }
//        }
    }

    /*
    * Recursive helper for the public method
    * */
    private Object getSlotPathFillerHelper(Object startingPoint, String slotPath){
        if (slotPath.equals(""))
            return startingPoint;
        String[] fillerPath = slotPath.split("\\.");
        String thisFiller = fillerPath[0];
        List<String> remainingFillers = new LinkedList<>(Arrays.asList(fillerPath));
        remainingFillers.remove(0);
        String remainingSlotPath = String.join(".", remainingFillers);
        if (!(startingPoint instanceof JSONObject))
            return null;
        if (((JSONObject) startingPoint).containsKey("class") &&
                (((JSONObject) startingPoint).get("class").equals("Or") ||
                ((JSONObject) startingPoint).get("class").equals("And"))){
            JSONObject ans = new JSONObject();
            JSONArray ansArray = new JSONArray();
            ans.put("class", ((JSONObject) startingPoint).get("class"));
            JSONArray nestedArray = (JSONArray) ((JSONObject) startingPoint).get("Values");
            for (Object child : nestedArray){
                ansArray.add(getSlotPathFillerHelper(child, slotPath));
            }
            ans.put("Values", ansArray);
            return ans;
        } else {
            return getSlotPathFillerHelper(((JSONObject) startingPoint).get(thisFiller), remainingSlotPath);
        }
    }

    /*
    * Calls a helper, which returns:
    *
    * 1) A JSONObject directly copied from this if slotPath path refers to a non-ambiguous non-leaf node
    * 2) A String if slotPath refers to a non-ambiguous leaf node
    * 3) An OR node if the level of ambiguity closest to the root is an OR ambiguity
    * 4) Same as above, replace OR with AND
    * 5) null if the path is invalid
    *
    * */
    public String getSlotPathFiller(String slotPath){
        return getSlotPathFillerHelper(internalRepresentation, slotPath).toString();
    }


//    public Map<String, String> getAllSlotFillerPairs(){
//        Map<String, String> ans = new HashMap<>();
//        // collect top level slots/fillers
//        // (exclude non-leaf pairs and special values (marked by surrounding arrow brackets))
//        for (String slot : slots.keySet()){
//            if (!children.containsKey(slots.get(slot)) && !slots.get(slot).matches("\\<.*\\>"))
//                ans.put(slot, slots.get(slot));
//        }
//        // collect recursively
//        for (String slot : slots.keySet()){
//            if (!children.containsKey(slots.get(slot)))
//                continue;
//            Map<String, String> childSlotFillers = children.get(slots.get(slot)).getAllNonSpecialSlotFillerLeafPairs();
//            for (String key : childSlotFillers.keySet()){
//                ans.put(slot+"."+key, childSlotFillers.get(key));
//            }
//        }
//        return ans;
//    }

    public Map<String, String> getAllSlotFillerPairs(){
        return null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SemanticsModel))
            return false;
        return ((SemanticsModel) obj).internalRepresentation.equals(internalRepresentation);
    }

    public Map<String, String> getSlots(){return null;}
    public Map<String, SemanticsModel> getChildren(){return null;}

    @Override
    public String toString() {
        return "SemanticsModel:\n"+internalRepresentation.toJSONString();
    }
}

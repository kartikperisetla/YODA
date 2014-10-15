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

    public static Object extendAndOverwrite(SemanticsModel obj1, SemanticsModel obj2){
        return extendAndOverwrite(obj1.internalRepresentation, obj2.internalRepresentation);
    }

    /*
    * (extend because original content should remain as long as it doesn't conflict with the new content)
    * Copy the contents of other on top of what is already in initial.
    * Overwrite slots that conflict.
    * The class UnknownThingWithRoles does not overwrite any other class,
    * any other class overwrites UnknownThingWithRoles without considering it to be a conflict.
    *
    * This calls itself on children so that current nested content isn't erased unless there is a conflict.
    * */
    public static Object extendAndOverwrite(JSONObject initial, JSONObject other){
        if (other.containsKey("class") && (other.get("class").equals("And") || other.get("class").equals("Or"))){
            System.out.println("Not Yet Implemented: SemanticsModel.extendAndOverwrite with conjunctions in other JSON object");
        }
        if (initial.containsKey("class") && (initial.get("class").equals("And") || initial.get("class").equals("Or"))){
            System.out.println("Not Yet Implemented: SemanticsModel.extendAndOverwrite with conjunctions in initial JSON object");
        }
        if (other.containsKey("class") && !other.get("class").equals("UnknownThingWithRoles")){
            initial.put("class", other.get("class"));
        }
        for (Object key : other.keySet()){
            if ("class".equals(key))
                continue;
            if (!initial.containsKey(key)){
                initial.put(key, other.get(key));
            } else {
                if (other.get(key) instanceof String){
                    initial.put(key, other.get(key));
                } else if ((initial.get(key) instanceof JSONObject) &&
                        (other.get(key) instanceof JSONObject)){
                    extendAndOverwrite(((JSONObject)initial.get(key)), ((JSONObject)other.get(key)));
                }
            }
        }
        return initial;
    }

    void insertAndOverwriteAtPointHelper(String slotPath, Object currentPoint, Object insertionContent){

    }

    /*
    * insert other and overwrite what is there currently at the point specified by slotPath
    *
    * If slotPath filler is null, throw an error
    *
    * If slotPath filler is a single point, insert and overwrite at that point
    *
    * If slotPath filler is a conjunction, insert and overwrite at all the points
    *
    * */
    public void insertAndOverwriteAtPoint(String slotPath, SemanticsModel other){

    }


    /*
    * Recursive helper for the public method
    * */
    //TODO: deal with null after conjunctions
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
        Object ans = getSlotPathFillerHelper(internalRepresentation, slotPath);
        return (ans==null)? null : ans.toString();
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

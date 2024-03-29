package edu.cmu.sv.semantics;


import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 8/27/14.
 *
 * A hierarchical slot-filling model.
 * Used to represent utterance / discourse unit meaning.
 * Uses a JSON object as the internal representation.
 *
 */
public class SemanticsModel {
    private static final JSONParser parser = new JSONParser();
    JSONObject internalRepresentation;

    public SemanticsModel(String jsonSource) {
        internalRepresentation = parseJSON(jsonSource);
    }

    public SemanticsModel(JSONObject internalRepresentation){this.internalRepresentation = internalRepresentation;}

    public SemanticsModel(){
        internalRepresentation = new JSONObject();
    }

    public JSONObject getInternalRepresentation() {
        return internalRepresentation;
    }

    public SemanticsModel deepCopy(){
        SemanticsModel ans = new SemanticsModel();
        ans.internalRepresentation = parseJSON(internalRepresentation.toJSONString());
        return ans;
    }

    public void extendAndOverwrite(SemanticsModel other){
        extendAndOverwriteHelper(internalRepresentation, other.internalRepresentation);
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
    void extendAndOverwriteHelper(JSONObject initial, final JSONObject other){
        if (other.containsKey("class") && (other.get("class").equals("And") || other.get("class").equals("Or"))){
            throw new Error("Not Yet Implemented: SemanticsModel.extendAndOverwriteHelper with conjunctions in other JSON object");
        }
        if (initial.containsKey("class") && (initial.get("class").equals("And") || initial.get("class").equals("Or"))){
            throw new Error("Not Yet Implemented: SemanticsModel.extendAndOverwriteHelper with conjunctions in internal JSON object");
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
                    extendAndOverwriteHelper(((JSONObject) initial.get(key)), ((JSONObject) other.get(key)));
                }
            }
        }
    }

    void extendAndOverwriteAtPointHelper(String slotPath, Object currentPoint, JSONObject insertionContent){
        if (currentPoint==null){
            throw new Error("Can not extend null");
        } else if (currentPoint instanceof String){
            throw new Error("Can not extend a String");
        } else if (currentPoint instanceof JSONObject){

            if (slotPath.equals("")) {
                extendAndOverwriteHelper((JSONObject)currentPoint, insertionContent);
                return;
            }
            String[] fillerPath = slotPath.split("\\.");
            String thisFiller = fillerPath[0];
            List<String> remainingFillers = new LinkedList<>(Arrays.asList(fillerPath));
            remainingFillers.remove(0);
            String remainingSlotPath = String.join(".", remainingFillers);

            if (((JSONObject) currentPoint).containsKey("class") &&
                    (((JSONObject) currentPoint).get("class").equals("Or") ||
                            ((JSONObject) currentPoint).get("class").equals("And"))){
                JSONArray nestedArray = (JSONArray) ((JSONObject) currentPoint).get("HasValues");
                for (Object child : nestedArray){
                    extendAndOverwriteAtPointHelper(slotPath, child, insertionContent);
                }
            } else {
                extendAndOverwriteAtPointHelper(remainingSlotPath, ((JSONObject) currentPoint).get(thisFiller),
                        insertionContent);
            }
        } else {
            assert false;
        }
    }

    public void filterOutLeafSlot(String slot){
        Set<String> internalPaths = getAllInternalNodePaths();
        for (String path: internalPaths){
            JSONObject node = (JSONObject)newGetSlotPathFiller(path);
            if (node.containsKey(slot))
                node.remove(slot);
        }
    }

    /*
    * Extend other and overwrite what is there currently at the point specified by slotPath
    *
    * If slotPath filler is null or a String, throw an error
    * If slotPath filler is a single point, insert and overwrite at that point
    * If slotPath filler is a conjunction, insert and overwrite at all the points
    *
    * */
    public void extendAndOverwriteAtPoint(String slotPath, SemanticsModel other){
        extendAndOverwriteAtPointHelper(slotPath, internalRepresentation, other.internalRepresentation);
    }


    /*
    * Returns:
    *
    * 1) A JSONObject directly copied from this if slotPath path refers to a non-ambiguous non-leaf node
    * 2) A String if slotPath refers to a non-ambiguous leaf node
    * 3) An OR node if the level of ambiguity closest to the root is an OR ambiguity
    * 4) Same as above, replace OR with AND
    * 5) null if the path is invalid
    *
    * */
    //TODO: deal with null after conjunctions
    private static Object getSlotPathFillerHelper(Object startingPoint, String slotPath){
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
            JSONArray nestedArray = (JSONArray) ((JSONObject) startingPoint).get("HasValues");
            for (Object child : nestedArray){
                ansArray.add(getSlotPathFillerHelper(child, slotPath));
            }
            ans.put("HasValues", ansArray);
            return ans;
        } else {
            return getSlotPathFillerHelper(((JSONObject) startingPoint).get(thisFiller), remainingSlotPath);
        }
    }

    /*
    * Returns a String, null, or a JSONObject
    * */
    public Object newGetSlotPathFiller(String slotPath){
        Object ans = getSlotPathFillerHelper(internalRepresentation, slotPath);
        return ans;
    }

    public static void putAtPath(JSONObject source, String slotPath, Object insertContent){
        if (!slotPath.contains("."))
            source.put(slotPath, insertContent);
        else {
            String[] fillerPath = slotPath.split("\\.");
            String thisFiller = fillerPath[0];
            List<String> remainingFillers = new LinkedList<>(Arrays.asList(fillerPath));
            remainingFillers.remove(0);
            String remainingSlotPath = String.join(".", remainingFillers);
            putAtPath((JSONObject) source.get(thisFiller), remainingSlotPath, insertContent);
        }

    }

    public Set<Object> getSlotsAtPath(String slotPath){
        Object ans = getSlotPathFillerHelper(internalRepresentation, slotPath);
        if (ans==null)
            return null;
        if (ans instanceof JSONObject)
            return ((JSONObject) ans).keySet();
        return null;
    }

    public String getSlotPathFiller(String slotPath){
        Object ans = getSlotPathFillerHelper(internalRepresentation, slotPath);
        return (ans==null)? null : ans.toString();
    }

    // TODO: deal with conjunctions
    private Set<String> getAllNodePathsHelper(JSONObject currentPoint, boolean internalPathsOnly){
        Set<String> ans = new HashSet<>();
        for (Object key : currentPoint.keySet()) {
            if (currentPoint.get(key) instanceof JSONObject) {
                ans.add((String) key);
                for (String childAns : getAllNodePathsHelper((JSONObject) currentPoint.get(key), internalPathsOnly)) {
                    ans.add(((String) key) + "." + childAns);
                }
            }
            if (!internalPathsOnly){
                ans.add((String) key);
            }
        }
        return ans;
    }

    /*
    * turn: {class: X, ...}
    * into
    * {class: wrapperClass, wrappingRole: {class: X, ...}}
    *
    * modifies the source object in place
    * */
    public static void wrap(JSONObject source, String wrapperClass, String wrappingRole){
        JSONObject tmp = new JSONObject();
        List<Object> keyList = new LinkedList<Object>(source.keySet());
        for (Object key: keyList){
            tmp.put(key, source.get(key));
            source.remove(key);
        }
        source.put("class", wrapperClass);
        source.put(wrappingRole, tmp);
    }

    /*
    * turn: {class: X, wrappingRole: {ABC}}
    * into
    * {ABC}
    *
    * does not matter what the wrapping class is
    * modifies the source object in place
    * does not delete the nested JSONObject or remove its contents, just copies them to the new object
    * */
    public static void unwrap(JSONObject source, String wrappingRole){
        Object nested = source.get(wrappingRole);
        List<Object> keyList = new LinkedList<Object>(source.keySet());
        for (Object key: keyList){
            source.remove(key);
        }
        for (Object key: ((JSONObject)nested).keySet()){
            source.put(key, ((JSONObject) nested).get(key));
        }
    }

    /*
    * overwrite the source object in place with the replacement content
    * */
    public static void overwrite(JSONObject source, JSONObject replacement){
//        Set<Object> existingKeys = new HashSet<>(source.keySet());
//        for (Object o : existingKeys){
//            source.remove(o);
//        }
        source.clear();
        for (Object o : replacement.keySet())
            source.put(o, replacement.get(o));
    }

    /*
    * Find all the slot paths who are filled by an entity description of class clsName
    * */
    public Set<String> findAllPathsToClass(String clsName){
        return getAllInternalNodePaths().stream().
                filter(x -> ((JSONObject)newGetSlotPathFiller(x)).containsKey("class") &&
                        ((JSONObject)newGetSlotPathFiller(x)).get("class").equals(clsName)).
                collect(Collectors.toSet());
    }

    /*
    * Return all the slot paths that point to JSONObjects
    * */
    public Set<String> getAllInternalNodePaths(){
        Set<String> ans = getAllNodePathsHelper(internalRepresentation, true);
        ans.add("");
        return ans;
    }

    public Set<String> getAllNodePaths(){
        Set<String> ans = getAllNodePathsHelper(internalRepresentation, false);
        ans.add("");
        return ans;
    }


    /*
    * Throws an error if the SLU hypothesis model is invalid
    * according to the registered ontology
    * */
    public void validateSLUHypothesis() {
        // there must be a dialog act
        if (getSlotPathFiller("dialogAct")==null)
            throw new Error("no dialog act in SLU model");
        if (!DialogRegistry.dialogActNameMap.containsKey(getSlotPathFiller("dialogAct")))
            throw new Error("this dialog act isn't in the dialog registry:" + getSlotPathFiller("dialogAct"));
        // there is a specific set of permitted slots in the top level
        if (getSlotsAtPath("").stream().
                anyMatch(x -> !x.equals("dialogAct") && !x.equals("verb") && !x.equals("topic")))
            throw new Error("the top level contains unpermitted slots");

        // check all children
        for (Object child : internalRepresentation.values()){
            try {
                if (!(child instanceof JSONObject))
                    continue;
                validateThingDescription((JSONObject)child);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * Recursively check that all the slots in a node are valid for the class of that node
    * (does not check that the fillers are in the range for the role which corresponds to that slot)
    * */
    public static void validateThingDescription(JSONObject description) throws IllegalAccessException, InstantiationException {
        String clsString = (String) getSlotPathFillerHelper(description, "class");
        if (clsString==null)
            throw new Error("thing description missing class slot: "+description);
        Object cls = Ontology.thingNameMap.get(clsString);
        // check that all slots correspond to roles which this node's class is in the domain of
        for (Object slot : description.keySet()){
            if (slot.equals("class") || slot.equals("refType"))
                continue;
            boolean inDomain = false;
            if (!Ontology.roleNameMap.containsKey(slot))
                throw new Error("thing description's class not in registry: "+slot+","+description);
            for (Object domainMember : Ontology.roleNameMap.get(slot).getDomain()){
//                if (domainMember.isAssignableFrom(cls)) {
//                    inDomain = true;
//                    break;
//                }
            }
//            if (!inDomain)
//                throw new Error("Class is not in slot's domain: "+description+", "+slot);
        }

        // check all children
        for (Object child : description.values()){
            if (!(child instanceof JSONObject))
                continue;
            validateThingDescription((JSONObject)child);
        }
    }

    public static JSONObject parseJSON(String jsonString) {
        try {
            synchronized (parser) {
                Object result = parser.parse(jsonString);
                return (JSONObject) result;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Error("failed to parse json string: input string:" + jsonString);
        }
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

    public Map<String, SemanticsModel> getChildren(){return null;}

    public static String extractChunk(JSONObject structure, String inputString, String slotPath){
        List<String> tokens = Tokenizer.tokenize(inputString);
        String currentString = inputString;
        if (structure.containsKey("chunk-start")){
            List<String> tokensInChunk = new LinkedList<>();
            Integer start;
            Integer end;
            if (structure.get("chunk-start") instanceof Long){
                start = (int) (long) structure.get("chunk-start");
            } else
                start = (Integer)structure.get("chunk-start");
            if (structure.get("chunk-end") instanceof Long){
                end = (int) (long) structure.get("chunk-end");
            } else
                end = (Integer) structure.get("chunk-end");
            
            for (Integer i = start; i <= end; i++) {
                tokensInChunk.add(tokens.get(i));
            }
            currentString = String.join(" ", tokensInChunk);
        }

        if (slotPath.equals("")){
            return currentString;
        }

        String[] fillerPath = slotPath.split("\\.");
        String thisFiller = fillerPath[0];
        List<String> remainingFillers = new LinkedList<>(Arrays.asList(fillerPath));
        remainingFillers.remove(0);
        String remainingSlotPath = String.join(".", remainingFillers);
        return extractChunk((JSONObject)structure.get(thisFiller), currentString, remainingSlotPath);
    }

    /*
    * Return whether the two Semantic models objects contain identical content
    * TODO: This function is completely broken, it compares JSON objects in an unreasonable way.
    * TODO: use contentEquivalenceComparisonAndReport instead
    * */
    public static boolean contentEqual(SemanticsModel sm1, SemanticsModel sm2){
        Set<String> internalPaths = sm1.getAllInternalNodePaths();
//        System.err.println("SM.contentQual: internalPaths:" + internalPaths);
        internalPaths.addAll(sm2.getAllInternalNodePaths());
        for (String path : internalPaths){
            if (!sm1.newGetSlotPathFiller(path).equals(sm2.newGetSlotPathFiller(path)))
                return false;
        }
        return true;
    }

    public static Pair<Boolean, String> contentEquivalenceComparisonAndReport(SemanticsModel sm1, SemanticsModel sm2){
        Set<String> internalPaths = sm1.getAllNodePaths();
        internalPaths.addAll(sm2.getAllNodePaths());
//        System.err.println("SM.contentEqualIncludingLeaves: internalPaths:" + internalPaths);

        for (String path : internalPaths){
            String report = "";
            report += "path:" + path + "\n";
            report += "sm1:" + sm1.newGetSlotPathFiller(path) + "\n";
            report += "sm2:" + sm2.newGetSlotPathFiller(path);
//            System.err.println(report);

            // if the path is listed, one of the SM's must have had a non-null value
            if (sm1.newGetSlotPathFiller(path)==null || sm2.newGetSlotPathFiller(path)==null)
                return new ImmutablePair<>(false, "=== SM Comparison Error Report ===\n"+ report);

            if (sm1.newGetSlotPathFiller(path) instanceof JSONObject &&
                    sm2.newGetSlotPathFiller(path) instanceof JSONObject)
                continue;

            if (!sm1.newGetSlotPathFiller(path).equals(sm2.newGetSlotPathFiller(path)))
                return new ImmutablePair<>(false, "=== SM Comparison Error Report ===\n"+ report);
        }
        return new ImmutablePair<>(true, null);
    }

    @Override
    public String toString() {
        return "SemanticsModel:\n"+internalRepresentation.toJSONString();
    }


}

package edu.cmu.sv.semantics;


import com.google.common.collect.Iterables;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.lang.Object;
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
    public static JSONParser parser = new JSONParser();
    JSONObject internalRepresentation;

    public SemanticsModel(String jsonSource) {
        try {
            internalRepresentation = (JSONObject)parser.parse(jsonSource);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Error("jsonSource is invalid");
        }
    }

    public SemanticsModel(JSONObject internalRepresentation){this.internalRepresentation = internalRepresentation;}

    public SemanticsModel(){
        internalRepresentation = new JSONObject();
    }

    public JSONObject getInternalRepresentation() {
        return internalRepresentation;
    }


    /*
    * Detect whether there will be any new content added while extending source with insertionContent
    * */
    public static boolean anyNewSenseInformation(JSONObject source, JSONObject insertionContent){
        if (source.keySet().isEmpty())
            return true;
        if (insertionContent.keySet().isEmpty())
            return false;
        // check for class compatibility
        if (source.get("class")==null || insertionContent.get("class")==null)
            throw new Error("one of these does not have a class!"+source + insertionContent);
        Class sourceClass = OntologyRegistry.thingNameMap.get(source.get("class"));
        Class insertionClass = OntologyRegistry.thingNameMap.get(insertionContent.get("class"));
        if (sourceClass==null || insertionClass==null){
            System.out.println("one of these classes is missing from thingNameMap: "+source.get("class")+", "+insertionContent.get("class"));
        }

        // two web resources always add new content, but not sense content
        if (sourceClass.equals(WebResource.class) && insertionClass.equals(WebResource.class))
            return false;

        // the insertionContent may not be more specific that the source content
        if (!(insertionClass.isAssignableFrom(sourceClass) ||
                insertionClass.equals(UnknownThingWithRoles.class)))
            return true;

        // check recursively for other role compatibility
        for (Object key : insertionContent.keySet()){
            if (key.equals("class")){
                continue;
            } else {
                if (source.containsKey(key)){
                    if (source.get(key) instanceof String &&
                            !(source.get(key).equals(insertionContent.get(key)))) {
                        return true;
                    } else if (insertionContent.get(key) instanceof String &&
                            !(insertionContent.get(key).equals(source.get(key)))) {
                        return true;
                    } else if (anyNewSenseInformation((JSONObject) source.get(key), (JSONObject) insertionContent.get(key))){
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    * Detect whether there will be any conflicts while extending source with insertionContent
    * Does NOT check that the resulting object will be valid
    * */
    public static boolean anySenseConflicts(JSONObject source, JSONObject insertionContent){
        if (source.keySet().isEmpty())
            return true;
        if (insertionContent.keySet().isEmpty())
            return false;
        // check for class compatibility
        if (source.get("class")==null || insertionContent.get("class")==null)
            throw new Error("one of these does not have a class!" + source + insertionContent);
        Class sourceClass = OntologyRegistry.thingNameMap.get(source.get("class"));
        Class insertionClass = OntologyRegistry.thingNameMap.get(insertionContent.get("class"));
        if (sourceClass==null || insertionClass==null){
            System.out.println("one of these classes is missing from thingNameMap: "+source.get("class")+", "+insertionContent.get("class"));
        }

        // two web resources can not cause sense conflicts, only denotation conflicts
        if (sourceClass.equals(WebResource.class) && insertionClass.equals(WebResource.class))
            return false;

        if (!(sourceClass.isAssignableFrom(insertionClass) ||
                insertionClass.isAssignableFrom(sourceClass) ||
                sourceClass.equals(UnknownThingWithRoles.class) ||
                insertionClass.equals(UnknownThingWithRoles.class)))
            return true;

        // check recursively for other role compatibility
        for (Object key : insertionContent.keySet()){
            if (key.equals("class")){
                continue;
            } else {
                if (source.containsKey(key)){
                    if (source.get(key) instanceof String &&
                            !(source.get(key).equals(insertionContent.get(key)))) {
                        return true;
                    } else if (insertionContent.get(key) instanceof String &&
                            !(insertionContent.get(key).equals(source.get(key)))) {
                        return true;
                    } else if (anySenseConflicts((JSONObject) source.get(key), (JSONObject) insertionContent.get(key))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean anySLUTopLevelConflicts(SemanticsModel filter, SemanticsModel testCase){
        try {
            filter.validateSLUHypothesis();
            testCase.validateSLUHypothesis();
        } catch (Error e){
            System.out.println("SM.anySLUTopLevelConflicts: invalid filter or test case");
            return true;
        }
        if (!testCase.newGetSlotPathFiller("dialogAct").equals(filter.newGetSlotPathFiller("dialogAct")))
            return true;

        if (filter.newGetSlotPathFiller("topic")!=null){
            if (testCase.newGetSlotPathFiller("topic")==null)
                return true;
            if (anySenseConflicts((JSONObject) testCase.newGetSlotPathFiller("topic"),
                    (JSONObject) filter.newGetSlotPathFiller("topic")))
                return true;
        }

        if (filter.newGetSlotPathFiller("verb")!=null){
            if (testCase.newGetSlotPathFiller("verb")==null)
                return true;
            if (anySenseConflicts((JSONObject) testCase.newGetSlotPathFiller("verb"),
                    (JSONObject) filter.newGetSlotPathFiller("verb")))
                return true;
        }

        return false;
    }

    public SemanticsModel deepCopy(){
        SemanticsModel ans = new SemanticsModel();
        try {
            ans.internalRepresentation = (JSONObject) parser.parse(internalRepresentation.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Error("failed to create a json object from an existing json object"+internalRepresentation);
        }
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
    private Set<String> getAllInternalNodePathsHelper(JSONObject currentPoint){
        Set<String> ans = new HashSet<>();
        for (Object key : currentPoint.keySet()) {
            if (currentPoint.get(key) instanceof JSONObject){
                ans.add((String) key);
                for (String childAns : getAllInternalNodePathsHelper((JSONObject) currentPoint.get(key))){
                    ans.add(((String)key)+"."+childAns);
                }
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
    * return the set of paths pointing to nodes that don't conflict with the filler
    * */
    public Set<String> findAllPathsToNonConflict(JSONObject filter){
        return getAllInternalNodePaths().stream().
                filter(x -> !anySenseConflicts((JSONObject) newGetSlotPathFiller(x), filter)).
                collect(Collectors.toSet());
    }


    /*
    * Return all the slot paths that point to JSONObjects
    * */
    public Set<String> getAllInternalNodePaths(){
        Set<String> ans = getAllInternalNodePathsHelper(internalRepresentation);
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
        Class<? extends Thing> cls = OntologyRegistry.thingNameMap.get(clsString);
        // check that all slots correspond to roles which this node's class is in the domain of
        for (Object slot : description.keySet()){
            if (slot.equals("class"))
                continue;
            boolean inDomain = false;
            if (!OntologyRegistry.roleNameMap.containsKey((String)slot))
                throw new Error("thing description's class not in registry: "+description);
            for (Class<? extends Thing> domainMember : OntologyRegistry.roleNameMap.get(slot).newInstance().getDomain()){
                if (domainMember.isAssignableFrom(cls)) {
                    inDomain = true;
                    break;
                }
            }
            if (!inDomain)
                throw new Error("Class is not in slot's domain: "+description+", "+slot);
        }

        // check all children
        for (Object child : description.values()){
            if (!(child instanceof JSONObject))
                continue;
            validateThingDescription((JSONObject)child);
        }
    }

    public static JSONObject parseJSON(String jsonString){
        try {
            return (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Error("failed to parse json string:"+jsonString);
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
            
            for (Integer i = start; i < end; i++) {
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


    public static Pair<Integer, Integer> getChunkingIndices(JSONObject sourceObject) {
        if (sourceObject.containsKey("chunk-start")) {

            Integer start;
            if (sourceObject.get("chunk-start") instanceof Long) {
                start = (int) (long) sourceObject.get("chunk-start");
            } else
                start = (Integer) sourceObject.get("chunk-start");
            Integer end;
            if (sourceObject.get("chunk-end") instanceof Long) {
                end = (int) (long) sourceObject.get("chunk-end");
            } else
                end = (Integer) sourceObject.get("chunk-end");
            return new ImmutablePair<>(start, end);
        }
        return null;
    }

    /*
    * Return whether the two Semantic models objects contain identical content
    * */
    public static boolean contentEqual(SemanticsModel sm1, SemanticsModel sm2){
        Set<String> internalPaths = sm1.getAllInternalNodePaths();
        internalPaths.addAll(sm2.getAllInternalNodePaths());
        for (String path : internalPaths){
            if (!sm1.newGetSlotPathFiller(path).equals(sm2.newGetSlotPathFiller(path)))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SemanticsModel:\n"+internalRepresentation.toJSONString();
    }


}

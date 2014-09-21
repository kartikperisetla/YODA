package edu.cmu.sv.database;

/**
 * Created by cohend on 9/20/14.
 */
public class DatabaseInterfaceDefinition {
    // if isProperty, this class will show up as a predicate in the KB
    // otherwise, it will be a subject or object
    boolean isProperty;
    boolean isClass;
    String identifier;

}

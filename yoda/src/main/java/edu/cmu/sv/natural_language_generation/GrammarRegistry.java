package edu.cmu.sv.natural_language_generation;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 */
public class GrammarRegistry {
    public Set<Class<? extends Template>> definiteReferenceTemplates = new HashSet<>();
    public Set<Class<? extends Template>> hasRoleFillerTemplates = new HashSet<>();
}

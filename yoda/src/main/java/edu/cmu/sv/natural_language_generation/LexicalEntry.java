package edu.cmu.sv.natural_language_generation;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 *
 * LexicalEntry instances store a set of closely related words
 * used to describe a single concept from the ontology
 *
 */
public class LexicalEntry {
    // nouns
    public Set<String> singularNounForms = new HashSet<>();
    public Set<String> pluralNounForms = new HashSet<>();
    public Set<String> massNounForms = new HashSet<>();
    public Set<String> standardNames = new HashSet<>();
    public Set<String> nicknames = new HashSet<>();

    // verbs
    public Set<String> presentSingularVerbs = new HashSet<>();
    public Set<String> presentPluralVerbs = new HashSet<>();

    // adjectives
    public Set<String> adjectives = new HashSet<>();



}

package edu.cmu.sv.domain;

import edu.cmu.sv.natural_language_generation.Lexicon;

/**
 * Created by David Cohen on 3/3/15.
 */
public class DomainSpec {
    String domainName;
    Lexicon lexicon;
    OntologyRegistry ontologyRegistry;
    NonDialogTaskRegistry nonDialogTaskRegistry;
    DatabaseRegistry databaseRegistry;

    public DomainSpec(String domainName, Lexicon lexicon, OntologyRegistry ontologyRegistry, NonDialogTaskRegistry nonDialogTaskRegistry, DatabaseRegistry databaseRegistry) {
        this.domainName = domainName;
        this.lexicon = lexicon;
        this.ontologyRegistry = ontologyRegistry;
        this.nonDialogTaskRegistry = nonDialogTaskRegistry;
        this.databaseRegistry = databaseRegistry;
    }

    public String getDomainName() {
        return domainName;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public NonDialogTaskRegistry getNonDialogTaskRegistry() {
        return nonDialogTaskRegistry;
    }

    public DatabaseRegistry getDatabaseRegistry() {
        return databaseRegistry;
    }

    public OntologyRegistry getOntologyRegistry(){
        return ontologyRegistry;
    }
}

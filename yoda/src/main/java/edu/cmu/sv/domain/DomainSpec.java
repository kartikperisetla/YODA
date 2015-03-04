package edu.cmu.sv.domain;

import edu.cmu.sv.database.DatabaseRegistry;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.natural_language_generation.Lexicon;

/**
 * Created by David Cohen on 3/3/15.
 */
public class DomainSpec {
    Lexicon lexicon;
    OntologyRegistry ontologyRegistry;
    DialogRegistry dialogRegistry;
    DatabaseRegistry databaseRegistry;

    public DomainSpec(Lexicon lexicon, OntologyRegistry ontologyRegistry, DialogRegistry dialogRegistry, DatabaseRegistry databaseRegistry) {
        this.lexicon = lexicon;
        this.ontologyRegistry = ontologyRegistry;
        this.dialogRegistry = dialogRegistry;
        this.databaseRegistry = databaseRegistry;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public DialogRegistry getDialogRegistry() {
        return dialogRegistry;
    }

    public DatabaseRegistry getDatabaseRegistry() {
        return databaseRegistry;
    }

    public OntologyRegistry getOntologyRegistry(){
        return ontologyRegistry;
    }
}

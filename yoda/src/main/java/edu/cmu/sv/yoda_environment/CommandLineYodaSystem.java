package edu.cmu.sv.yoda_environment;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander.RegexPlusKeywordUnderstander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by David Cohen on 11/21/14.
 *
 * Command line interface to yoda dialog system
 *
 */
public abstract class CommandLineYodaSystem {

    public static List<DomainSpec> domainSpecs = new LinkedList<>();
    public static List<Runnable> simultaneousLaunch = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        for (DomainSpec spec : domainSpecs){
            System.err.println("loading domain spec ..." + spec.getDomainName());
            yodaEnvironment.loadDomain(spec);
        }
        Ontology.finalizeOntology();
        DialogRegistry.finalizeDialogRegistry();
                ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).constructTemplates();
        System.err.println("done loading domain");

        for (Runnable runnable : simultaneousLaunch){
            Thread t = new Thread(runnable);
            t.start();
        }

        Thread dstThread = new Thread(yodaEnvironment.dst);
        dstThread.start();
        Thread dmThread = new Thread(yodaEnvironment.dm);
        dmThread.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        // An empty line or Ctrl-Z terminates the program
        while ((s = in.readLine()) != null)
            if (s.length() !=0)
                yodaEnvironment.slu.process1BestAsr(s);
    }

}

package edu.cmu.sv;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.smart_house.SmartHouseDatabaseRegistry;
import edu.cmu.sv.domain.smart_house.SmartHouseLexicon;
import edu.cmu.sv.domain.smart_house.SmartHouseNonDialogTaskRegistry;
import edu.cmu.sv.domain.smart_house.SmartHouseOntologyRegistry;
import edu.cmu.sv.domain.smart_house.data.SmartHouseSLUDataset;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.data.YodaSkeletonSLUDataset;
import edu.cmu.sv.spoken_language_understanding.SLUDataset;
import edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander.RegexPlusKeywordUnderstander;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */

public class TestRegexPlusKeywordUnderstander {

    /*
    * Can only create one yoda environment per program, since it relies on static classes
    * */
    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
//        testYodaSkeletonSLU();
        testSmartHouseSLU();
//        runUnderstander();
    }

    public void testYodaSkeletonSLU() throws FileNotFoundException, UnsupportedEncodingException {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        List<DomainSpec> domainSpecs = new LinkedList<>();
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YodaSkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));

        for (DomainSpec spec : domainSpecs) {
            System.err.println("loading domain spec ..." + spec.getDomainName());
            yodaEnvironment.loadDomain(spec);
        }
        Ontology.finalizeOntology();
        DialogRegistry.finalizeDialogRegistry();
        ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).constructTemplates();
        System.err.println("done loading domain");

        SLUDataset tmp = new YodaSkeletonSLUDataset();
        yodaEnvironment.slu.evaluate(yodaEnvironment, tmp);
    }


//
//    public void testYelpPhoenixSLU() throws FileNotFoundException, UnsupportedEncodingException {
//        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();
//
//        List<DomainSpec> domainSpecs = new LinkedList<>();
//        domainSpecs.add(new DomainSpec(
//                "YODA skeleton domain",
//                new YodaSkeletonLexicon(),
//                new YodaSkeletonOntologyRegistry(),
//                new NonDialogTaskRegistry(),
//                new DatabaseRegistry()));
//        // yelp phoenix domain
//        domainSpecs.add(new DomainSpec(
//                "Yelp Phoenix domain",
//                new YelpPhoenixLexicon(),
//                new YelpPhoenixOntologyRegistry(),
//                new YelpPhoenixNonDialogTaskRegistry(),
//                new YelpPhoenixDatabaseRegistry()));
//
//        for (DomainSpec spec : domainSpecs) {
//            System.err.println("loading domain spec ..." + spec.getDomainName());
//            yodaEnvironment.loadDomain(spec);
//        }
//        Ontology.finalizeOntology();
//        DialogRegistry.finalizeDialogRegistry();
//        ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).constructTemplates();
//        System.err.println("done loading domain");
//
//        SLUDataset tmp = new YelpPhoenixSLUDataset();
//        yodaEnvironment.slu.evaluate(yodaEnvironment, tmp);
//    }

    public void testSmartHouseSLU() throws FileNotFoundException, UnsupportedEncodingException{
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        List<DomainSpec> domainSpecs = new LinkedList<>();
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YodaSkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // smart house domain
        domainSpecs.add(new DomainSpec(
                "Smart house domain",
                new SmartHouseLexicon(),
                new SmartHouseOntologyRegistry(),
                new SmartHouseNonDialogTaskRegistry(),
                new SmartHouseDatabaseRegistry()));

        for (DomainSpec spec : domainSpecs) {
            System.err.println("loading domain spec ..." + spec.getDomainName());
            yodaEnvironment.loadDomain(spec);
        }
        Ontology.finalizeOntology();
        DialogRegistry.finalizeDialogRegistry();
        ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).constructTemplates();
        System.err.println("done loading domain");

        SLUDataset tmp = new SmartHouseSLUDataset();
        yodaEnvironment.slu.evaluate(yodaEnvironment, tmp);
    }

    public void runUnderstander(){

        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        List<DomainSpec> domainSpecs = new LinkedList<>();
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YodaSkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // smart house domain
        domainSpecs.add(new DomainSpec(
                "Smart house domain",
                new SmartHouseLexicon(),
                new SmartHouseOntologyRegistry(),
                new SmartHouseNonDialogTaskRegistry(),
                new SmartHouseDatabaseRegistry()));

        for (DomainSpec spec : domainSpecs) {
            System.err.println("loading domain spec ..." + spec.getDomainName());
            yodaEnvironment.loadDomain(spec);
        }
        Ontology.finalizeOntology();
        DialogRegistry.finalizeDialogRegistry();
        ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).constructTemplates();
        System.err.println("done loading domain");

        List<String> testUtterances = new LinkedList<>();
        testUtterances.add("turn on the air conditioner");
        testUtterances.add("turn it on");
        testUtterances.add("turn on it");
        testUtterances.add("is the air conditioner on");
        testUtterances.add("is the air security system on");

        for (String testUtterance : testUtterances){
            System.out.println("understanding utterance: " + testUtterance);
            yodaEnvironment.slu.process1BestAsr(testUtterance);
        }

    }

}
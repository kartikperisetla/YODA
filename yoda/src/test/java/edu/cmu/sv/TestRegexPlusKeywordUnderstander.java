package edu.cmu.sv;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixDatabaseRegistry;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixLexicon;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixNonDialogTaskRegistry;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixOntologyRegistry;
import edu.cmu.sv.domain.yelp_phoenix.data.YelpPhoenixSLUDataset;
import edu.cmu.sv.domain.yoda_skeleton.YODASkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.spoken_language_understanding.SLUDataset;
import edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander.RegexPlusKeywordUnderstander;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */
public class TestRegexPlusKeywordUnderstander {
    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        List<DomainSpec> domainSpecs = new LinkedList<>();
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YODASkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // yelp phoenix domain
        domainSpecs.add(new DomainSpec(
                "Yelp Phoenix domain",
                new YelpPhoenixLexicon(),
                new YelpPhoenixOntologyRegistry(),
                new YelpPhoenixNonDialogTaskRegistry(),
                new YelpPhoenixDatabaseRegistry()));

        for (DomainSpec spec : domainSpecs){
            System.err.println("loading domain spec ..." + spec.getDomainName());
            yodaEnvironment.loadDomain(spec);
        }
        Ontology.finalizeOntology();
        DialogRegistry.finalizeDialogRegistry();
        ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).constructTemplates();
        System.err.println("done loading domain");


        SLUDataset tmp = new YelpPhoenixSLUDataset();
        System.out.println(tmp.dataSet.size());
        System.exit(0);

        List<String> testUtterances = new LinkedList<>();
        testUtterances.add("make a reservation at this restaurant");
        testUtterances.add("what's the closest mexican restaurant");
        testUtterances.add("where is the nearest mexican restaurant");
        testUtterances.add("are there any cheap mexican restaurants near preston fields hotel");
        testUtterances.add("can you make a reservation there for 8 p.m. tomorrow night");
        testUtterances.add("can i get directions from preston feels hotel to this restaurant");
        testUtterances.add("how many restaurants are near the preston fields hotel");
        testUtterances.add("is there a brew pub near the preston fields hotel");
        testUtterances.add("is there a bar near the preston fields hotel");
        testUtterances.add("make a reservation at burger meats buns");
        testUtterances.add("the restaurant burger meats buns");
        testUtterances.add("directions to the restaurant burger meats buns");
        testUtterances.add("the restaurant burger meats buns");
        testUtterances.add("please give me a list of restaurants near the preston hotel");
        testUtterances.add("what restaurants are near the preston fields hotel");
        testUtterances.add("what restaurants are near the preston fields hotel");
        testUtterances.add("is there a restaurant near the preston fields hotel");
        testUtterances.add("i'd like to make a reservation at the burger meats bun restaurant");
        testUtterances.add("burger meats bun restaurant near the preston fields hotel");
        testUtterances.add("yes");
        testUtterances.add("give me directions to the restaurant");
        testUtterances.add("yes");
        testUtterances.add("find restaurants near preston field hotel");
        testUtterances.add("kind of food is it");
        testUtterances.add("what's the price range of rhubarb");
        testUtterances.add("the reservation at rhubarb");
        testUtterances.add("yes");
        testUtterances.add("send me directions");
        testUtterances.add("yes");
        testUtterances.add("no");
        testUtterances.add("scotty can you find a restaurant near the preston fields hotel for me");
        testUtterances.add("can you find a restaurant near the preston field hotel for me");
        testUtterances.add("search for restaurant near preston fields hotel");
        testUtterances.add("what type of food is this restaurant provides");
        testUtterances.add("is it good is the");
        testUtterances.add("is rhubarb uh good");
        testUtterances.add("is the restaurant you told me good");
        testUtterances.add("show me directions to steins brewery");
        testUtterances.add("stein's brewery mountain view");
        testUtterances.add("show me a restaurant near preston fields hotel");
        testUtterances.add("show me a restaurant near preston fields hotel please");
        testUtterances.add("make a reservation for two at rhubarb");
        testUtterances.add("dinner for 2 at rhubarb");
        testUtterances.add("give me directions to rhubarb please");
        testUtterances.add("yes");
        testUtterances.add("rhubarb the expensive british place");
        testUtterances.add("i need to know any good thai restaurants near preston fields hotel");
        testUtterances.add("could you make a reservation there");
        testUtterances.add("could i please have directions to this restaurant");
        testUtterances.add("is there a good restaurant near preston fields hotel");
        testUtterances.add("is there a nice restaurant around preston fields hotel");
        testUtterances.add("is there a good restaurant near preston fields hotel");
        testUtterances.add("is there a good restaurant near preston fields hotel");
        testUtterances.add("make a reservation at burger meats bun");
        testUtterances.add("burger meats bun");
        testUtterances.add("no");
        testUtterances.add("give me directions to burger meats bun");
        testUtterances.add("yes");
        testUtterances.add("yes");
        testUtterances.add("find a restaurant near preston fields hotel");
        testUtterances.add("is it good");
        testUtterances.add("what are the other options");
        testUtterances.add("what other restaurants are around");
        testUtterances.add("make a reservation at rhubarbs");
        testUtterances.add("no");
        testUtterances.add("make a reservation at rhubarb");
        testUtterances.add("no");
        testUtterances.add("get directions from preston fields hotel to rhubarb restaurant");
        testUtterances.add("no please give me directions between preston field hotel and rhubarb restaurant");
        testUtterances.add("restaurants near preston fields hotel in phoenix arizona");
        testUtterances.add("restaurants near preston fields hotel");
        testUtterances.add("i'd like to make a reservation at that restaurant");
        testUtterances.add("how do i get to that restaurant");
        testUtterances.add("show me directions to that restaurant");
        testUtterances.add("yes");
        testUtterances.add("navigate to a thai restaurant near preston fields hotel");
        testUtterances.add("find a thai restaurant with the best yelp ratings near preston fields hotel");
        testUtterances.add("find a thai restaurant near preston fields hotel ");
        testUtterances.add("find a mediterranean restaurant near preston fields hotel");
        testUtterances.add("find a hamburger place near preston fields hotel");
        testUtterances.add("is there an in n out near preston fields hotel");
        testUtterances.add("find a table for 2 at the burger joint near preston fields hotel");
        testUtterances.add("make a reservation at the burger restaurant near preston fields hotel");
        testUtterances.add("burger place");
        testUtterances.add("yes");
        testUtterances.add("navigate to hamburger palace");
        testUtterances.add("navigate to hamburger palace");
        testUtterances.add("navigate to hamburger palace near preston fields hotel");
        testUtterances.add("directions to hamburger palace near preston fields hotel");
        testUtterances.add("hamburger palace");
        testUtterances.add("yes");
        testUtterances.add("are there any thai restaurants nearby");
        testUtterances.add("are there any thai restaurants near preston fields hotel");
        testUtterances.add("are there any reviews for the ho for the restaurant");
        testUtterances.add("are there any reviews for the restaurant");
        testUtterances.add("are there any thai restaurants near preston fields hotel");
        testUtterances.add("are there any thai restaurants near preston fields hotel");
        testUtterances.add("can you make a dinner reservation");
        testUtterances.add("can you make a reservation to Thaisanuk restaurant");
        testUtterances.add("yes");
        testUtterances.add("how do i get there from preston fields hotel");
        testUtterances.add("how do i get from preston field hotel to Thaisanuk restaurant");
        testUtterances.add("what is the nearest restaurant to preson fields hotel");
        testUtterances.add("i would like to find a restaurant near preston fields hotel");
        testUtterances.add("find restaurant near preston fields hotel");
        testUtterances.add("what did they serve for dinner");
        testUtterances.add("what is on for dinner");
        testUtterances.add("i'd like to make a reservation");
        testUtterances.add("rhubarb restaurant");
        testUtterances.add("yes");
        testUtterances.add("directions to rhubarb ho restaurant");
        testUtterances.add("rhubarb restaurant");
        testUtterances.add("no rhubarb restaurant");
        testUtterances.add("uh please find the restaurant near the preston field hotel");
        testUtterances.add("restaurant near the preston field hotel");
        testUtterances.add("restaurant near the preston field hotel");
        testUtterances.add("what is the price range of gray horse");
        testUtterances.add("is there a cheaper one");
        testUtterances.add("cheaper bar than the gray horse");
        testUtterances.add("cheaper bar for gray horse");
        testUtterances.add("cheaper restaurant");
        testUtterances.add("book a table at the gray horse");
        testUtterances.add("book a table at the gray horse");
        testUtterances.add("reserve a table at the gray horse");
        testUtterances.add("reserve a table at the gray horse");
        testUtterances.add("reserve a table at the gray horse");
        testUtterances.add("please give directions to the grey horse");
        testUtterances.add("get directions to the gray horse from the preston fields hotel");
        testUtterances.add("get directions to the gray horse from the preston fields hotel");
        testUtterances.add("get directions to the gray horse");
        testUtterances.add("yes");
        testUtterances.add("restaurants around preston hotel");
        testUtterances.add("restaurant around preston field hotel");
        testUtterances.add("restaurants around preston's field hotel");
        testUtterances.add("cafe around preston fields hotel");
        testUtterances.add("cafes near preston field hotel");
        testUtterances.add("reviews for burger");
        testUtterances.add("yelp reviews for burger");
        testUtterances.add("is burger restaurant good");
        testUtterances.add("burger good or bad");
        testUtterances.add("make a reservation for burger restaurant");
        testUtterances.add("burger");
        testUtterances.add("no");
        testUtterances.add("make a reservation at burger restaurant");
        testUtterances.add("directions to burger");
        testUtterances.add("can you tell me the names of some nearby restaurants");
        testUtterances.add("what's the best way to get to preston fields");
        testUtterances.add("can you tell me the names of some restaurants that are near preston fields");
        testUtterances.add("where can i go to eat near preston fields");
        testUtterances.add("please make a reservation at burger");
        testUtterances.add("please make a reservation to have dinner at burger");
        testUtterances.add("no that's not right i meant burger");
        testUtterances.add("no that's not right this is a hamburger place");
        testUtterances.add("i need directions to the burger restaurant");
        testUtterances.add("local restaurants near the preston hotel");
        testUtterances.add("restaurants near the preston field hotel");
        testUtterances.add("thanks anything else");
        testUtterances.add("another restaurant near the preston fields hotel");
        testUtterances.add("make a reservation at rom bar");
        testUtterances.add("reservation at room barb");
        testUtterances.add("yes that's right");
        testUtterances.add("reservation at rum bard ");
        testUtterances.add("reservation at rhubarb");
        testUtterances.add("please make reservation at rhubarb");
        testUtterances.add("pizza");
        testUtterances.add("pizza");
        testUtterances.add("directions to rhubarb");

        for (String testUtterance : testUtterances){
            System.out.println("understanding utterance: " + testUtterance);
            yodaEnvironment.slu.process1BestAsr(testUtterance);
        }

    }

}
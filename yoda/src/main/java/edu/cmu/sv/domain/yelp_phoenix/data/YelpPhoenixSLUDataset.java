package edu.cmu.sv.domain.yelp_phoenix.data;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.SLUDataset;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 3/11/15.
 */
public class YelpPhoenixSLUDataset extends SLUDataset {
    public YelpPhoenixSLUDataset() {
        // for these examples, the annotation scheme completely captures the meaning of the utterance
        {
//            add(new ImmutablePair<>("make a reservation at this restaurant",
//                    new SemanticsModel("{\"verb\": {\"Destination\": {\"refType\": \"pronoun\", \"class\": \"Restaurants\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("are there any cheap mexican restaurants near preston fields hotel",
//                    new SemanticsModel("{\"verb\": {\"class\":\"Exist\", \"Agent\" : {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Mexican\", \"HasExpensiveness\": {\"class\": \"Cheap\"}}}, \"dialogAct\": \"YNQuestion\"}")));
//            add(new ImmutablePair<>("is there a bar near the preston fields hotel",
//                    new SemanticsModel("{\"verb\": {\"class\":\"Exist\", \"Agent\" : {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Bars\"}}, \"dialogAct\": \"YNQuestion\"}")));
//            add(new ImmutablePair<>("can i get directions from preston feels hotel to this restaurant",
//                    new SemanticsModel("{\"verb\": {\"Destination\": {\"refType\": \"pronoun\", \"class\": \"Restaurants\"}, \"Origin\" : {\"class\": \"Hotels\", \"HasName\": \"preston feels hotel\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("make a reservation at burger meats buns",
//                    new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \"burger meats buns\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("the restaurant burger meats buns",
//                    new SemanticsModel("{\"topic\": {\"class\": \"Restaurants\", \"HasName\": \"burger meats buns\"}, \"dialogAct\": \"Fragment\"}")));
//            add(new ImmutablePair<>("the restaurant burger meats buns",
//                    new SemanticsModel("{\"topic\": {\"class\": \"Restaurants\", \"HasName\": \"burger meats buns\"}, \"dialogAct\": \"Fragment\"}")));
//            add(new ImmutablePair<>("directions to the restaurant burger meats buns",
//                    new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\", \"HasName\": \"burger meats buns\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("what restaurants are near the preston fields hotel",
//                    new SemanticsModel("{\"verb\": {\"class\":\"Exist\", \"Agent\" : {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"the preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//            add(new ImmutablePair<>("what's the price range of rhubarb",
//                    new SemanticsModel("{\"verb\": {\"Patient\": {\"HasValue\": {\"class\": \"Expensiveness\"}, \"class\": \"Requested\"}, \"class\": \"HasProperty\", \"Agent\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" rhubarb\"}}, \"dialogAct\": \"WHQuestion\"}")));
//            add(new ImmutablePair<>("the reservation at rhubarb",
//                    new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \"rhubarb\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("what restaurants are near the preston fields hotel",
//                    new SemanticsModel("{\"verb\": {\"class\":\"Exist\", \"Agent\" : {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"the preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//            add(new ImmutablePair<>("yes",
//                    new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//            add(new ImmutablePair<>("give me directions to the restaurant",
//                    new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("yes",
//                    new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//            add(new ImmutablePair<>("find restaurants near preston field hotel",
//                    new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"preston field hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//            add(new ImmutablePair<>("yes",
//                    new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//            add(new ImmutablePair<>("send me directions",
//                    new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//            add(new ImmutablePair<>("yes",
//                    new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//            add(new ImmutablePair<>("no",
//                    new SemanticsModel("{\"dialogAct\": \"Reject\"}")));
//            add(new ImmutablePair<>("scotty can you find a restaurant near the preston fields hotel for me",
//                    new SemanticsModel("{\"verb\": {\"class\":\"Exist\", \"Agent\" : {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"the preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//            add(new ImmutablePair<>("restaurants near preston fields hotel",
//                    new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}, \"dialogAct\": \"Fragment\"}")));
//            add(new ImmutablePair<>("i'd like to make a reservation at that restaurant",
//                    new SemanticsModel("{\"verb\": {\"class\": \"MakeReservation\", \"Destination\":{\"class\": \"Restaurants\", \"refType\":\"pronoun\"}}, \"dialogAct\": \"Command\"}")));

            add(new ImmutablePair<>("how do i get to that restaurant",
                    new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\", \"Destination\":{\"class\": \"Restaurants\", \"refType\":\"pronoun\"}}, \"dialogAct\": \"Command\"}")));
            add(new ImmutablePair<>("show me directions to that restaurant",
                    new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\", \"Destination\":{\"class\": \"Restaurants\", \"refType\":\"pronoun\"}}, \"dialogAct\": \"Command\"}")));
            add(new ImmutablePair<>("yes",
                    new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
            add(new ImmutablePair<>("navigate to a thai restaurant near preston fields hotel",
                    new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\", \"Destination\":{\"class\": \"Thai\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"the preston fields hotel\"}, \"class\": \"IsCloseTo\"}}}, \"dialogAct\": \"Command\"}")));
            add(new ImmutablePair<>("i'd like to make a reservation at the burger meats bun restaurant",
                    new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\", \"HasName\": \"burger meats bun\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
        }

        // for these examples, the annotation scheme misses important information from the utterance
        {
            add(new ImmutablePair<>("can you make a reservation there for 8 p.m. tomorrow night",
                    new SemanticsModel("{\"verb\": {\"Destination\": {\"refType\": \"pronoun\", \"class\": \"PointOfInterest\"}, \"HasAtTime\":{\"class\":\"Time\", \"HasHour\":8, \"HasAmPm\":\"PM\"} \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
            add(new ImmutablePair<>("please give me a list of restaurants near the preston hotel",
                    new SemanticsModel("{\"verb\": {\"class\":\"Exist\", \"Agent\" : {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \"the preston hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
        }

        // for these examples, the dialog system can't deal with missing information
        {
//        add(new ImmutablePair<>("what's the closest mexican restaurant",
//                new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what's the closest mexican restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("where is the nearest mexican restaurant",
//                new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"where is the nearest mexican restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("how many restaurants are near the preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"how many restaurants are near the preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("kind of food is it", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"kind of food is it\"}, \"dialogAct\": \"Fragment\"}")));
        }

//        add(new ImmutablePair<>("is there a brew pub near the preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a brew pub near the preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is there a restaurant near the preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a restaurant near the preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("burger meats bun restaurant near the preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"burger meats bun restaurant near the preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("can you find a restaurant near the preston field hotel for me", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"can you find a restaurant near the preston field hotel for me\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("search for restaurant near preston fields hotel", new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("what type of food is this restaurant provides", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what type of food is this restaurant provides\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is it good is the", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is it good is the\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is rhubarb uh good", new SemanticsModel("{\"verb\": {\"Patient\": {\"HasGoodness\": {\"class\": \"Good\"}, \"class\": \"UnknownThingWithRoles\"}, \"class\": \"HasProperty\", \"Agent\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \"rhubarb uh \"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("is the restaurant you told me good", new SemanticsModel("{\"verb\": {\"Patient\": {\"HasGoodness\": {\"class\": \"Good\"}, \"class\": \"UnknownThingWithRoles\"}, \"class\": \"HasProperty\", \"Agent\": {\"class\": \"Restaurants\", \"HasName\": \"restaurant you told me \"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("show me directions to steins brewery", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("stein's brewery mountain view", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"stein's brewery mountain view\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("show me a restaurant near preston fields hotel", new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("show me a restaurant near preston fields hotel please", new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel please\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("make a reservation for two at rhubarb", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" for two at rhubarb\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("dinner for 2 at rhubarb", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"dinner for 2 at rhubarb\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("give me directions to rhubarb please", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" to rhubarb please\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("rhubarb the expensive british place", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"rhubarb the expensive british place\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("i need to know any good thai restaurants near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"i need to know any good thai restaurants near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("could you make a reservation there", new SemanticsModel("{\"verb\": {\"Destination\": {\"refType\": \"pronoun\", \"class\": \"PointOfInterest\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("could i please have directions to this restaurant", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("is there a good restaurant near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a good restaurant near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is there a nice restaurant around preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a nice restaurant around preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is there a good restaurant near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a good restaurant near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is there a good restaurant near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a good restaurant near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("make a reservation at burger meats bun", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at burger meats bun\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("burger meats bun", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"burger meats bun\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("no", new SemanticsModel("{\"dialogAct\": \"Reject\"}")));
//        add(new ImmutablePair<>("give me directions to burger meats bun", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" to burger meats bun\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("find a restaurant near preston fields hotel", new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("is it good", new SemanticsModel("{\"verb\": {\"Patient\": {\"HasGoodness\": {\"class\": \"Good\"}, \"class\": \"UnknownThingWithRoles\"}, \"class\": \"HasProperty\", \"Agent\": {\"refType\": \"pronoun\", \"class\": \"Noun\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("what are the other options", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what are the other options\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("what other restaurants are around", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what other restaurants are around\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("make a reservation at rhubarbs", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at rhubarbs\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("no", new SemanticsModel("{\"dialogAct\": \"Reject\"}")));
//        add(new ImmutablePair<>("make a reservation at rhubarb", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at rhubarb\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("no", new SemanticsModel("{\"dialogAct\": \"Reject\"}")));
//        add(new ImmutablePair<>("get directions from preston fields hotel to rhubarb restaurant", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("no please give me directions between preston field hotel and rhubarb restaurant", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("restaurants near preston fields hotel in phoenix arizona", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel in phoenix arizona\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("find a thai restaurant with the best yelp ratings near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"find a thai restaurant with the best yelp ratings near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("find a thai restaurant near preston fields hotel ", new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel \"}, \"class\": \"IsCloseTo\"}, \"class\": \"Thai\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("find a mediterranean restaurant near preston fields hotel", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Mediterranean\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("find a hamburger place near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"find a hamburger place near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is there an in n out near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there an in n out near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("find a table for 2 at the burger joint near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"find a table for 2 at the burger joint near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("make a reservation at the burger restaurant near preston fields hotel", new SemanticsModel("{\"verb\": {\"Destination\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\", \"HasName\": \" at the burger restaurant \"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("burger place", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"burger place\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("navigate to hamburger palace", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"navigate to hamburger palace\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("navigate to hamburger palace", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"navigate to hamburger palace\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("navigate to hamburger palace near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"navigate to hamburger palace near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("directions to hamburger palace near preston fields hotel", new SemanticsModel("{\"verb\": {\"Destination\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"UnknownThingWithRoles\", \"HasName\": \" to hamburger palace \"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("hamburger palace", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"hamburger palace\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("are there any thai restaurants nearby", new SemanticsModel("{\"topic\": {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \"by\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Thai\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("are there any thai restaurants near preston fields hotel", new SemanticsModel("{\"topic\": {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Thai\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("are there any reviews for the ho for the restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"are there any reviews for the ho for the restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("are there any reviews for the restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"are there any reviews for the restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("are there any thai restaurants near preston fields hotel", new SemanticsModel("{\"topic\": {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Thai\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("are there any thai restaurants near preston fields hotel", new SemanticsModel("{\"topic\": {\"refType\": \"pronoun\", \"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Thai\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("can you make a dinner reservation", new SemanticsModel("{\"verb\": {\"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("can you make a reservation to Thaisanuk restaurant", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\", \"HasName\": \" to Thaisanuk restaurant\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("how do i get there from preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"how do i get there from preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("how do i get from preston field hotel to Thaisanuk restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"how do i get from preston field hotel to Thaisanuk restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("what is the nearest restaurant to preson fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what is the nearest restaurant to preson fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("i would like to find a restaurant near preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"i would like to find a restaurant near preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("find restaurant near preston fields hotel", new SemanticsModel("{\"verb\": {\"class\": \"Exist\", \"Agent\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston fields hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("what did they serve for dinner", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what did they serve for dinner\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("what is on for dinner", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what is on for dinner\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("i'd like to make a reservation", new SemanticsModel("{\"verb\": {\"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("rhubarb restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"rhubarb restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("directions to rhubarb ho restaurant", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\", \"HasName\": \" to rhubarb ho restaurant\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("rhubarb restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"rhubarb restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("no rhubarb restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"no rhubarb restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("uh please find the restaurant near the preston field hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"uh please find the restaurant near the preston field hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("restaurant near the preston field hotel", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" the preston field hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("restaurant near the preston field hotel", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" the preston field hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("what is the price range of gray horse", new SemanticsModel("{\"verb\": {\"Patient\": {\"HasValue\": {\"class\": \"Expensiveness\"}, \"class\": \"Requested\"}, \"class\": \"HasProperty\", \"Agent\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" gray horse\"}}, \"dialogAct\": \"WHQuestion\"}")));
//        add(new ImmutablePair<>("is there a cheaper one", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"is there a cheaper one\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("cheaper bar than the gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"cheaper bar than the gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("cheaper bar for gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"cheaper bar for gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("cheaper restaurant", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"cheaper restaurant\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("book a table at the gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"book a table at the gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("book a table at the gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"book a table at the gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("reserve a table at the gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"reserve a table at the gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("reserve a table at the gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"reserve a table at the gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("reserve a table at the gray horse", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"reserve a table at the gray horse\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("please give directions to the grey horse", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" to the grey horse\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("get directions to the gray horse from the preston fields hotel", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("get directions to the gray horse from the preston fields hotel", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("get directions to the gray horse", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("yes", new SemanticsModel("{\"dialogAct\": \"Accept\"}")));
//        add(new ImmutablePair<>("restaurants around preston hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"restaurants around preston hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("restaurant around preston field hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"restaurant around preston field hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("restaurants around preston's field hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"restaurants around preston's field hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("cafe around preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"cafe around preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("cafes near preston field hotel", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" preston field hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Cafes\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("reviews for burger", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"reviews for burger\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("yelp reviews for burger", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"yelp reviews for burger\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("is burger restaurant good", new SemanticsModel("{\"verb\": {\"Patient\": {\"HasGoodness\": {\"class\": \"Good\"}, \"class\": \"UnknownThingWithRoles\"}, \"class\": \"HasProperty\", \"Agent\": {\"class\": \"Restaurants\", \"HasName\": \"burger restaurant \"}}, \"dialogAct\": \"YNQuestion\"}")));
//        add(new ImmutablePair<>("burger good or bad", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"burger good or bad\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("make a reservation for burger restaurant", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\", \"HasName\": \" for burger restaurant\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("burger", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"burger\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("no", new SemanticsModel("{\"dialogAct\": \"Reject\"}")));
//        add(new ImmutablePair<>("make a reservation at burger restaurant", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Restaurants\", \"HasName\": \" at burger restaurant\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("directions to burger", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" to burger\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("can you tell me the names of some nearby restaurants", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"can you tell me the names of some nearby restaurants\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("what's the best way to get to preston fields", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"what's the best way to get to preston fields\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("can you tell me the names of some restaurants that are near preston fields", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"can you tell me the names of some restaurants that are near preston fields\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("where can i go to eat near preston fields", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"where can i go to eat near preston fields\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("please make a reservation at burger", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at burger\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("please make a reservation to have dinner at burger", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" to have dinner at burger\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("no that's not right i meant burger", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"no that's not right i meant burger\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("no that's not right this is a hamburger place", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"no that's not right this is a hamburger place\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("i need directions to the burger restaurant", new SemanticsModel("{\"verb\": {\"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("local restaurants near the preston hotel", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" the preston hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"LocalFlavor\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("restaurants near the preston field hotel", new SemanticsModel("{\"topic\": {\"HasDistance\": {\"InRelationTo\": {\"class\": \"Hotels\", \"HasName\": \" the preston field hotel\"}, \"class\": \"IsCloseTo\"}, \"class\": \"Restaurants\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("thanks anything else", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"thanks anything else\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("another restaurant near the preston fields hotel", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"another restaurant near the preston fields hotel\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("make a reservation at rom bar", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"Bars\", \"HasName\": \" at rom bar\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("reservation at room barb", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at room barb\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("yes that's right", new SemanticsModel("{\"topic\": {\"class\": \"PointOfInterest\", \"HasName\": \"yes that's right\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("reservation at rum bard ", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at rum bard \"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("reservation at rhubarb", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at rhubarb\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("please make reservation at rhubarb", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" at rhubarb\"}, \"class\": \"MakeReservation\"}, \"dialogAct\": \"Command\"}")));
//        add(new ImmutablePair<>("pizza", new SemanticsModel("{\"topic\": {\"class\": \"Pizza\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("pizza", new SemanticsModel("{\"topic\": {\"class\": \"Pizza\"}, \"dialogAct\": \"Fragment\"}")));
//        add(new ImmutablePair<>("directions to rhubarb", new SemanticsModel("{\"verb\": {\"Destination\": {\"class\": \"UnknownThingWithRoles\", \"HasName\": \" to rhubarb\"}, \"class\": \"GiveDirections\"}, \"dialogAct\": \"Command\"}")));
    }
}

package edu.cmu.sv.spoken_language_understanding.auto_lexicon;

import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sahil on 13/3/15.
 */
public class LexiconExtender {
    IDictionary wnDict = null;
    static Map<Lexicon.LexicalEntry.PART_OF_SPEECH, POS> posMap = new HashMap<>();
    static {
        posMap.put(Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, POS.NOUN);
        posMap.put(Lexicon.LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, POS.NOUN);
        posMap.put(Lexicon.LexicalEntry.PART_OF_SPEECH.S1_VERB, POS.VERB);
        posMap.put(Lexicon.LexicalEntry.PART_OF_SPEECH.S3_VERB, POS.VERB);
        posMap.put(Lexicon.LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, POS.VERB);
        posMap.put(Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, POS.ADJECTIVE);
    }

    // Constructor
    public LexiconExtender() throws IllegalStateException {
        String wnHome = System.getenv("WORDNET_HOME");
        if (wnHome.isEmpty()) {
            throw new IllegalStateException("'WORDNET_HOME' environment variable not set. Cannot initiate LexiconExtender.");
        }

        String path = wnHome + File.separator + "dict";
        URL url = null;

        try {
            url = new URL("file", null, path);
            wnDict = new Dictionary(url);
            wnDict.open();
        } catch (MalformedURLException e) { // URL error
            e.printStackTrace();
        } catch (IOException e) { // Error opening dictionary
            e.printStackTrace();
        }
    }

    public Set<String> getSynonyms(String word, Lexicon.LexicalEntry.PART_OF_SPEECH pos) {
        IIndexWord idxWord = wnDict.getIndexWord(word, posMap.get(pos));
        IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
        IWord iWord = wnDict.getWord(wordID);
        ISynset synset = iWord.getSynset();

        Set<String> synonymSet = new HashSet<>();
        for (IWord synWord : synset.getWords())
            synonymSet.add(synWord.getLemma());

        return synonymSet;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide 2 arguments");
            System.out.println("1st argument: Word to check synsets for");
            System.out.println("2nd argument: Part Of Speech type (noun || verb || adjective)");
            return;
        }

        LexiconExtender le = null;
        Set<String> synset = null;
        try {
            le = new LexiconExtender();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        switch (args[1]) {
            case "verb":
                synset = le.getSynonyms(args[0], Lexicon.LexicalEntry.PART_OF_SPEECH.S1_VERB);
                break;
            case "adjective":
                synset = le.getSynonyms(args[0], Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE);
                break;
            case "noun":
            default:
                synset = le.getSynonyms(args[0], Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN);
        }
        System.out.print("Synset: ");
        System.out.println(synset);
    }
}

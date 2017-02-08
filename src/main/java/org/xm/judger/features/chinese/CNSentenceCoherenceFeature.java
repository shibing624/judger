package org.xm.judger.features.chinese;


import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Feature for sentence-transition coherence.  More or less average keyword overlap.
 *
 * @author xuming
 */
public class CNSentenceCoherenceFeature implements CNFeatures {

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        int numWords = 0;
        int overlap = 0;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            HashSet<String> partParagraph = new HashSet<>();
            for (ArrayList<String> sentence : paragraph) {
                HashSet<String> words = new HashSet<>();
                for (String token : sentence) {
                    token = token.toLowerCase();
                    if (partParagraph.contains(token))
                        overlap++;
                    numWords++;
                    words.add(token);
                }
                // merge
                partParagraph.addAll(words);
            }
        }
        result.put("overlap_coherence", new Double(overlap / (double) numWords));
        if (Config.DEBUG)
            System.out.println("Overlap coherence for ID(" + instance.id + ") @ score("
                    + instance.domain1_score + "): " + result.get("overlap_coherence"));

        return result;
    }
}

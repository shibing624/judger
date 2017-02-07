package org.xm.judger.features.chinese;


import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.judger.domain.EssayInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author xuming
 */
public class CNIDFFeature implements CNFeatures {
    HashMap<String, double[]> idf;

    /**
     * init and compute IDF
     *
     * @param instances
     */
    public CNIDFFeature(ArrayList<CNEssayInstance> instances) {
        idf = new HashMap<>();
        // 1.doc frequency
        for (EssayInstance instance : instances) {
            HashSet<String> words = new HashSet<>();
            ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
            for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
                for (ArrayList<String> sentence : paragraph) {
                    for (String token : sentence) {
                        words.add(token.toLowerCase());
                    }
                }
            }
            // merge
            for (String word : words) {
                if (idf.containsKey(word))
                    idf.get(word)[0]++;
                else idf.put(word, new double[]{1});
            }
        }
        // 2.invert it
        for (String word : idf.keySet()) idf.get(word)[0] = Math.log(instances.size() / (double) idf.get(word)[0]);
    }

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        int numWords = 0;
        double sumIdf = 0;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                for (String token : sentence) {
                    sumIdf += idf.get(token.toLowerCase())[0];
                    numWords++;
                }
            }
        }
        HashMap<String, Double> values = new HashMap<>();
        values.put("AverageIDF", new Double(sumIdf / (double) numWords));
        if (Config.DEBUG)
            System.out.println("AverageIDF for ID(" + instance.id + "): " + values.get("AverageIDF"));
        return values;
    }
}

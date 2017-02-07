package org.xm.judger.features;


import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;
import org.xm.xmnlp.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author xuming
 */
public class AverageWordLengthFeature implements Features {
    public static final Pattern validWord = Pattern.compile("\\w");

    @Override
    public HashMap<String, Double> getFeatureScores(ENEssayInstance instance) {
        int numWords = 0;
        int sumLength = 0;
        ArrayList<ArrayList<ArrayList<String>>> pargraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> pargraph : pargraphs) {
            for (ArrayList<String> sentence : pargraph) {
                for (String token : sentence) {
                    if (token.charAt(0) != '@' && validWord.matcher(token).find()) {
                        numWords++;
                        sumLength += token.length();
                    }
                }
            }
        }
        HashMap<String, Double> values = new HashMap<>();
        values.put("AverageWordLength", new Double(sumLength / (double) numWords));
        if (Config.DEBUG)
            System.out.println("Average word length for ID(" + instance.id + "): " + (sumLength / (double) numWords));
        return values;
    }

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        int numWords = 0;
        int sumLength = 0;
        ArrayList<ArrayList<ArrayList<String>>> pargraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> pargraph : pargraphs) {
            for (ArrayList<String> sentence : pargraph) {
                for (String token : sentence) {
                    if (StringUtil.isNotBlank(token)) {
                        numWords++;
                        sumLength += token.length();
                    }
                }
            }
        }
        HashMap<String, Double> values = new HashMap<>();
        values.put("AverageWordLength", new Double(sumLength / (double) numWords));
        if (Config.DEBUG)
            System.out.println("Average word length for ID(" + instance.id + "): " + (sumLength / (double) numWords));
        return values;
    }
}

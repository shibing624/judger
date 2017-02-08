package org.xm.judger.features.chinese;


import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 词长特征
 * 包括：平均词长、词长是1的单词数、词长是2的单词数、词长是3的单词数、词长是4的单词数及占比
 *
 * @author xuming
 */
public class CNWordLengthFeature implements CNFeatures {

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        int numWords = 0;
        int sumLength = 0;
        int oneLengthWordCount = 0;
        int twoLengthWordCount = 0;
        int threeLengthWordCount = 0;
        int fourLengthWordCount = 0;
        ArrayList<ArrayList<ArrayList<String>>> pargraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> pargraph : pargraphs) {
            for (ArrayList<String> sentence : pargraph) {
                for (String token : sentence) {
                    numWords++;
                    int count = token.length();
                    sumLength += count;
                    if (count == 1) oneLengthWordCount++;
                    if (count == 2) twoLengthWordCount++;
                    if (count == 3) threeLengthWordCount++;
                    if (count == 4) fourLengthWordCount++;
                }
            }
        }
        HashMap<String, Double> values = new HashMap<>();
        values.put("AverageWordLength", new Double(sumLength / (double) numWords));

        values.put("OneLengthWordCount", new Double(oneLengthWordCount));
        values.put("OneLengthWordRatio", new Double(oneLengthWordCount / (double) numWords));
        values.put("TwoLengthWordCount", new Double(twoLengthWordCount));
        values.put("TwoLengthWordRatio", new Double(twoLengthWordCount / (double) numWords));
        values.put("ThreeLengthWordCount", new Double(threeLengthWordCount));
        values.put("ThreeLengthWordRatio", new Double(threeLengthWordCount / (double) numWords));
        values.put("FourLengthWordCount", new Double(fourLengthWordCount));
        values.put("FourLengthWordRatio", new Double(fourLengthWordCount / (double) numWords));

        if (Config.DEBUG)
            System.out.println("Average word length for ID(" + instance.id + "): " + (sumLength / (double) numWords));
        return values;
    }
}

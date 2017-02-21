package org.xm.judger.features.chinese;


import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 句子数、平均句长、句长是大于7个词的句数、句长是大于8个词的句数、句长是大于9个词的句数及占比
 *
 * @author xuming
 */
public class CNSentenceLengthFeature implements CNFeatures {

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        int numSentences = 0;
        int sumCount = 0;
        int moreThanSevenWordSentenceCount = 0;
        int moreThanEightWordSentenceCount = 0;
        int moreThanNineWordSentenceCount = 0;
        // compute the word length of longest essay
        ArrayList<ArrayList<ArrayList<String>>> pargraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> pargraph : pargraphs) {
            for (ArrayList<String> sentence : pargraph) {
                numSentences++;
                int size = sentence.size();
                sumCount += size;
                if (size > 7) moreThanSevenWordSentenceCount++;
                if (size > 8) moreThanEightWordSentenceCount++;
                if (size > 9) moreThanNineWordSentenceCount++;
            }
        }

        HashMap<String, Double> values = new HashMap<>();
        values.put("Num_Sentences", new Double(numSentences));
        values.put("AverageSentenceLength", new Double(sumCount / (double) numSentences));
        values.put("MoreThanSevenWordSentenceCount", new Double(moreThanSevenWordSentenceCount));
        values.put("MoreThanSevenWordSentenceRatio", new Double(moreThanSevenWordSentenceCount / (double) numSentences));
        values.put("MoreThanEightWordSentenceCount", new Double(moreThanEightWordSentenceCount));
        values.put("MoreThanEightWordSentenceRatio", new Double(moreThanEightWordSentenceCount / (double) numSentences));
        values.put("MoreThanNineWordSentenceCount", new Double(moreThanNineWordSentenceCount));
        values.put("MoreThanNineWordSentenceRatio", new Double(moreThanNineWordSentenceCount / (double) numSentences));

        if (Config.DEBUG) {
            System.out.println("numSentences for ID(" + instance.id + "): " + (double) numSentences);
        }
        return values;
    }
}

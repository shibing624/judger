package org.xm.judger.features.english;


import org.xm.judger.Judger;
import org.xm.judger.domain.ENEssayInstance;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author xuming
 */
public class ENLengthFeature implements ENFeatures {
    HashMap<String, Double> featureScores = new HashMap<>();
    private static int maxWordCount = -1;

    /**
     * The length of this essay normalized against the length of longest essay
     * @param essay
     * @return
     */
    public Double getScore(String essay) {
        String[] words = essay.split("\\s");
        return Double.valueOf((double) words.length / getMaxWordCount());
    }

    private int getMaxWordCount() {
        if (maxWordCount == -1) {
            ArrayList<ENEssayInstance> instances = Judger.getENInstances();
            // compute the word length of longest essay
            for (ENEssayInstance instance : instances) {
                String essay = instance.essay;
                String[] words = essay.split("\\s");
                int count = words.length;
                if (count > maxWordCount)
                    maxWordCount = count;
            }
        }
        return maxWordCount;
    }

    @Override
    public HashMap<String, Double> getFeatureScores(ENEssayInstance instance) {
        featureScores.put("lengthratio", getScore(instance.essay));
        return featureScores;
    }
}

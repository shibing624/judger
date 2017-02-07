package org.xm.judger.features.chinese;


import org.xm.judger.Judger;
import org.xm.judger.domain.CNEssayInstance;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author xuming
 */
public class CNLengthFeature implements CNFeatures {
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
            ArrayList<CNEssayInstance> instances = Judger.getCNInstances();
            // compute the word length of longest essay
            for (CNEssayInstance instance : instances) {
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
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        featureScores.put("lengthratio", getScore(instance.essay));
        return featureScores;
    }
}

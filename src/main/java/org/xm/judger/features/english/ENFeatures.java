package org.xm.judger.features.english;

import org.xm.judger.domain.ENEssayInstance;

import java.util.HashMap;

/**
 * @author xuming
 */
public interface ENFeatures {
    /**
     * get feature score for each english essay instance
     * eg:spelling features
     *
     * @param instance
     * @return
     */
    HashMap<String, Double> getFeatureScores(ENEssayInstance instance);

}

package org.xm.judger.features;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.ENEssayInstance;

import java.util.HashMap;

/**
 * @author xuming
 */
public interface Features {
    /**
     * get feature score for each english essay instance
     * eg:spelling features
     *
     * @param instance
     * @return
     */
    HashMap<String, Double> getFeatureScores(ENEssayInstance instance);

    /**
     * get feature score for each chinese essay instance
     *
     * @param instance
     * @return
     */
    HashMap<String, Double> getFeatureScores(CNEssayInstance instance);
}

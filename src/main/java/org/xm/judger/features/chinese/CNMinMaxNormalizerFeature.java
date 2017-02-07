package org.xm.judger.features.chinese;


import org.xm.judger.domain.CNEssayInstance;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Normalize the specified feature to (val-min)/(max-min) on a task-by-task basis.
 *
 * @author xuming
 */
public class CNMinMaxNormalizerFeature implements CNFeatures {
    private HashMap<Integer, double[]> min;
    private HashMap<Integer, double[]> max;
    private CNFeatures baseFeature;
    private String baseName;
    private String name;

    /**
     * Learns the min-max range of the feature for each task
     *
     * @param trainingSample
     * @param base
     * @param baseName
     */
    public CNMinMaxNormalizerFeature(ArrayList<CNEssayInstance> trainingSample, CNFeatures base, String baseName) {
        min = new HashMap<>();
        max = new HashMap<>();
        this.baseFeature = base;
        this.baseName = baseName;
        this.name = baseName + "_MinMaxNorm_Task";
        for (CNEssayInstance instance : trainingSample) {
            double value = getBaseValue(instance);
            if (!min.containsKey(instance.set))
                min.put(instance.set, new double[]{value});
            else if (min.get(instance.set)[0] > value)
                min.get(instance.set)[0] = value;
            if (!max.containsKey(instance.set))
                max.put(instance.set, new double[]{value});
            else if (max.get(instance.set)[0] < value)
                max.get(instance.set)[0] = value;
        }
    }

    private double getBaseValue(CNEssayInstance instance) {
        Double value = instance.getFeature(baseName);
        if (value == null) {
            HashMap<String, Double> values = baseFeature.getFeatureScores(instance);
            value = values.get(baseName);
        }
        return value.doubleValue();
    }

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        double value = getBaseValue(instance);
        // have all
        assert (min.containsKey(instance.set) && max.containsKey(instance.set));
        double tempMin = min.get(instance.set)[0];
        double tempMax = max.get(instance.set)[0];
        // not equal
        assert (tempMax != tempMin);
        double score = (value - tempMin) / (tempMax - tempMin);
        result.put(name, score);
        return result;
    }
}

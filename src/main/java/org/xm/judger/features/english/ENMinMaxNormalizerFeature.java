package org.xm.judger.features.english;


import org.xm.judger.domain.ENEssayInstance;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Normalize the specified feature to (val-min)/(max-min) on a task-by-task basis.
 *
 * @author xuming
 */
public class ENMinMaxNormalizerFeature implements ENFeatures {
    private HashMap<Integer, double[]> min;
    private HashMap<Integer, double[]> max;
    private ENFeatures baseFeature;
    private String baseName;
    private String name;

    /**
     * Learns the min-max range of the feature for each task
     *
     * @param trainingSample
     * @param base
     * @param baseName
     */
    public ENMinMaxNormalizerFeature(ArrayList<ENEssayInstance> trainingSample, ENFeatures base, String baseName) {
        min = new HashMap<>();
        max = new HashMap<>();
        this.baseFeature = base;
        this.baseName = baseName;
        this.name = baseName + "_MinMaxNorm_Task";
        for (ENEssayInstance instance : trainingSample) {
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

    private double getBaseValue(ENEssayInstance instance) {
        Double value = instance.getFeature(baseName);
        if (value == null) {
            HashMap<String, Double> values = baseFeature.getFeatureScores(instance);
            value = values.get(baseName);
        }
        return value.doubleValue();
    }

    @Override
    public HashMap<String, Double> getFeatureScores(ENEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        double value = getBaseValue(instance);
        // have all
        assert (min.containsKey(instance.set) && max.containsKey(instance.set));
        double tempMin = min.get(instance.set)[0];
        double tempMax = max.get(instance.set)[0];
        // not equal
        if(tempMax == tempMin){
            System.out.println(tempMax+" : "+instance);
        }
        assert (tempMax != tempMin);
        double score = (value - tempMin) / (tempMax - tempMin);
        result.put(name, score);
        return result;
    }
}

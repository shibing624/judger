package org.xm.judger.features.english;


import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author xuming
 */
public class ENGaussianNormailizerFeature implements ENFeatures {
    private HashMap<Integer, double[]> means;
    private HashMap<Integer, double[]> stddev;
    private ENFeatures baseFeature;
    private String baseName;
    private String name;
    private Type type;

    public enum Type {
        ZSCORE,
        ABS_ZSCORE,
        NORMAL_PROB
    }

    /**
     * Learn the mean and stddev of the feature
     *
     * @param trainingSample the instance
     * @param base
     * @param baseName
     * @param type
     */
    public ENGaussianNormailizerFeature(ArrayList<ENEssayInstance> trainingSample, ENFeatures base, String baseName, Type type) {
        means = new HashMap<>();
        stddev = new HashMap<>();
        this.baseFeature = base;
        this.baseName = baseName;
        this.name = baseName + "_" + type.toString().toLowerCase() + "_gaussiannorm_task";
        this.type = type;
        HashMap<Integer, int[]> docs = new HashMap<>();
        // compute the sums
        for (ENEssayInstance instance : trainingSample) {
            double value = getBaseValue(instance);
            if (!means.containsKey(instance.set))
                means.put(instance.set, new double[]{value});
            else means.get(instance.set)[0] += value;

            if (!docs.containsKey(instance.set))
                docs.put(instance.set, new int[]{1});
            else docs.get(instance.set)[0]++;
        }
        // convert to mean
        for (Integer i : means.keySet()) means.get(i)[0] /= docs.get(i)[0];
        // compute standard deviation
        for (ENEssayInstance instance : trainingSample) {
            double value = getBaseValue(instance);
            double dev = means.get(instance.set)[0] - value;
            dev *= dev;
            if (!stddev.containsKey(instance.set))
                stddev.put(instance.set, new double[]{dev});
            else stddev.get(instance.set)[0] += dev;
        }
        // normalize the stddev
        for (Integer i : stddev.keySet()) stddev.get(i)[0] = Math.sqrt(stddev.get(i)[0] / (docs.get(i)[0] - 1));
        // debug
        if (Config.DEBUG) {
            List<Integer> tasks = new ArrayList<>(means.keySet());
            Collections.sort(tasks);
            for (Integer task : tasks) {
                System.out.println("Feature " + baseName + " for task/set " + task + ":");
                System.out.println("\tx: " + means.get(task)[0]);
                System.out.println("\ts: " + stddev.get(task)[0]);
            }
        }
    }

    /**
     * Checks to see if base value is already computed.
     * Otherwise computes the value and stores it.
     *
     * @param instance
     * @return
     */
    private double getBaseValue(ENEssayInstance instance) {
        Double value = instance.getFeature(baseName);
        if (value == null) {
            // duplicate features
            HashMap<String, Double> values = baseFeature.getFeatureScores(instance);
            instance.setFeature(baseName, values.get(baseName));
            value = values.get(baseName);
        }
        return value.doubleValue();
    }

    @Override
    public HashMap<String, Double> getFeatureScores(ENEssayInstance instance) {
        HashMap<String, Double> values = new HashMap<>();
        double value = getBaseValue(instance);
        // missing features
        assert (means.containsKey(instance.set) && stddev.containsKey(instance.set));
        double tempMean = means.get(instance.set)[0];
        double tempStddev = stddev.get(instance.set)[0];
        // not zero
        assert (tempStddev != 0);
        double core = (value - tempMean) / tempStddev;
        if (type == Type.ABS_ZSCORE)
            core = Math.abs(core);
        else if (type == Type.NORMAL_PROB)
            core = (Math.exp(-Math.pow(core, 2) / 2)) / (tempStddev * Math.sqrt(2 * Math.PI));
        values.put(name, core);
        return values;
    }
}

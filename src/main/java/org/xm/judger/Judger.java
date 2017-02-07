package org.xm.judger;

import org.xm.judger.domain.EssayInstance;

import java.util.ArrayList;

/**
 * @author xuming
 */
public class Judger {
    private static ArrayList<EssayInstance> instances;

    public static ArrayList<EssayInstance> getInstances() {
        return instances;
    }

    public static void setInstances(ArrayList<EssayInstance> instances) {
        Judger.instances = instances;
    }

    /**
     * 作文得分
     */
    public static double cilinSimilarity(String word1, String word2) {
        return 0.0;
    }
}

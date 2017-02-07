package org.xm.judger;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.ENEssayInstance;

import java.util.ArrayList;

/**
 * @author xuming
 */
public class Judger {
    private static ArrayList<ENEssayInstance> eninstances;

    public static ArrayList<ENEssayInstance> getENInstances() {
        return eninstances;
    }

    public static void setENInstances(ArrayList<ENEssayInstance> instances) {
        Judger.eninstances = instances;
    }

    private static ArrayList<CNEssayInstance> cninstances;

    public static ArrayList<CNEssayInstance> getCNInstances() {
        return cninstances;
    }

    public static void setCNInstances(ArrayList<CNEssayInstance> instances) {
        Judger.cninstances = instances;
    }

    /**
     * 作文得分
     */
    public static double cilinSimilarity(String word1, String word2) {
        return 0.0;
    }
}

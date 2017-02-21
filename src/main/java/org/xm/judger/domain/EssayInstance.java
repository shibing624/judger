package org.xm.judger.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Essay instance
 *
 * @author xuming
 */
public abstract class EssayInstance {
    //storing all the fields available in the training set file
    public int id;
    public int set;
    public String essay;
    public String title;
    public String filename;
    public int rater1_domain1 = -1;
    public int rater2_domain1 = -1;
    public int rater3_domain1 = -1;
    public int domain1_score = -1;
    public int rater1_domain2 = -1;
    public int rater2_domain2 = -1;
    public int domain2_score = -1;

    public int rater1_trait1 = -1;
    public int rater1_trait2 = -1;
    public int rater1_trait3 = -1;
    public int rater1_trait4 = -1;
    public int rater1_trait5 = -1;
    public int rater1_trait6 = -1;

    public int rater2_trait1 = -1;
    public int rater2_trait2 = -1;
    public int rater2_trait3 = -1;
    public int rater2_trait4 = -1;
    public int rater2_trait5 = -1;
    public int rater2_trait6 = -1;

    public int rater3_trait1 = -1;
    public int rater3_trait2 = -1;
    public int rater3_trait3 = -1;
    public int rater3_trait4 = -1;
    public int rater3_trait5 = -1;
    public int rater3_trait6 = -1;

    ArrayList<ArrayList<ArrayList<String>>> cachedParse = null;
    HashMap<String, Double> features;

    public EssayInstance() {
        this.features = new HashMap<>();
    }

    public void setFeature(HashMap<String, Double> featureScores) {
        for (String key : featureScores.keySet()) {
            if (features.containsKey(key))
                features.put(key.concat("1"), featureScores.get(key));
            else
                features.put(key, featureScores.get(key));
        }
    }


    public void setFeature(String feature, double value) {
        features.put(feature, value);
    }

    public HashMap<String, Double> getFeatures() {
        return this.features;
    }

    public double getFeature(String feature) {
        if (feature.equals("grade"))
            return domain1_score;
        return features.get(feature);
    }

    public List<String> listFeatures() {
        ArrayList<String> fList = new ArrayList<>(features.keySet());
        Collections.sort(fList);
        return fList;
    }

    /**
     * Paragraphs
     *
     * @return the parsed structure of the text at the paragraph, sentence, word levels
     */
    public abstract ArrayList<ArrayList<ArrayList<String>>> getParagraphs();

    /**
     * get token(String) from paragraphs
     *
     * @return the parsed the paragraph to word levels
     */
    public static List<String> getTokens(EssayInstance instance) {
        List<String> tokens = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) paragraph.forEach(tokens::addAll);
        return tokens;
    }

    /**
     * toString
     *
     * @return shows ID and parsed contents
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ID(" + id + "): ");
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                for (String token : sentence) {
                    sb.append(token);
                    sb.append(" ");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}


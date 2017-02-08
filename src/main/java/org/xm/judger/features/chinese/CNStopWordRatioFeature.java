package org.xm.judger.features.chinese;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.xmnlp.dic.DicReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Stop word num / all word num
 *
 * @author xuming
 */
public class CNStopWordRatioFeature implements CNFeatures {
    HashSet<String> stopwords;
    private static final String PATH = Config.StopwordsPath;

    public CNStopWordRatioFeature() throws IOException {
        this(PATH);
    }

    public CNStopWordRatioFeature(String stopwordPath) throws IOException {
        stopwords = new HashSet<>();
        BufferedReader br = DicReader.getReader(stopwordPath);
        String line;
        while ((line = br.readLine()) != null) {
            line.trim();
            if (line.length() == 0 || line.charAt(0) == '#')
                continue;
            stopwords.add(line.toLowerCase());
        }
        br.close();
    }

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        int numStopwords = 0;
        int numWords = 0;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                for (String token : sentence) {
                    numWords++;
                    if (stopwords.contains(token.toLowerCase()))
                        numStopwords++;
                }
            }
        }
        result.put("stopword_ratio", numStopwords / (double) numWords);
        return result;
    }
}

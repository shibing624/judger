package org.xm.judger.features.english;

import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;
import org.xm.xmnlp.dic.DicReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * stop word num / all word num
 *
 * @author xuming
 */
public class ENStopWordRatioFeature implements ENFeatures {
    HashSet<String> stopwords;
    private static final String PATH = Config.StopwordsPath;

    public ENStopWordRatioFeature() throws IOException {
        this(PATH);
    }

    public ENStopWordRatioFeature(String stopwordPath) throws IOException {
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
    public HashMap<String, Double> getFeatureScores(ENEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        int numStopwords = 0;
        int numWords = 0;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                for (String token : sentence) {
                    // filter nonsense words
                    if (token.length() == 1 && !token.matches("\\w"))
                        continue;
                    if (stopwords.contains(token.toLowerCase()))
                        numStopwords++;
                    numWords++;
                }
            }
        }
        result.put("stopword_ratio", numStopwords / (double) numWords);
        return result;
    }

}

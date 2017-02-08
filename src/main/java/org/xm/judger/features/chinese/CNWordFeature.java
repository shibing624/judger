package org.xm.judger.features.chinese;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.xmnlp.util.LexiconUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Percent OOV, Percent obvious typos, need a word list to spell check
 *
 * @author xuming
 */
public class CNWordFeature implements CNFeatures {
    public CNWordFeature() throws IOException {
        // add punctuation symbols
        String[] punctuation = new String[]{",","，", ".","。", "?", "？","-", "!", "！","'", "\"", "(", ")","（","）",
                "$", ":","：", ";","；","“","”","《","》"};
    }

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        int numWords = 0;
        int matches = 0;
        int numTypos = 0;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                for (String token : sentence) {
                    if (LexiconUtil.getAttribute(token.toLowerCase())!=null)
                        matches++;
                    else
                        numTypos++;
                    numWords++;
                }
            }
        }
        result.put("OOVs", new Double(1 - matches / (double) numWords)); // 未登录词 占比
        result.put("obvious_typos", new Double(numTypos / (double) numWords)); // 明显错误词 占比
        if (Config.DEBUG) {
            System.out.println("OOVs for ID(" + instance.id + "): " + result.get("OOVs"));
            System.out.println("Obvious typos for ID(" + instance.id + "): " + result.get("obvious_typos"));
        }
        return result;
    }
}

package org.xm.judger.features.chinese;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.xmnlp.corpus.tag.Nature;
import org.xm.xmnlp.dictionary.CoreDictionary;
import org.xm.xmnlp.dictionary.CustomDictionary;
import org.xm.xmnlp.util.LexiconUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 未登录词（OOV）占比、错误词（obvious typos）占比
 * 类符形符比（TTR，type/token ratio）：可以反映文本中词汇的丰富程度
 * 动词的类符形符比、副词的类符形符比、介词类数以及代词数量
 * <p>
 * 类符形符比：一个语料库有100万词，指的是100万tokens，即100万词次。但这100万词次的语料库中，或许只用到了5万个单词。
 * 这5万个词，就是types，词种。
 *
 * @author xuming
 */
public class CNWordFeature implements CNFeatures {
    // add punctuation symbols
    String[] punctuation = new String[]{",", "，", ".", "。", "?", "？", "-", "!", "！", "'", "\"", "(", ")", "（", "）",
            "$", ":", "：", ";", "；", "“", "”", "《", "》"};

    @Override
    public HashMap<String, Double> getFeatureScores(CNEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        int numChars = 0; // 总字数
        int numWords = 0; // 总词语数
        int matches = 0;
        int numTypos = 0;
        int wordTotalSize = 0; // 分词词典的词语总数
        int numVerb = 0; // 动词
        int numAdverb = 0; // 副词
        int numPrePosition = 0; // 介词
        int numPronoun = 0; // 代词
        if (CustomDictionary.dat.size() != 0 || CoreDictionary.trie.size() != 0)
            wordTotalSize += CustomDictionary.dat.size() + CoreDictionary.trie.size();
        else wordTotalSize = 153115;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                for (String token : sentence) {
                    numWords++;
                    numChars += token.length();
                    CoreDictionary.Attribute attr = LexiconUtil.getAttribute(token.toLowerCase());
                    if (attr != null) {
                        matches++;
                        if (attr.hasNature(Nature.v)) numVerb++;
                        if (attr.hasNature(Nature.d)) numAdverb++;
                        if (attr.hasNature(Nature.p)) numPrePosition++;
                        if (attr.hasNature(Nature.r)) numPronoun++;
                    } else
                        numTypos++;
                }
            }
        }
        result.put("OOVs", new Double(1 - matches / (double) numWords)); // 未登录词 占比
        result.put("obvious_typos", new Double(numTypos / (double) numWords)); // 明显错误词 占比
        result.put("TTR", new Double(matches / (double) wordTotalSize)); // 类符形符比
        result.put("Verb_TTR", new Double(numVerb / (double) wordTotalSize)); // 动词的类符形符比
        result.put("Adverb_TTR", new Double(numAdverb / (double) wordTotalSize)); // 副词的类符形符比
        result.put("Num_PrePosition", new Double(numPrePosition)); // 介词数
        result.put("Num_Pronoun", new Double(numPronoun)); // 代词数
        result.put("Num_Chars", new Double(numChars)); // 总字数
        result.put("Num_Words", new Double(numWords)); // 总词数
        if (Config.DEBUG) {
            System.out.println("OOVs for ID(" + instance.id + "): " + result.get("OOVs"));
            System.out.println("Obvious typos for ID(" + instance.id + "): " + result.get("obvious_typos"));
            System.out.println("TTR for ID(" + instance.id + "): " + result.get("TTR"));
            System.out.println("Verb_TTR for ID(" + instance.id + "): " + result.get("Verb_TTR"));
            System.out.println("Adverb_TTR for ID(" + instance.id + "): " + result.get("Adverb_TTR"));
            System.out.println("Num_PrePosition for ID(" + instance.id + "): " + result.get("Num_PrePosition"));
            System.out.println("Num_Pronoun for ID(" + instance.id + "): " + result.get("Num_Pronoun"));
            System.out.println("Num_Chars for ID(" + instance.id + "): " + result.get("Num_Chars"));
        }
        return result;
    }
}


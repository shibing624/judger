package org.xm.judger.features.english;

import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;
import org.xm.xmnlp.dic.DicReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Percent OOV, Percent obvious typos, need a word list to spell check
 * 检查词拼写
 * 包括 词库中词语set，词库中词语小写set，拼写错误set（即未登录词），拼写小写错误set
 *
 * @author xuming
 */
public class ENWordFeature implements ENFeatures {
    HashSet<String> lexicon = null;
    HashSet<String> lexiconLC = null;
    HashSet<String> typos = null;
    HashSet<String> typosLC = null;

    private static final String PATH = Config.ENSpellCheckingWordsPath;

    public ENWordFeature() throws IOException {
        this(PATH);
    }

    public ENWordFeature(String wordDictionaryPath) throws IOException {
        lexicon = new HashSet<>();
        lexiconLC = new HashSet<>();
        typos = new HashSet<>();
        typosLC = new HashSet<>();
        BufferedReader br = DicReader.getReader(wordDictionaryPath);
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            lexicon.add(line);
            lexiconLC.add(line.toLowerCase());
        }
        br.close();
        // typo list
        // generate typos from word list
        Pattern iePattern = Pattern.compile("ie");
        Pattern eiPattern = Pattern.compile("ei");
        Pattern doubleLetterPattern = Pattern.compile("(\\w)\\1");
        // the error about 's
        Pattern aposPattern = Pattern.compile("'(?!s)");
        for (String word : lexicon) {
            Matcher m = iePattern.matcher(word);
            if (m.find()) {
                String candidate = m.replaceFirst("ei");
                if (!lexicon.contains(candidate)) {
                    typos.add(candidate);
                    if (Config.DEBUG)
                        System.out.println("potential typo: " + candidate);
                }
                if (!lexiconLC.contains(candidate.toLowerCase()))
                    typosLC.add(candidate.toLowerCase());
            }
            m = eiPattern.matcher(word);
            if (m.find()) {
                String candidate = m.replaceFirst("ie");
                if (!lexicon.contains(candidate)) {
                    typos.add(candidate);
                    if (Config.DEBUG)
                        System.out.println("potential typo: " + candidate);
                }
                if (!lexiconLC.contains(candidate.toLowerCase()))
                    typosLC.add(candidate.toLowerCase());
            }

            m = doubleLetterPattern.matcher(word);
            if (m.find()) {
                String candidate = m.replaceFirst(m.group(1));
                if (!lexicon.contains(candidate)) {
                    typos.add(candidate);
                    if (Config.DEBUG)
                        System.out.println("potential typo: " + candidate);
                }
                if (!lexiconLC.contains(candidate.toLowerCase()))
                    typosLC.add(candidate.toLowerCase());
            }
            m = aposPattern.matcher(word);
            if (m.find()) {
                String candidate = m.replaceFirst("");
                if (!lexicon.contains(candidate)) {
                    typos.add(candidate);
                    if (Config.DEBUG)
                        System.out.println("potential typo: " + candidate);
                }
                if (!lexiconLC.contains(candidate.toLowerCase()))
                    typosLC.add(candidate.toLowerCase());
            }
        }
        // add punctuation symbols
        String[] punctuation = new String[]{",", ".", "?", "-", "!", "'", "\"", "(", ")", "$", ":", ";"};
        for (String i : punctuation) {
            lexicon.add(i);
            lexiconLC.add(i);
        }
    }

    @Override
    public HashMap<String, Double> getFeatureScores(ENEssayInstance instance) {
        HashMap<String, Double> result = new HashMap<>();
        int numWords = 0;
        int matches = 0;
        int numTypos = 0;
        ArrayList<ArrayList<ArrayList<String>>> paragraphs = instance.getParagraphs();
        for (ArrayList<ArrayList<String>> paragraph : paragraphs) {
            for (ArrayList<String> sentence : paragraph) {
                boolean initialCaps = true;
                for (String token : sentence) {
                    // ignore @tags
                    if (token.charAt(0) == '@') {
                        initialCaps = false;
                        continue;
                    }
                    if (initialCaps) {
                        if (lexiconLC.contains(token.toLowerCase()))
                            matches++;
                        else if (typosLC.contains(token.toLowerCase()))
                            numTypos++;
                    } else {
                        if (lexicon.contains(token))
                            matches++;
                        else if (typos.contains(token))
                            numTypos++;
                    }
                    numWords++;
                    initialCaps = false;
                    // opening quote
                    if (token.equals("\""))
                        initialCaps = true;
                }
            }
        }
        result.put("OOVs", new Double(1 - matches / (double) numWords));
        result.put("obvious_typos", new Double(numTypos / (double) numWords));
        if (Config.DEBUG) {
            System.out.println("OOVs for ID(" + instance.id + "): " + result.get("OOVs"));
            System.out.println("Obvious typos for ID(" + instance.id + "): " + result.get("obvious_typos"));
        }
        return result;
    }

}

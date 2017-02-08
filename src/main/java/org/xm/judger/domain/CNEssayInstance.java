package org.xm.judger.domain;

import org.xm.xmnlp.Xmnlp;
import org.xm.xmnlp.seg.domain.Term;
import org.xm.xmnlp.util.StringUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author xuming
 */
public class CNEssayInstance extends EssayInstance {

    private static final Pattern paragraphPattern = Pattern.compile("\\s{2,}");

    @Override
    public ArrayList<ArrayList<ArrayList<String>>> getParagraphs() {
        if (cachedParse != null) return cachedParse;
        cachedParse = new ArrayList<>();
        String[] paragraphs = paragraphPattern.split(essay);
        for (String paragraph : paragraphs) {
            ArrayList<ArrayList<String>> sentenceList = new ArrayList<>();
            cachedParse.add(sentenceList);
            // get sentence
            List<String> sentences = splitSentence(paragraph);
            for (String sentence : sentences) {
                ArrayList<String> wordList = new ArrayList<>();
                sentenceList.add(wordList);
                // get token
                List<Term> tokens = Xmnlp.segment(sentence);
                for (Term token : tokens) {
                    if (token == null || StringUtil.isBlank(token.word))
                        continue;
//                    if (token.nature.startsWith("w"))
//                        continue;
                    wordList.add(token.word);
                }
            }
        }
        return cachedParse;
    }

    /**
     * 把文章分割为句子
     *
     * @param document
     * @return
     */
    private List<String> splitSentence(String document) {
        List<String> sentences = new ArrayList<String>();
        for (String line : document.split("[\r\n]")) {
            line = line.trim();
            if (line.length() == 0) continue;

            int i = 0;
            for (String s : line.split("[,，.。；;“”？?!！：:]")) {
                s = s.trim();
                if (s.length() == 0)
                    continue;
                int step = s.length() + 1;
                if (i + step >= line.length())
                    s = line.substring(i);
                else s = line.substring(i, i + step);
                i += step;
                sentences.add(s);
            }

        }
        return sentences;
    }

    /**
     * print essay instance info
     *
     * @param instances
     */
    public static void printEssayInstances(ArrayList<CNEssayInstance> instances, String outFile) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("UTF-8")));
            for (CNEssayInstance essay : instances)
                out.println(essay);
            out.close();
        } catch (IOException e) {
            System.err.println("Failure to write to outfile: " + e);
        }
    }

}

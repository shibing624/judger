package org.xm.judger.domain;

import org.xm.judger.util.Tokenizer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 中文作文实例
 *
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
                List<Tokenizer.Word> tokens = Tokenizer.segment(sentence);
                for (Tokenizer.Word token : tokens) {
                    if (token == null || token.getName().length() == 0)
                        continue;
                    wordList.add(token.getName());
                }
            }
        }
        return cachedParse;
    }

    /**
     * 把文章分割为句子
     *
     * @param document 作文
     * @return list
     */
    public List<String> splitSentence(String document) {
        List<String> sentences = new ArrayList<String>();
        for (String line : document.split("[\r\n]")) {
            line = line.trim();
            if (line.length() == 0) continue;

            int i = 0;
            for (String s : line.split("[,，.。；;“”？?!！：:]")) {
            //for (String s : line.split("[.。？?!！]")) {
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
     * @param instances 实例
     */
    public static void printEssayInstances(ArrayList<CNEssayInstance> instances, String outFile) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("UTF-8")));
            instances.forEach(out::println);
            out.close();
        } catch (IOException e) {
            System.err.println("Failure to write to outfile: " + e);
        }
    }

}

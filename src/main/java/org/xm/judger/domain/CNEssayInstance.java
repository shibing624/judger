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

    public static final Pattern paragraphPattern = Pattern.compile("\\s{3,}");
    public static final Pattern sentencePattern = Pattern.compile("(?<=[\\.?!][^\\w\\s]?)\\s+(?![a-z])");

    @Override
    public ArrayList<ArrayList<ArrayList<String>>> getParagraphs() {
        if (cachedParse != null) return cachedParse;
        cachedParse = new ArrayList<>();
        String[] paragraphs = paragraphPattern.split(essay);
        for (String paragraph : paragraphs) {
            ArrayList<ArrayList<String>> sentenceList = new ArrayList<>();
            cachedParse.add(sentenceList);
            // get sentence
            String[] sentences = sentencePattern.split(paragraph);
            for (String sentence : sentences) {
                ArrayList<String> wordList;
                wordList = new ArrayList<>();
                sentenceList.add(wordList);
                // get token
                List<Term> tokens = Xmnlp.segment(sentence);
                for (Term token : tokens) {
                    if (token == null || StringUtil.isNotBlank(token.word))
                        continue;
                    if(token.nature.startsWith("w"))
                        continue;
                    wordList.add(token.word);
                }
            }
        }
        return cachedParse;
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
                out.println(essay + "\n");
            out.close();
        } catch (IOException e) {
            System.err.println("Failure to write to outfile: " + e);
        }
    }

}

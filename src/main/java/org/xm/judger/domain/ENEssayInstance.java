package org.xm.judger.domain;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xuming
 */
public class ENEssayInstance extends EssayInstance {
    private static final Pattern paragraphPattern = Pattern.compile("\\s{3,}");
    private static final Pattern sentencePattern = Pattern.compile("(?<=[\\.?!][^\\w\\s]?)\\s+(?![a-z])");
    private static final Pattern wordPattern = Pattern.compile("\\s+");
    private static final Pattern frontPattern = Pattern.compile("([^\\w@]+)(\\w@].*)");
    private static final Pattern backPattern = Pattern.compile("(.*\\w)(\\W+)");

    /**
     * EN abbreviation
     */
    private static final Pattern endsInAbbreviation = Pattern.compile(".*(Mr|Mrs|Dr|Jr|Ms|Prof|Sr|dept|Univ|Inc|Ltd|Co" +
            "|Corp|Mt|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Oct|Nov|Dec|Sept|vs|etc|no)\\.");

    @Override
    public ArrayList<ArrayList<ArrayList<String>>> getParagraphs() {
        if (cachedParse != null) return cachedParse;
        cachedParse = new ArrayList<>();
        String[] paragraphs = paragraphPattern.split(essay);
        for (String paragraph : paragraphs) {
            ArrayList<ArrayList<String>> sentenceList = new ArrayList<>();
            cachedParse.add(sentenceList);
            // boring sentence splitter
            String[] sentences = sentencePattern.split(paragraph);
            // load an abbreviation list and merge sentences back ending in abbrevs
            boolean merageIntoPrevious = false;
            for (String sentence : sentences) {
                ArrayList<String> wordList;
                if (merageIntoPrevious)
                    wordList = sentenceList.get(sentenceList.size() - 1);
                else {
                    wordList = new ArrayList<>();
                    sentenceList.add(wordList);
                }

                //next sentence
                if (endsInAbbreviation.matcher(sentence).matches())
                    merageIntoPrevious = true;
                else merageIntoPrevious = false;
                // split on spaces, then strip off leading and trailing punctuation
                String[] tokens = wordPattern.split(sentence);
                for (String token : tokens) {
                    // empty token
                    if (token.length() == 0)
                        continue;
                    Matcher m = frontPattern.matcher(token);
                    if (m.matches()) {
                        // explode group 1
                        String front = m.group(1);
                        for (int i = 0; i < front.length(); i++)
                            wordList.add(String.valueOf(front.charAt(i)));

                        // group 2
                        token = m.group(2);
                    }
                    m = backPattern.matcher(token);
                    if (m.matches()) {
                        // save group 1
                        wordList.add(m.group(1));
                        // explode group 2
                        String back = m.group(2);
                        for (int i = 0; i < back.length(); i++)
                            wordList.add(String.valueOf(back.charAt(i)));
                    } else wordList.add(token);
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
    public static void printEssayInstances(ArrayList<ENEssayInstance> instances, String outFile) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("UTF-8")));
            for (EssayInstance essay : instances)
                out.println(essay);
            out.close();
        } catch (IOException e) {
            System.err.println("Failure to write to outfile: " + e);
        }
    }
}

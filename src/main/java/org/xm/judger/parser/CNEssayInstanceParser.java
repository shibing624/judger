package org.xm.judger.parser;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.xmnlp.dic.DicReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse the CN essay instance
 *
 * @author xuming
 */
public class CNEssayInstanceParser {
    /**
     * match CSV-escaped double quotes
     */
    public static final Pattern escapedDoubleQuote = Pattern.compile("\"\"");

    public ArrayList<CNEssayInstance> parse(String filename, boolean header) {
        ArrayList<CNEssayInstance> essayInstances = new ArrayList<>();
        BufferedReader br;
        try {
            br = DicReader.getReader(filename);
            String line;
            while ((line = br.readLine()) != null) {
                if (header == true) {
                    header = false;
                    continue;
                }
                CNEssayInstance essay = parseFields(line);
                essayInstances.add(essay);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return essayInstances;
    }

    private CNEssayInstance parseFields(String line) {
        String[] fields = line.split("\t");
        CNEssayInstance essay = new CNEssayInstance();
        essay.id = parseInt(fields[0]);
        essay.set = parseInt(fields[1]);
        essay.essay = csvUnescape(fields[2]);
        if (fields.length <= 3) return essay;
        essay.rater1_domain1 = parseInt(fields[3]);
        if (fields.length <= 4) return essay;
        essay.rater2_domain1 = parseInt(fields[4]);
        if (fields.length <= 5) return essay;
        essay.rater3_domain1 = parseInt(fields[5]);
        if (fields.length <= 6) return essay;
        essay.domain1_score = parseInt(fields[6]);

        if (fields.length <= 7) return essay;
        essay.rater1_domain2 = parseInt(fields[7]);
        if (fields.length <= 8) return essay;
        essay.rater2_domain2 = parseInt(fields[8]);
        if (fields.length <= 9) return essay;
        essay.domain2_score = parseInt(fields[9]);

        return essay;
    }

    private int parseInt(String input) {
        if (input.equals("")) return -1;
        return Integer.parseInt(input);
    }

    /**
     * escaping of the string
     *
     * @param field
     * @return
     */
    private static String csvUnescape(String field) {
        StringBuilder sb = new StringBuilder(field);
        if (sb.length() > 0 && sb.charAt(0) == '"') sb.deleteCharAt(0);
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '"') sb.deleteCharAt(sb.length() - 1);
        Matcher m = escapedDoubleQuote.matcher(sb);
        return m.replaceAll("\"");
    }
}

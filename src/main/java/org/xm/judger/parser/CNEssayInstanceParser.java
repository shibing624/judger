package org.xm.judger.parser;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.EssayInstance;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse the CN essay instance
 *
 * @author xuming
 */
public class CNEssayInstanceParser implements EssayInstanceParser {
    /**
     * match CSV-escaped double quotes
     */
    public static final Pattern escapedDoubleQuote = Pattern.compile("\"\"");
    @Override
    public ArrayList<EssayInstance> parse(String filename, boolean header) {
        return null;
    }

    public EssayInstance parseFields(String line) {
        String[] fields = line.split("\t");
        EssayInstance essay = new CNEssayInstance();
        essay.id = parseInt(fields[0]);
        essay.set = parseInt(fields[1]);
        essay.essay = csvUnescape(fields[2]);
        essay.rater1_domain1 = parseInt(fields[3]);
        essay.rater2_domain1 = parseInt(fields[4]);
        essay.rater3_domain1 = parseInt(fields[5]);
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

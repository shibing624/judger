package org.xm.judger.parser;

import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;
import org.xm.xmnlp.dic.DicReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse the EN essay instance
 *
 * @author xuming
 */
public class ENEssayInstanceParser {

    /**
     * match CSV-escaped double quotes
     */
    public static final Pattern escapedDoubleQuote = Pattern.compile("\"\"");
    public static final Pattern improperSingleQuote = Pattern.compile("[\u2018\u2019]");
    public static final Pattern improperDoubleQuote = Pattern.compile("[\201C\u201D]");
    public static final Pattern dashes = Pattern.compile("[\u2013\u2014]");

    /**
     * match text like don''t or won''t
     */
    public static final Pattern escapedFunkyQuote1 = Pattern.compile("(?<=\\w)''(?=\\w)");
    /**
     * match double-quotes
     */
    public static final Pattern escapedFunkyQuote2 = Pattern.compile("''");

    public ArrayList<ENEssayInstance> parse(String filename, boolean header) {
        ArrayList<ENEssayInstance> essayInstances = new ArrayList<>();
        BufferedReader br;
        try {
            br = DicReader.getReader(filename);
            String line;
            while ((line = br.readLine()) != null) {
                if (header == true) {
                    header = false;
                    continue;
                }
                ENEssayInstance essay = parseFields(line);
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

    private ENEssayInstance parseFields(String line) {
        String[] fields = line.split("\t");
        ENEssayInstance essay = new ENEssayInstance();
        essay.id = parseInt(fields[0]);
        essay.set = parseInt(fields[1]);
        essay.essay = cleanupText(csvUnescape(fields[2]));
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
        if (fields.length <= 10) return essay;
        essay.rater1_trait1 = parseInt(fields[10]);
        if (fields.length <= 11) return essay;
        essay.rater1_trait2 = parseInt(fields[11]);
        if (fields.length <= 12) return essay;
        essay.rater1_trait3 = parseInt(fields[12]);
        if (fields.length <= 13) return essay;
        essay.rater1_trait4 = parseInt(fields[13]);
        if (fields.length <= 14) return essay;
        essay.rater1_trait5 = parseInt(fields[14]);
        if (fields.length <= 15) return essay;
        essay.rater1_trait6 = parseInt(fields[15]);
        if (fields.length <= 16) return essay;
        essay.rater2_trait1 = parseInt(fields[16]);
        if (fields.length <= 17) return essay;
        essay.rater2_trait2 = parseInt(fields[17]);
        if (fields.length <= 18) return essay;
        essay.rater2_trait3 = parseInt(fields[18]);
        if (fields.length <= 19) return essay;
        essay.rater2_trait4 = parseInt(fields[19]);
        if (fields.length <= 20) return essay;
        essay.rater2_trait5 = parseInt(fields[20]);
        if (fields.length <= 21) return essay;
        essay.rater2_trait6 = parseInt(fields[21]);
        if (fields.length <= 22) return essay;
        essay.rater3_trait1 = parseInt(fields[22]);
        if (fields.length <= 23) return essay;
        essay.rater3_trait2 = parseInt(fields[23]);
        if (fields.length <= 24) return essay;
        essay.rater3_trait3 = parseInt(fields[24]);
        if (fields.length <= 25) return essay;
        essay.rater3_trait4 = parseInt(fields[25]);
        if (fields.length <= 26) return essay;
        essay.rater3_trait5 = parseInt(fields[26]);
        if (fields.length <= 27) return essay;
        essay.rater3_trait6 = parseInt(fields[27]);
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

    /**
     * Cleans up the long-form text
     * there is a '' when there should be a "
     *
     * @param field
     * @return
     */
    private static String cleanupText(String field) {
        field = field.trim();
        Matcher m = improperSingleQuote.matcher(field);
        if (Config.DEBUG && m.find())
            System.out.println("Improper single quote found: " + field);
        field = m.replaceAll("'");

        m = improperDoubleQuote.matcher(field);
        if (Config.DEBUG && m.find())
            System.out.println("Improper double quote found: " + field);
        field = m.replaceAll("\"");

        m = escapedFunkyQuote1.matcher(field);
        if (Config.DEBUG && m.find())
            System.out.println("Funky single quote found: " + field);
        field = m.replaceAll("'");

        m = escapedFunkyQuote2.matcher(field);
        if (Config.DEBUG && m.find())
            System.out.println("Funky double quote found: " + field);
        field = m.replaceAll("\"");

        m = dashes.matcher(field);
        if (Config.DEBUG && m.find())
            System.out.println("Improper dash found: " + field);
        field = m.replaceAll("-");

        // check for any weirdo characters
        if (Config.DEBUG) {
            for (int i = 0; i < field.length(); i++) {
                char c = field.charAt(i);
                if (c > 127)
                    System.out.println("Extended ASCII:  " + c);
            }
        }

        return field;
    }

}

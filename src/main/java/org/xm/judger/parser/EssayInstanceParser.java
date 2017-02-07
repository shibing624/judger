package org.xm.judger.parser;

import org.xm.judger.domain.EssayInstance;
import org.xm.xmnlp.dic.DicReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * parse the essay instance
 *
 * @author xuming
 */
public interface EssayInstanceParser {

    /**
     * parse the essay train set
     * @param filename train set path
     * @param header has header; if true:skip the first line
     * @return
     */
    default ArrayList<EssayInstance> parse(String filename, boolean header) {
        ArrayList<EssayInstance> essayInstances = new ArrayList<>();
        BufferedReader br;
        try {
            br = DicReader.getReader(filename);
            String line;
            while ((line = br.readLine()) != null) {
                if (header == true) {
                    header = false;
                    continue;
                }
                EssayInstance essay = parseFields(line);
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

    EssayInstance parseFields(String line);

}

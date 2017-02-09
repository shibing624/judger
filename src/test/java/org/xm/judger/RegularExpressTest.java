package org.xm.judger;

import org.junit.Test;

/**
 * @author xuming
 */
public class RegularExpressTest {
    @Test
    public void test() {
        String[] words = "Dear reader, @ORGANIZATION1 has had a dramatic effect on human life. It ".split("\\s");
        Double d = Double.valueOf((double) words.length / 5);
    }
}

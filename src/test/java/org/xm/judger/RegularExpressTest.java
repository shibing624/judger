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

    @Test
    public void testSplit() {
        String line = "帽子上面有毛，毛上面的左右两边是一对耳朵，耳朵中间有个鼻子，鼻";
        String[] strs = line.split("[,，]");
        int k = 0;
        for (int j = 0; j < strs.length; j++) {
            String tmp = "";
            if (k + strs[j].length() + 1 >= line.length()) {
                tmp = line.substring(k, k + strs[j].length());
            } else {
                tmp = line.substring(k, k + strs[j].length() + 1);
            }

            k = k + strs[j].length() + 1;
            System.out.println(tmp);
        }

    }

}

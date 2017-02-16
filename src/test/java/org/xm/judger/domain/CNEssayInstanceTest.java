package org.xm.judger.domain;

import org.junit.Test;

/**
 * @author xuming
 */
public class CNEssayInstanceTest {
    String line = "帽子上面有毛，毛上面的左右两边是一对耳朵，耳朵中间有个鼻子：鼻子比较大！";

    @Test
    public void splitSentence() throws Exception {
        CNEssayInstance instance = new CNEssayInstance();
        instance.splitSentence(line).forEach(System.out::println);
    }

    @Test
    public void testSplit() {
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
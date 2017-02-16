package org.xm.judger.domain;

/**
 * @author xuming
 */
public class Config {
    public static final boolean DEBUG = true;
    /**
     * 英文停用词路径
     */
    public static String StopwordsPath = "stopwords.txt";
    /**
     * 英文拼写检查词典路径
     */
    public static String ENSpellCheckingWordsPath = "scowl-7.1/american_50.utf8";
    /**
     * 英文作文训练集路径
     */
//    public static String ENTrainSetPath ="Phase1/training_set_rel3.tsv";
    public static String ENTrainSetPath = "Phase1/training_set_rel4.tsv";

    public static String ENTrainTestSetPath = "Phase1/training_set1.test.tsv";
    /**
     * 中文作文训练集路径
     */
    public static String CNTrainSetPath = "Phase1/cn_training_set_rel1.tsv";

}

package org.xm.judger.analyzer;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.features.chinese.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.xm.judger.analyzer.CNFeatureBuilder.saveARFFRealClass;


/**
 * 特征处理者
 * 
 * 在已抽取的140个特征项中，不同的特征项组合对高分作文的判别性能差别较大，但是某些特征项可以稳定地反映作文质量。
 * 这些选出的特征项包括:句子数、平均句长、三级词汇占比、词长大于7的单词数、词长大于8的单词数、词长大于9的单词数、
 * 单词数量、类符形符比、平均词长、动词短语的数量、动词的类符形符比、副词的类符形符比、介词类数以及代词数量。
 *
 * 这些特征项从长度、词汇(比如词汇量、词汇多样性、复杂词汇和难词等)、搭配(比如动词短语等)、句子(比如复杂句式等)以及不同词性的词汇使用情况等不同角度反映作文的内在特点，每个特征项都有自己的用处:
 (1)句子数、平均句长反映了句子的复杂程度。
 (2)三级词汇占比反映了作文中难词的使用情况。单词数量反映了作文的长度，因为大学生英语写作的要求一般为120到150个单词，该特征能检查空作文、过短或者过长的作文。
 (3)平均词长反映了作文整体的单词复杂度。
 (4)类符形符比反映了作文使用的词汇多样性。
 (5)单词的类符数量反映了写作者的词汇量。
 (6)动词短语的数量，反映了写作者对动词掌握的情况，这个特征项已经经过前期相关学者的研究，证明其在大学英语写作中对作文分数具有较高的预测力。
 (7)动词的类符形符比、副词的类符形符比、介词类数以及代词数量反映了作文中各个词性词汇的掌握情况。
 *
 * @author xuming
 */
public class FeatureHandler {

    /**
     * normalization type for the zscore function
     */
    public enum NormType {
        BASIC,
        ABS,
        PROB
    }

    public static ArrayList<CNEssayInstance> getFeatures(ArrayList<CNEssayInstance> instances) {
        ArrayList<CNFeatures> CNFeaturesArrayList = new ArrayList<>();
        CNFeaturesArrayList.add(new CNSentenceLengthFeature());
        CNFeatures wordLengthFeature = new CNWordLengthFeature();
        CNFeaturesArrayList.add(wordLengthFeature);
        CNFeatures idfFeature = new CNIDFFeature(instances);
        CNFeaturesArrayList.add(idfFeature);
        CNFeatures coherenceFeature = new CNSentenceCoherenceFeature();
        CNFeaturesArrayList.add(coherenceFeature);
        // normalization
        CNFeaturesArrayList.add(new CNPercentMatchesFeature("，"));
        CNFeaturesArrayList.add(new CNPercentMatchesFeature("！"));
        CNFeaturesArrayList.add(new CNPercentMatchesFeature("？"));
        CNFeatures theFeature = new CNPercentMatchesFeature("这");
        CNFeaturesArrayList.add(theFeature);
        CNFeaturesArrayList.add(new CNPercentMatchesFeature("是"));
        // need dictionary
        CNFeatures wordFeature = null;
        try {
            wordFeature = new CNWordFeature();
            CNFeaturesArrayList.add(new CNStopWordRatioFeature());
        } catch (IOException e) {
            System.err.println("Unable to load words: " + e);
        }
        CNFeaturesArrayList.add(wordFeature);
        // primary features
        for (CNEssayInstance instance : instances) {
            for (CNFeatures CNFeatures : CNFeaturesArrayList)
                instance.setFeature(CNFeatures.getFeatureScores(instance));
        }
        ArrayList<CNFeatures> normlizationFeatures = new ArrayList<>();
        normlizationFeatures.add(new CNMinMaxNormalizerFeature(instances, wordFeature, "OOVs"));
        normlizationFeatures.add(new CNMinMaxNormalizerFeature(instances, wordFeature, "obvious_typos"));
        normlizationFeatures.add(new CNMinMaxNormalizerFeature(instances, wordLengthFeature, "AverageWordLength"));
        normlizationFeatures.add(new CNMinMaxNormalizerFeature(instances, idfFeature, "AverageIDF"));

        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, idfFeature, "AverageIDF", CNGaussianNormailizerFeature.Type.ABS_ZSCORE));
        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, idfFeature, "AverageIDF", CNGaussianNormailizerFeature.Type.ZSCORE));
        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, coherenceFeature, "overlap_coherence", CNGaussianNormailizerFeature.Type.ZSCORE));
        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, coherenceFeature, "overlap_coherence", CNGaussianNormailizerFeature.Type.ABS_ZSCORE));
        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, theFeature, "PercentMatches_\\Q这\\E", CNGaussianNormailizerFeature.Type.ZSCORE));
        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, theFeature, "PercentMatches_\\Q这\\E", CNGaussianNormailizerFeature.Type.NORMAL_PROB));
        normlizationFeatures.add(new CNGaussianNormailizerFeature(instances, theFeature, "PercentMatches_\\Q这\\E", CNGaussianNormailizerFeature.Type.ABS_ZSCORE));
        // compute normalization feature
        for (CNEssayInstance instance : instances) {
            for (CNFeatures feature : normlizationFeatures)
                instance.setFeature(feature.getFeatureScores(instance));
        }

        return instances;
    }

    public static String arffEscapeName(String name) {
        name = name.replaceAll("\\\\Q|\\\\E", "");    // strip \\Q \\E
        /*name = name.replaceAll("!", "exclamation_mark");
        name = name.replaceAll("\\?", "question_mark");
        name = name.replaceAll("\\.\\*", "dot_star");
        name = name.replaceAll(",", "comma");
        name = name.replaceAll("@", "at_sign");*/

        return name;
    }
    public static void saveFeatures(ArrayList<CNEssayInstance> instances, String path) {
        try {
            saveARFFRealClass(instances, path);
        } catch (IOException e) {
            System.err.println("Error saving ARFF: " + e);
        }
    }
}

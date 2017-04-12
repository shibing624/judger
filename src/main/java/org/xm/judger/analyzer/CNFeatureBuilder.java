package org.xm.judger.analyzer;

import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.features.chinese.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * 特征构建者
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
public class CNFeatureBuilder {

    /**
     * normalization type for the zscore function
     */
    public enum NormType {
        BASIC,
        ABS,
        PROB
    }

    public static ArrayList<CNEssayInstance> buildFeatures(ArrayList<CNEssayInstance> instances,int set) {
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

        //analysis feature
        CNFeatureAnalyzer.analysis(instances,set);
        return instances;
    }

    public static void saveAllFeatures(ArrayList<CNEssayInstance> instances,int set) {
        try {
            // generate an ARFF with real valued output class (for regression if possible)
            saveARFFRealClass(CNFeatureAnalyzer.filter(instances, set), "data/cn_training_essay1_real.arff");
            saveARFFDiscreteClass(CNFeatureAnalyzer.filter(instances, set), "data/cn_training_essay1_discrete.arff");
            // generate an ARFF where grade is turned into a binary feature based on the threshold (in this case over/under 8.5)
            saveARFFThresholdClass(CNFeatureAnalyzer.filter(instances, set), "data/cn_training_essay1_t8.5.arff", 8.5);
        } catch (IOException e) {
            System.err.println("Error saving ARFF: " + e);
        }
    }

    /**
     * Save the data as an ARFF file where grade is specified as a real-valued feature.
     * This type of output class doesn't work with most of Weka3.8.
     */
    public static void saveARFFRealClass(ArrayList<CNEssayInstance> instances, String filename) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(filename));
        printWriter.println("% Autogenerated by java saveARFFRealClass");
        printWriter.println("@relation " + filename);
        List<String> features = instances.get(0).listFeatures();
        for (String i : features)
            printWriter.println("@attribute " + arffEscapeName(i) + " real");
        printWriter.println("@attribute grade real");
        printWriter.println("@data");
        for (CNEssayInstance instance : instances) {
            for (String feature : features)
                printWriter.print(instance.getFeature(feature) + ",");
            printWriter.println(instance.getFeature("grade"));
            //printWriter.println(instance.title);
        }
        printWriter.close();
    }
    public static void saveFeatures(ArrayList<CNEssayInstance> instances, int set, String path) {
        try {
            saveARFFRealClass(CNFeatureAnalyzer.filter(instances, set), path);
        } catch (IOException e) {
            System.err.println("Error saving ARFF: " + e);
        }
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

    /**
     * Save the data as an ARFF file where grade is specified as a discrete feature.
     * This type of output class works with Weka, but the machine learning won't
     * take into account that it's really a continuous scale.
     */
    public static void saveARFFDiscreteClass(ArrayList<CNEssayInstance> instances, String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        out.println("% Auto generated by java saveARFFDiscreteClass");
        out.println("@relation training_essay_set_1");

        List<String> features = instances.get(0).listFeatures();
        for (String feature : features) {
            out.println("@attribute " + arffEscapeName(feature) + " real");
        }

        HashMap<Double, int[]> histogram = CNFeatureAnalyzer.buildHistogram(instances, "grade");
        out.print("@attribute grade {");
        ArrayList<Double> values = new ArrayList<Double>(histogram.keySet());
        Collections.sort(values);
        for (int i = 0; i < values.size(); i++) {
            if (i > 0)
                out.print(",");
            out.print(values.get(i));
        }
        out.println("}");
        out.println("@data");
        for (CNEssayInstance instance : instances) {
            for (String feature : features)
                out.print(instance.getFeature(feature) + ",");
            out.println(instance.getFeature("grade"));
        }

        out.close();
    }

    /**
     * Save the data as an ARFF file where grade is specified as a binary
     * feature of less-than or greater-than-or-equal-to the threshold.
     * This type of feature works nicely with Weka.
     */
    public static void saveARFFThresholdClass(ArrayList<CNEssayInstance> instances, String filename, double gradeThreshold)
            throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        out.println("% Auto generated by java");
        out.println("@relation training_essay_set_1");

        List<String> features = instances.get(0).listFeatures();
        for (String feature : features) {
            out.println("@attribute " + arffEscapeName(feature) + " real");
        }
        out.println("@attribute grade {0,1}");
        out.println("@data");
        for (CNEssayInstance instance : instances) {
            for (String feature : features)
                out.print(instance.getFeature(feature) + ",");
            out.println(instance.getFeature("grade") < gradeThreshold ? 0 : 1);
        }

        out.close();
    }

}

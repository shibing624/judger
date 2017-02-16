package org.xm.judger.analyzer;

import org.xm.judger.domain.ENEssayInstance;
import org.xm.judger.features.english.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * 特征构建者
 * @author xuming
 */
public class ENFeatureBuilder {

    /**
     * normalization type for the zscore function
     */
    public enum NormType {
        BASIC,
        ABS,
        PROB
    }

    public static ArrayList<ENEssayInstance> buildFeatures(ArrayList<ENEssayInstance> instances) {
        ArrayList<ENFeatures> ENFeaturesArrayList = new ArrayList<>();
        ENFeaturesArrayList.add(new ENLengthFeature());
        ENFeatures wordLengthFeature = new ENAverageWordLengthFeature();
        ENFeaturesArrayList.add(wordLengthFeature);
        ENFeatures idfFeature = new ENIDFFeature(instances);
        ENFeaturesArrayList.add(idfFeature);
        ENFeatures coherenceFeature = new ENSentenceCoherenceFeature();
        ENFeaturesArrayList.add(coherenceFeature);
        // normalization
        ENFeaturesArrayList.add(new ENPercentMatchesFeature(","));
        ENFeaturesArrayList.add(new ENPercentMatchesFeature("!"));
        ENFeaturesArrayList.add(new ENPercentMatchesFeature("?"));
        ENFeatures theFeature = new ENPercentMatchesFeature("the");
        ENFeaturesArrayList.add(theFeature);
        ENFeaturesArrayList.add(new ENPercentMatchesFeature("is"));
        ENFeaturesArrayList.add(new ENPercentMatchesFeature("@.*", true));
        // need dictionary
        ENFeatures wordFeature = null;
        try {
            wordFeature = new ENWordFeature();
            ENFeaturesArrayList.add(new ENStopWordRatioFeature());
        } catch (IOException e) {
            System.err.println("Unable to load words: " + e);
        }
        ENFeaturesArrayList.add(wordFeature);
        // primary features
        for (ENEssayInstance instance : instances) {
            for (ENFeatures features : ENFeaturesArrayList)
                instance.setFeature(features.getFeatureScores(instance));
        }
        ArrayList<ENFeatures> normlizationFeatures = new ArrayList<>();
        normlizationFeatures.add(new ENMinMaxNormalizerFeature(instances, wordFeature, "OOVs"));
        normlizationFeatures.add(new ENMinMaxNormalizerFeature(instances, wordFeature, "obvious_typos"));
        normlizationFeatures.add(new ENMinMaxNormalizerFeature(instances, wordLengthFeature, "AverageWordLength"));
        normlizationFeatures.add(new ENMinMaxNormalizerFeature(instances, idfFeature, "AverageIDF"));
        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, idfFeature, "AverageIDF", ENGaussianNormailizerFeature.Type.ABS_ZSCORE));
        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, idfFeature, "AverageIDF", ENGaussianNormailizerFeature.Type.ZSCORE));

        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, coherenceFeature, "overlap_coherence", ENGaussianNormailizerFeature.Type.ZSCORE));
        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, coherenceFeature, "overlap_coherence", ENGaussianNormailizerFeature.Type.ABS_ZSCORE));

        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, theFeature, "PercentMatches_\\Qthe\\E", ENGaussianNormailizerFeature.Type.ZSCORE));
        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, theFeature, "PercentMatches_\\Qthe\\E", ENGaussianNormailizerFeature.Type.NORMAL_PROB));
        normlizationFeatures.add(new ENGaussianNormailizerFeature(instances, theFeature, "PercentMatches_\\Qthe\\E", ENGaussianNormailizerFeature.Type.ABS_ZSCORE));
        // compute normalization feature
        for (ENEssayInstance instance : instances) {
            for (ENFeatures feature : normlizationFeatures)
                instance.setFeature(feature.getFeatureScores(instance));
        }

        //analysis feature
        ENFeatureAnalyzer.analysis(instances);
        return instances;
    }

    public static void saveAllFeatures(ArrayList<ENEssayInstance> instances) {
        try {
            // generate an ARFF with real valued output class (for regression if possible)
            saveARFFRealClass(ENFeatureAnalyzer.filter(instances, 1), "data/training_essay1_real.arff");
            saveARFFDiscreteClass(ENFeatureAnalyzer.filter(instances, 1), "data/training_essay1_discrete.arff");
            // generate an ARFF where grade is turned into a binary feature based on the threshold (in this case over/under 8.5)
            saveARFFThresholdClass(ENFeatureAnalyzer.filter(instances, 1), "data/training_essay1_t8.5.arff", 8.5);
        } catch (IOException e) {
            System.err.println("Error saving ARFF: " + e);
        }
    }
    public static void saveTestFeatures(ArrayList<ENEssayInstance> instances) {
        try {
            saveARFFRealClass(ENFeatureAnalyzer.filter(instances, 1), "data/training_essay1_test_real.arff");
            saveARFFThresholdClass(ENFeatureAnalyzer.filter(instances, 1), "data/training_essay1_test_t8.5.arff", 8.5);
        } catch (IOException e) {
            System.err.println("Error saving ARFF: " + e);
        }
    }
    public static void saveFeatures(ArrayList<ENEssayInstance> instances,int set,String path) {
        try {
            saveARFFRealClass(ENFeatureAnalyzer.filter(instances, set), path);
        } catch (IOException e) {
            System.err.println("Error saving ARFF: " + e);
        }
    }

    /**
     * Save the data as an ARFF file where grade is specified as a real-valued feature.
     * This type of output class doesn't work with most of Weka.
     */
    public static void saveARFFRealClass(ArrayList<ENEssayInstance> instances, String filename) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(filename));
        printWriter.println("% Autogenerated by java saveARFFRealClass");
        printWriter.println("@relation " + filename);
        List<String> features = instances.get(0).listFeatures();
        for (String i : features)
            printWriter.println("@attribute " + arffEscapeName(i) + " real");
        printWriter.println("@attribute grade real");
        printWriter.println("@data");
        for (ENEssayInstance instance : instances) {
            for (String feature : features)
                printWriter.print(instance.getFeature(feature) + ",");
            printWriter.println(instance.getFeature("grade"));
        }
        printWriter.close();
    }

    public static String arffEscapeName(String name) {
        name = name.replaceAll("\\\\Q|\\\\E", "");    // strip \\Q \\E
        name = name.replaceAll("!", "exclamation_mark");
        name = name.replaceAll("\\?", "question_mark");
        name = name.replaceAll("\\.\\*", "dot_star");
        name = name.replaceAll(",", "comma");
        name = name.replaceAll("@", "at_sign");

        return name;
    }

    /**
     * Save the data as an ARFF file where grade is specified as a discrete feature.
     * This type of output class works with Weka, but the machine learning won't
     * take into account that it's really a continuous scale.
     */
    public static void saveARFFDiscreteClass(ArrayList<ENEssayInstance> instances, String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        out.println("% Auto generated by java saveARFFDiscreteClass");
        out.println("@relation training_essay_set_1");

        List<String> features = instances.get(0).listFeatures();
        for (String feature : features) {
            out.println("@attribute " + arffEscapeName(feature) + " real");
        }

        HashMap<Double, int[]> histogram = ENFeatureAnalyzer.buildHistogram(instances, "grade");
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
        for (ENEssayInstance instance : instances) {
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
    public static void saveARFFThresholdClass(ArrayList<ENEssayInstance> instances, String filename, double gradeThreshold) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        out.println("% Auto generated by java saveARFFThresholdClass");
        out.println("@relation training_essay_set_1");

        List<String> features = instances.get(0).listFeatures();
        for (String feature : features) {
            out.println("@attribute " + arffEscapeName(feature) + " real");
        }
        out.println("@attribute grade {0,1}");
        out.println("@data");
        for (ENEssayInstance instance : instances) {
            for (String feature : features)
                out.print(instance.getFeature(feature) + ",");
            out.println(instance.getFeature("grade") < gradeThreshold ? 0 : 1);
        }

        out.close();
    }

}

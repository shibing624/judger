package org.xm.judger;

import org.junit.Test;
import org.xm.judger.analyzer.ENFeatureBuilder;
import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;
import org.xm.judger.parser.ENEssayInstanceParser;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * @author xuming
 */
public class JudgerTest {
    @Test
    public void testEN() {
        ENEssayInstanceParser parser = new ENEssayInstanceParser();
        // Parse the input training file
        ArrayList<ENEssayInstance> instances = parser.parse(Config.ENTrainSetPath, true);
        Judger.setENInstances(instances);
        ENEssayInstance.printEssayInstances(instances, "data/examples.utf8");

        // Get feature Scores for each instance
        ArrayList<ENEssayInstance> instancesFeatures = ENFeatureBuilder.buildFeatures(instances);
        // Now we have all the instances and features
        // use any Machine Learning Tools (such as Weka)
        ENFeatureBuilder.saveAllFeatures(instancesFeatures);
    }

    @Test
    public void testEN_testFile() {
        ENEssayInstanceParser parser = new ENEssayInstanceParser();
        // Parse the input training file
        ArrayList<ENEssayInstance> instances = parser.parse(Config.ENTrainTestSetPath, true);
        Judger.setENInstances(instances);
        ENEssayInstance.printEssayInstances(instances, "data/examples_test.utf8");

        // Get feature Scores for each instance
        ArrayList<ENEssayInstance> instancesFeatures = ENFeatureBuilder.buildFeatures(instances);
        // Now we have all the instances and features
        // use any Machine Learning Tools (such as Weka)
        ENFeatureBuilder.saveTestFeatures(instancesFeatures);
    }


    /**
     * 利用训练集预测测试集的分类，批量处理
     *
     * @throws Exception
     */
    @Test
    public void testOutputClassDistribution() throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File("data/training_essay1_real.arff"));
        Instances train = loader.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);

        ArffLoader loader1 = new ArffLoader();
        loader1.setFile(new File("data/training_essay1_test_real.arff"));
        Instances test = loader1.getDataSet();
        test.setClassIndex(test.numAttributes() - 1);

        RandomForest classifier = new RandomForest();
        classifier.buildClassifier(train);
        System.out.println("num\t-\tfact\t-\tpred\t-\terr\t-\tdistribution");
        for (int i = 0; i < test.numInstances(); i++) {
            double pred = classifier.classifyInstance(test.instance(i));
            double[] dist = classifier.distributionForInstance(test.instance(i));
            StringBuilder sb = new StringBuilder();
            sb.append(i + 1)
                    .append(" - ")
                    .append(test.instance(i).toString(test.classIndex()))
                    .append(" - ")
                    .append(test.classAttribute().value((int) pred))
                    .append(" - ");
            if (pred != test.instance(i).classValue())
                sb.append("yes");
            else
                sb.append("no");
            sb.append(" - ");
            sb.append(Utils.arrayToString(dist));
            System.out.println(sb.toString());
        }
    }

    public void saveENARFF(String inputPath, String outputPath) {
        ENEssayInstanceParser parser = new ENEssayInstanceParser();
        // Parse the input training file
        ArrayList<ENEssayInstance> instances = parser.parse(inputPath, true);
        Judger.setENInstances(instances);
        ENEssayInstance.printEssayInstances(instances, "data/examples.utf8");

        // Get feature Scores for each instance
        ArrayList<ENEssayInstance> instancesFeatures = ENFeatureBuilder.buildFeatures(instances);
        // Now we have all the instances and features
        // use any Machine Learning Tools (such as Weka)
        ENFeatureBuilder.saveFeatures(instancesFeatures, 1, outputPath);
    }

    @Test
    public void testENClassifierNew() throws Exception {
        String trainSetPath = "Phase1/training_set_rel3.tsv";
        String saveTrainFeaturesPath = "data/training_real.arff";

        String testSetPath = "Phase1/training_set_rel4.tsv";
        String saveTestFeaturesPath = "data/test_real.arff";

        saveENARFF(trainSetPath, saveTrainFeaturesPath);
        saveENARFF(testSetPath, saveTestFeaturesPath);

        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(saveTrainFeaturesPath));
        Instances train = loader.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);

        ArffLoader loader1 = new ArffLoader();
        loader1.setFile(new File(saveTestFeaturesPath));
        Instances test = loader1.getDataSet();
        test.setClassIndex(test.numAttributes() - 1);

        RandomForest classifier = new RandomForest();
        classifier.buildClassifier(train);
        System.out.println("num\t- fact\t- pred\t- err\t- distribution");
        for (int i = 0; i < test.numInstances(); i++) {
            double pred = classifier.classifyInstance(test.instance(i));
            double[] dist = classifier.distributionForInstance(test.instance(i));
            StringBuilder sb = new StringBuilder();
            sb.append(i + 1)
                    .append(" - ")
                    .append(test.instance(i).toString(test.classIndex()))
                    .append(" - ")
                    .append((int) pred)
                    .append(" - ");
            if (pred != test.instance(i).classValue())
                sb.append("yes");
            else
                sb.append("no");
            sb.append(" - ");
            sb.append(Utils.arrayToString(dist));
            System.out.println(sb.toString());
        }
    }
}
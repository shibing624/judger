package org.xm.judger;

import org.junit.Test;
import org.xm.judger.analyzer.CNFeatureBuilder;
import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.judger.parser.CNEssayInstanceParser;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * @author xuming
 */
public class JudgerCNTest {
    @Test
    public void testCN() {
        CNEssayInstanceParser parser = new CNEssayInstanceParser();
        ArrayList<CNEssayInstance> instances = parser.parse(Config.CNTrainSetPath, true);
        Judger.setCNInstances(instances);
        CNEssayInstance.printEssayInstances(instances, "data/cn_examples.utf8");
        ArrayList<CNEssayInstance> instancesFeatures = CNFeatureBuilder.buildFeatures(instances,4);
        CNFeatureBuilder.saveAllFeatures(instancesFeatures,4);
    }

    public void saveCNARFF(String inputPath,String outputPath){
        CNEssayInstanceParser parser = new CNEssayInstanceParser();
        // Parse the input training file
        ArrayList<CNEssayInstance> instances = parser.parse(inputPath, true);
        Judger.setCNInstances(instances);
        CNEssayInstance.printEssayInstances(instances, "data/examples.utf8");

        // Get feature Scores for each instance
        ArrayList<CNEssayInstance> instancesFeatures = CNFeatureBuilder.buildFeatures(instances,4);
        // Now we have all the instances and features
        // use any Machine Learning Tools (such as Weka)
        CNFeatureBuilder.saveFeatures(instancesFeatures,4,outputPath);
    }

    @Test
    public void testCNClassifierNew() throws Exception {
        String trainSetPath = "Phase1/cn_training_set_rel1.tsv";
        String saveTrainFeaturesPath = "data/cn_training_real.arff";

        String testSetPath = "Phase1/cn_training_set_rel1.test.tsv";
        String saveTestFeaturesPath = "data/cn_test_real.arff";

        saveCNARFF(trainSetPath,saveTrainFeaturesPath);
        saveCNARFF(testSetPath,saveTestFeaturesPath);

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
        System.out.println("num\t-\tfact\t-\tpred\t-\terr\t-\tdistribution");
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

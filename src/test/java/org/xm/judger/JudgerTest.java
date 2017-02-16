package org.xm.judger;

import org.junit.Test;
import org.xm.judger.analyzer.CNFeatureBuilder;
import org.xm.judger.analyzer.ENFeatureBuilder;
import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.domain.Config;
import org.xm.judger.domain.ENEssayInstance;
import org.xm.judger.parser.CNEssayInstanceParser;
import org.xm.judger.parser.ENEssayInstanceParser;

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

    @Test
    public void testCN() {
        CNEssayInstanceParser parser = new CNEssayInstanceParser();
        ArrayList<CNEssayInstance> instances = parser.parse(Config.CNTrainSetPath, true);
        Judger.setCNInstances(instances);
        CNEssayInstance.printEssayInstances(instances, "data/cn_examples.utf8");
        ArrayList<CNEssayInstance> instancesFeatures = CNFeatureBuilder.buildFeatures(instances,4);
        CNFeatureBuilder.saveAllFeatures(instancesFeatures,4);
    }

}
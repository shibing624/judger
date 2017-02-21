package org.xm.judger;

import org.junit.Test;
import org.xm.judger.analyzer.FeatureHandler;
import org.xm.judger.domain.CNEssayInstance;
import org.xm.judger.parser.CNEssayInstanceParser;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author xuming
 */
public class AnalysisEssayTest {
    @Test
    public void testCN() throws IOException {
        String trainSetPath = "data/jinyong";
        String saveTrainFeaturesPath = "data/jinyong_training_result.arff";

        CNEssayInstanceParser parser = new CNEssayInstanceParser();
        // Parse the input training file
        ArrayList<CNEssayInstance> instances = parser.load(trainSetPath);
        Judger.setCNInstances(instances);

        // Get feature Scores for each instance
        ArrayList<CNEssayInstance> instancesFeatures = FeatureHandler.getFeatures(instances);
        // Now we have all the instances and features
        // use any Machine Learning Tools (such as Weka)
        FeatureHandler.saveFeatures(instancesFeatures, saveTrainFeaturesPath);
    }

    @Test
    public void testCN_bajin() throws IOException {
        String trainSetPath = "data/bajin/A";
        String saveTrainFeaturesPath = "data/bajin_novels_A_features.arff";

        CNEssayInstanceParser parser = new CNEssayInstanceParser();
        // Parse the input training file
        ArrayList<CNEssayInstance> instances = parser.load(trainSetPath);
        Judger.setCNInstances(instances);

        ArrayList<CNEssayInstance> instancesFeatures = FeatureHandler.getFeatures(instances);
        FeatureHandler.saveFeatures(instancesFeatures, saveTrainFeaturesPath);
    }
}

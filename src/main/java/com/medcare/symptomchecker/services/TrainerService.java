package com.medcare.symptomchecker.services;

import com.medcare.symptomchecker.utils.DataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.Random;

@Slf4j
@Service
public class TrainerService {
    DataLoader dataLoader;

    @Autowired
    public TrainerService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void trainModel(String dataSetPath, String modelPath, int classIndex) throws Exception {
        Instances data = dataLoader.loadFromCSV(dataSetPath);

        ReplaceMissingValues replaceMissingValues = new ReplaceMissingValues();
        replaceMissingValues.setInputFormat(data);
        Filter.useFilter(data, replaceMissingValues);

        data.setClassIndex(classIndex);

        // Train J48 decision tree classifier on the data
        J48 classifier = new J48();
        classifier.buildClassifier(data);

        evaluate(classifier, data);
        saveModel(classifier, modelPath);
    }

    private void evaluate(J48 classifier, Instances data) throws Exception {
        // Print the classifier's model
        log.info(String.valueOf(classifier));

        // Evaluate the classifier
        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(classifier, data, 10, new Random(1));

        log.info(evaluation.toSummaryString());
        log.info("Accuracy: " + evaluation.pctCorrect() + "%");
    }

    private void saveModel(J48 classifier, String path) {
        try {
            SerializationHelper.write(path, classifier);
            log.info("Model saved to " + path);
        } catch (Exception e) {
            log.error("Error saving model: " + e.getMessage());
        }
    }
}

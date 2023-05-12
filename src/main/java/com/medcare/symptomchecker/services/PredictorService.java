package com.medcare.symptomchecker.services;

import com.medcare.symptomchecker.utils.DataLoader;
import com.medcare.symptomchecker.utils.Preloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PredictorService {

    String dataSetSymptoms;
    String modelSymptoms;

    Preloader preloader;

    DataLoader dataLoader;

    @Autowired
    public PredictorService(Preloader preloader,
                            DataLoader dataLoader,
                            @Value("${dataset.symptoms}") String dataSetSymptoms,
                            @Value("${model.symptoms}") String modelSymptoms) {
        this.preloader = preloader;
        this.dataLoader = dataLoader;
        this.dataSetSymptoms = dataSetSymptoms;
        this.modelSymptoms = modelSymptoms;
    }

    public String predictDisease(List<String> symptoms) throws Exception {
        Instances data = dataLoader.loadFromCSV(dataSetSymptoms);

        J48 classifier;
        try {
            classifier = (J48) SerializationHelper.read(modelSymptoms);
            log.info("Model loaded from " + modelSymptoms);
        } catch (Exception e) {
            log.error("Error loading model: " + e.getMessage());
            throw new Exception("Error loading model: " + e.getMessage());
        }

        DenseInstance patient = new DenseInstance(data.numAttributes());
        patient.setDataset(data);

        Map<String, List<Integer>> symptomIndexes = new HashMap<>();

        for (String symptom : symptoms) {
            List<Integer> keys = new ArrayList<>();
            for (Map.Entry<Integer, List<String>> entry : preloader.getIndexSymptomsMap().entrySet()) {
                if (entry.getValue().contains(symptom)) {
                    keys.add(entry.getKey());
                }
                symptomIndexes.put(symptom, keys);
            }
        }

        List<Integer> possibleIndexes = IntStream.rangeClosed(1, 17).boxed().toList();
        Map<String, Integer> indexUsedMap = new HashMap<>();

        for (String symptom : symptoms) {
            List<Integer> possibleSymptomIndexes = symptomIndexes.get(symptom);
            int indexToUse = 0;
            for (Integer possibleIndex : possibleIndexes) {
                if (possibleSymptomIndexes.contains(possibleIndex) && !indexUsedMap.containsValue(possibleIndex)) {
                    indexToUse = possibleIndex;
                    indexUsedMap.put(symptom, indexToUse);
                    break;
                }
            }
            patient.setValue(indexToUse, symptom);
        }

        double disease = classifier.classifyInstance(patient);

        return data.classAttribute().value((int) disease);
    }
}

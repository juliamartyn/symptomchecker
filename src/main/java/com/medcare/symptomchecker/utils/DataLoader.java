package com.medcare.symptomchecker.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;

@Slf4j
@Component
public class DataLoader {

    public Instances loadFromCSV(String filePath) throws Exception {
        Instances data;

        CSVLoader loader = new CSVLoader();
        loader.setFile(new File(filePath));
        try {
            data = loader.getDataSet();
        } catch (Exception e) {
            log.error("Error loading data: " + e.getMessage());
            throw new Exception("Error loading model: " + e.getMessage());
        }

        // Set index to the attribute 'Disease'
        data.setClassIndex(0);

        return data;
    }
}

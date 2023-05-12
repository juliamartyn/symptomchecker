package com.medcare.symptomchecker.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Preloader {

    Map<Integer, List<String>> indexSymptomsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/dataset_ukrainian.csv"))) {
            String line = br.readLine(); // skip the header line
            line = br.readLine(); // read the first data line
            while (line != null) {
                String[] parts = line.split(",");
                for (int i = 1; i < parts.length; i++) {
                    List<String> symptoms = indexSymptomsMap.computeIfAbsent(i, k -> new ArrayList<>());
                    String symptom = parts[i];
                    if (!symptoms.contains(symptom)) {
                        symptoms.add(symptom);
                    }
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error loading data: " + e.getMessage());
        }
    }

    public Map<Integer, List<String>> getIndexSymptomsMap() {
        return indexSymptomsMap;
    }
}

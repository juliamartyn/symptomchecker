package com.medcare.symptomchecker.controllers;

import com.medcare.symptomchecker.services.TrainerService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    String dataSetSymptoms;
    String modelSymptoms;

    TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService,
                             @Value("${dataset.symptoms}") String dataSetSymptoms,
                             @Value("${model.symptoms}") String modelSymptoms) {
        this.trainerService = trainerService;
        this.dataSetSymptoms = dataSetSymptoms;
        this.modelSymptoms = modelSymptoms;
    }

    @GetMapping("/symptoms")
    public void trainSymptomsModel() throws Exception {
        trainerService.trainModel(dataSetSymptoms, modelSymptoms, 0);
    }
}

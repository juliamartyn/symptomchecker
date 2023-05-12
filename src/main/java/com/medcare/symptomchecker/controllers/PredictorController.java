package com.medcare.symptomchecker.controllers;

import com.medcare.symptomchecker.services.PredictorService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/predictor")
public class PredictorController {

    PredictorService predictorService;

    @Autowired
    public PredictorController(PredictorService predictorService) {
        this.predictorService = predictorService;
    }

    @GetMapping("/disease")
    public String predictDisease(@RequestParam("symptom") List<String> symptoms) throws Exception {
        return predictorService.predictDisease(symptoms);
    }
}

package com.love.portfolio.controller;

import com.love.portfolio.service.ChemistryAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chemistry")
@CrossOrigin(origins = "*")
public class ChemistryAIController {

    @Autowired
    private ChemistryAIService chemistryAIService;

    @PostMapping("/solve")
    public String solveEquation(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        return chemistryAIService.solveChemistry(prompt);
    }

    @PostMapping("/generate-quiz")
    public String generateQuiz(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        return chemistryAIService.generateQuiz(topic);
    }
}

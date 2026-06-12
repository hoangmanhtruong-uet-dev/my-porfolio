package com.love.portfolio.controller;

import com.love.portfolio.model.GoalProgress;
import com.love.portfolio.repository.GoalProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")

public class GoalProgressController {

    @Autowired
    private GoalProgressRepository repo;

    // GET toàn bộ tiến độ (trả về map goalId -> data)
    @GetMapping
    public List<GoalProgress> getAll() {
        return repo.findAll();
    }

    // PUT upsert tiến độ của 1 goal (tạo mới nếu chưa có, update nếu đã có)
    @PutMapping("/{goalId}")
    public GoalProgress upsert(@PathVariable Integer goalId,
                                @RequestBody GoalProgress incoming) {
        incoming.setGoalId(goalId);
        return repo.save(incoming);
    }

    // PUT batch — lưu nhiều goals cùng lúc
    @PutMapping("/batch")
    public List<GoalProgress> batchSave(@RequestBody List<GoalProgress> goals) {
        return repo.saveAll(goals);
    }
}

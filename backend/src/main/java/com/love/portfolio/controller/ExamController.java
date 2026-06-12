package com.love.portfolio.controller;

import com.love.portfolio.model.Exam;
import com.love.portfolio.model.Question;
import com.love.portfolio.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")

public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    // GET tất cả đề thi (kèm câu hỏi)
    @GetMapping
    public List<Exam> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        // Deserialize questions cho mỗi exam
        exams.forEach(Exam::deserializeQuestions);
        return exams;
    }

    // GET 1 đề thi theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable Long id) {
        return examRepository.findById(id)
                .map(exam -> {
                    exam.deserializeQuestions();
                    return ResponseEntity.ok(exam);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // POST tạo đề thi mới
    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        exam.serializeQuestions();
        Exam saved = examRepository.save(exam);
        saved.deserializeQuestions();
        return saved;
    }

    // PUT cập nhật thông tin đề thi (title, time)
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody Exam updated) {
        return examRepository.findById(id)
                .map(exam -> {
                    exam.setTitle(updated.getTitle());
                    exam.setTime(updated.getTime());
                    exam.serializeQuestions();
                    Exam saved = examRepository.save(exam);
                    saved.deserializeQuestions();
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE xóa đề thi
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (!examRepository.existsById(id)) return ResponseEntity.notFound().build();
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // PUT cập nhật toàn bộ danh sách câu hỏi của 1 đề
    @PutMapping("/{id}/questions")
    public ResponseEntity<Exam> updateQuestions(@PathVariable Long id,
                                                 @RequestBody List<Question> questions) {
        return examRepository.findById(id)
                .map(exam -> {
                    exam.setQuestions(questions);
                    exam.serializeQuestions();
                    Exam saved = examRepository.save(exam);
                    saved.deserializeQuestions();
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

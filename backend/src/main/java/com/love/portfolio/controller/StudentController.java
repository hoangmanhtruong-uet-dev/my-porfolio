package com.love.portfolio.controller;

import com.love.portfolio.model.Student;
import com.love.portfolio.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/students")

public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student student) {
        if (studentRepository.findByUsername(student.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
        }
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Student student) {
        Optional<Student> foundStudent = studentRepository.findByUsername(student.getUsername());
        if (foundStudent.isPresent()) {
            if (foundStudent.get().getPassword().equals(student.getPassword())) {
                return ResponseEntity.ok(foundStudent.get());
            } else {
                return ResponseEntity.status(401).body("Sai mật khẩu!");
            }
        }
        return ResponseEntity.status(404).body("NOT_FOUND");
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        return studentRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save-quiz")
    public ResponseEntity<?> saveQuiz(@RequestBody QuizResultRequest request) {
        Optional<Student> optStudent = studentRepository.findByUsername(request.getUsername());
        if (optStudent.isPresent()) {
            Student student = optStudent.get();
            
            // Cập nhật thống kê
            int oldCount = student.getQuizCount() != null ? student.getQuizCount() : 0;
            double oldAvg = student.getAvgScore() != null ? student.getAvgScore() : 0.0;
            
            int newCount = oldCount + 1;
            double newAvg = ((oldAvg * oldCount) + request.getScore()) / newCount;
            
            student.setQuizCount(newCount);
            student.setAvgScore(newAvg);
            
            // Cập nhật lịch sử (lưu dạng JSON string)
            String historyJson = String.format("{\"examTitle\":\"%s\",\"score\":%.1f,\"date\":\"%s\"}", 
                    request.getExamTitle(), request.getScore(), new java.util.Date().toString());
            
            String currentHistory = student.getQuizHistory();
            if (currentHistory == null || currentHistory.isEmpty() || currentHistory.equals("[]")) {
                student.setQuizHistory("[" + historyJson + "]");
            } else {
                // Thêm vào chuỗi JSON array hiện tại
                student.setQuizHistory(currentHistory.substring(0, currentHistory.length() - 1) + "," + historyJson + "]");
            }
            
            return ResponseEntity.ok(studentRepository.save(student));
        }
        return ResponseEntity.notFound().build();
    }

    // DTO class for request
    public static class QuizResultRequest {
        private String username;
        private String examTitle;
        private Double score;
        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getExamTitle() { return examTitle; }
        public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<java.util.List<Student>> getLeaderboard() {
        return ResponseEntity.ok(studentRepository.findAll().stream()
                .sorted((s1, s2) -> Double.compare(s2.getAvgScore(), s1.getAvgScore()))
                .limit(5)
                .toList());
    }
}

package com.love.portfolio.controller;

import com.love.portfolio.model.TutorClass;
import com.love.portfolio.repository.TutorClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")

public class TutorClassController {

    @Autowired
    private TutorClassRepository repo;

    // GET tất cả lớp (sắp xếp theo ngày tạo)
    @GetMapping
    public List<TutorClass> getAll() {
        return repo.findAllByOrderByCreatedAtAsc();
    }

    // GET một lớp
    @GetMapping("/{id}")
    public ResponseEntity<TutorClass> getOne(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST tạo lớp mới
    @PostMapping
    public TutorClass create(@RequestBody TutorClass cls) {
        cls.setId(null); // đảm bảo auto-increment
        return repo.save(cls);
    }

    // PUT cập nhật toàn bộ
    @PutMapping("/{id}")
    public ResponseEntity<TutorClass> update(@PathVariable Long id,
                                              @RequestBody TutorClass cls) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        cls.setId(id);
        return ResponseEntity.ok(repo.save(cls));
    }

    // PATCH điểm danh +1 buổi
    @PatchMapping("/{id}/attend")
    public ResponseEntity<TutorClass> attend(@PathVariable Long id) {
        return repo.findById(id).map(cls -> {
            if (cls.getCompleted() < cls.getTotal()) {
                cls.setCompleted(cls.getCompleted() + 1);
                // Tự động đánh dấu done khi hoàn thành lộ trình
                if (cls.getCompleted() >= cls.getTotal()) {
                    cls.setStatus("done");
                }
            }
            return ResponseEntity.ok(repo.save(cls));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE xóa lớp
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

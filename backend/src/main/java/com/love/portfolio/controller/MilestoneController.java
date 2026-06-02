package com.love.portfolio.controller;

import com.love.portfolio.model.Milestone;
import com.love.portfolio.repository.MilestoneRepository;
import com.love.portfolio.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/milestones")
@CrossOrigin(origins = "*")
public class MilestoneController {

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public List<Milestone> getAllMilestones() {
        return milestoneRepository.findAll();
    }

    @PostMapping
    public Milestone saveMilestone(
            @RequestParam("date") String date,
            @RequestParam("title") String title,
            @RequestParam("desc") String desc,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        List<String> imageUrls = uploadFiles(files);

        Milestone milestone = new Milestone();
        milestone.setDate(date);
        milestone.setTitle(title);
        milestone.setDescription(desc);
        milestone.setImages(imageUrls);

        return milestoneRepository.save(milestone);
    }

    @PutMapping("/{id}")
    public Milestone updateMilestone(
            @PathVariable Long id,
            @RequestParam("date") String date,
            @RequestParam("title") String title,
            @RequestParam("desc") String desc,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỉ niệm với id: " + id));

        milestone.setDate(date);
        milestone.setTitle(title);
        milestone.setDescription(desc);

        // Chỉ thay ảnh nếu có file mới gửi lên
        if (files != null && files.length > 0) {
            milestone.setImages(uploadFiles(files));
        }

        return milestoneRepository.save(milestone);
    }

    @DeleteMapping("/{id}")
    public void deleteMilestone(@PathVariable Long id) {
        milestoneRepository.deleteById(id);
    }

    /**
     * Upload danh sách file lên Cloudinary, trả về list URL
     */
    private List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadImage(file, "milestones");
                    imageUrls.add(url); // URL https://res.cloudinary.com/...
                }
            }
        }
        return imageUrls;
    }
}

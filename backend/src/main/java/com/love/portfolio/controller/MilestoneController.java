package com.love.portfolio.controller;

import com.love.portfolio.model.Milestone;
import com.love.portfolio.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/milestones")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API
public class MilestoneController {

    @Autowired
    private MilestoneRepository milestoneRepository;

    private final String UPLOAD_DIR = "uploads/";

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

        List<String> imageUrls = saveFiles(files);

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

        // Only replace images if new files are provided
        if (files != null && files.length > 0) {
            milestone.setImages(saveFiles(files));
        }

        return milestoneRepository.save(milestone);
    }

    @DeleteMapping("/{id}")
    public void deleteMilestone(@PathVariable Long id) {
        milestoneRepository.deleteById(id);
    }

    private List<String> saveFiles(MultipartFile[] files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        if (files != null) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath);
                    imageUrls.add("/uploads/" + fileName);
                }
            }
        }
        return imageUrls;
    }
}

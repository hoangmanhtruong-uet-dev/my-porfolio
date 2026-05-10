package com.love.portfolio.controller;

import com.love.portfolio.model.LocketMoment;
import com.love.portfolio.repository.LocketMomentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locket")
@CrossOrigin(origins = "*")
public class LocketController {

    @Autowired
    private LocketMomentRepository locketMomentRepository;


    @GetMapping
    public List<LocketMoment> getAllMoments() {
        return locketMomentRepository.findAll();
    }

    private Path getUploadPath() {
        Path path = Paths.get("uploads", "locket");
        if (!Files.exists(path.getParent()) && Files.exists(Paths.get("..", "uploads"))) {
            path = Paths.get("..", "uploads", "locket");
        }
        return path;
    }

    @PostMapping
    public LocketMoment sendMoment(
            @RequestParam("caption") String caption,
            @RequestParam("file") MultipartFile file) throws IOException {

        Path uploadPath = getUploadPath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        LocketMoment moment = new LocketMoment();
        moment.setCaption(caption);
        moment.setImageUrl("/uploads/locket/" + fileName);

        return locketMomentRepository.save(moment);
    }
}

package com.love.portfolio.controller;

import com.love.portfolio.model.LocketMoment;
import com.love.portfolio.repository.LocketMomentRepository;
import com.love.portfolio.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/locket")

public class LocketController {

    @Autowired
    private LocketMomentRepository locketMomentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public List<LocketMoment> getAllMoments() {
        return locketMomentRepository.findAll();
    }

    @PostMapping
    public LocketMoment sendMoment(
            @RequestParam("caption") String caption,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Upload lên Cloudinary thay vì lưu local
        String imageUrl = cloudinaryService.uploadImage(file, "locket");

        LocketMoment moment = new LocketMoment();
        moment.setCaption(caption);
        moment.setImageUrl(imageUrl); // URL https://res.cloudinary.com/...

        return locketMomentRepository.save(moment);
    }
}

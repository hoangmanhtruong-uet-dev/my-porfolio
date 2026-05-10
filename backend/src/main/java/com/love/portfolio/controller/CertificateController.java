package com.love.portfolio.controller;

import com.love.portfolio.model.Certificate;
import com.love.portfolio.repository.CertificateRepository;
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
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {

    @Autowired
    private CertificateRepository certificateRepository;

    private final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @PostMapping
    public Certificate uploadCertificate(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("file") MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        Certificate certificate = new Certificate();
        certificate.setTitle(title != null ? title : "Chứng nhận");
        certificate.setImageUrl("/uploads/" + fileName);

        return certificateRepository.save(certificate);
    }

    @DeleteMapping("/{id}")
    public void deleteCertificate(@PathVariable long id) {
        certificateRepository.deleteById(id);
    }
}

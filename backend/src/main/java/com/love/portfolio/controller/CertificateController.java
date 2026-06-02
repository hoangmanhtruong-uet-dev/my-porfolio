package com.love.portfolio.controller;

import com.love.portfolio.model.Certificate;
import com.love.portfolio.repository.CertificateRepository;
import com.love.portfolio.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @PostMapping
    public Certificate uploadCertificate(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Upload lên Cloudinary thay vì lưu local
        String imageUrl = cloudinaryService.uploadImage(file, "certificates");

        Certificate certificate = new Certificate();
        certificate.setTitle(title != null ? title : "Chứng nhận");
        certificate.setImageUrl(imageUrl); // URL https://res.cloudinary.com/...

        return certificateRepository.save(certificate);
    }

    @DeleteMapping("/{id}")
    public void deleteCertificate(@PathVariable long id) {
        certificateRepository.deleteById(id);
    }
}

package com.love.portfolio.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Upload một file ảnh lên Cloudinary, trả về URL https://res.cloudinary.com/...
     *
     * @param file   file ảnh từ MultipartFile
     * @param folder thư mục lưu trên Cloudinary (vd: "locket", "milestones", "certificates")
     * @return URL công khai của ảnh
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", folder)
        );
        return (String) result.get("secure_url");
    }

    /**
     * Xóa ảnh trên Cloudinary theo public_id
     * (Dùng khi cần xóa ảnh cũ khi update hoặc delete record)
     *
     * @param publicId public_id của ảnh trên Cloudinary
     */
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}

package com.love.portfolio.controller;

import com.love.portfolio.model.CnsProgress;
import com.love.portfolio.model.CnsPdf;
import com.love.portfolio.repository.CnsProgressRepository;
import com.love.portfolio.repository.CnsPdfRepository;
import com.love.portfolio.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API cho tính năng CNS (Chứng nhận số) – lưu tiến độ bước và ảnh minh chứng.
 *
 * Endpoints:
 *   GET    /api/cns/{lessonId}              → lấy tất cả bước của 1 bài
 *   PATCH  /api/cns/{lessonId}/{stepNum}    → toggle done / cập nhật
 *   POST   /api/cns/{lessonId}/{stepNum}/image  → upload ảnh minh chứng
 *   DELETE /api/cns/{lessonId}/{stepNum}/image  → xóa ảnh
 *   DELETE /api/cns/{lessonId}              → reset toàn bộ bài
 */
@RestController
@RequestMapping("/api/cns")
@CrossOrigin(origins = "*")
public class CnsController {

    @Autowired
    private CnsProgressRepository repo;

    @Autowired
    private CnsPdfRepository pdfRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ── GET: lấy tiến độ toàn bộ bài ─────────────────────────────────────────
    @GetMapping("/{lessonId}")
    public List<CnsProgress> getLesson(@PathVariable String lessonId) {
        return repo.findByLessonId(lessonId);
    }

    // ── PATCH: toggle done cho 1 bước ────────────────────────────────────────
    @PatchMapping("/{lessonId}/{stepNum}")
    public CnsProgress toggleDone(@PathVariable String lessonId,
                                   @PathVariable int stepNum,
                                   @RequestBody Map<String, Object> body) {
        CnsProgress p = repo.findByLessonIdAndStepNum(lessonId, stepNum)
                .orElseGet(() -> {
                    CnsProgress np = new CnsProgress();
                    np.setLessonId(lessonId);
                    np.setStepNum(stepNum);
                    return np;
                });

        if (body.containsKey("done"))
            p.setDone(Boolean.parseBoolean(body.get("done").toString()));

        return repo.save(p);
    }

    // ── POST: upload ảnh minh chứng lên Cloudinary ───────────────────────────
    @PostMapping("/{lessonId}/{stepNum}/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable String lessonId,
            @PathVariable int stepNum,
            @RequestParam("file") MultipartFile file) throws IOException {

        String url = cloudinaryService.uploadImage(file, "cns/" + lessonId);

        CnsProgress p = repo.findByLessonIdAndStepNum(lessonId, stepNum)
                .orElseGet(() -> {
                    CnsProgress np = new CnsProgress();
                    np.setLessonId(lessonId);
                    np.setStepNum(stepNum);
                    return np;
                });
        p.setImageUrl(url);
        repo.save(p);

        return ResponseEntity.ok(Map.of("imageUrl", url));
    }

    // ── DELETE: xóa ảnh của 1 bước ───────────────────────────────────────────
    @DeleteMapping("/{lessonId}/{stepNum}/image")
    public ResponseEntity<Void> removeImage(@PathVariable String lessonId,
                                             @PathVariable int stepNum) {
        repo.findByLessonIdAndStepNum(lessonId, stepNum).ifPresent(p -> {
            p.setImageUrl(null);
            repo.save(p);
        });
        return ResponseEntity.noContent().build();
    }

    // ── DELETE: reset toàn bộ bài ────────────────────────────────────────────
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> resetLesson(@PathVariable String lessonId) {
        List<CnsProgress> all = repo.findByLessonId(lessonId);
        repo.deleteAll(all);
        return ResponseEntity.noContent().build();
    }

    // ══ PDF Endpoints ═════════════════════════════════════════════════════════

    // ── GET: lấy tất cả PDF slots ─────────────────────────────────────────────
    @GetMapping("/pdf")
    public Map<String, Object> getAllPdfs() {
        Map<String, Object> result = new HashMap<>();
        pdfRepo.findAll().forEach(p -> {
            Map<String, String> info = new HashMap<>();
            info.put("viewUrl", "/api/cns/pdf/view/" + p.getSlotId());
            info.put("fileUrl", p.getFileUrl());
            info.put("fileName", p.getFileName());
            result.put(p.getSlotId(), info);
        });
        return result;
    }

    // ── GET: lấy thông tin 1 PDF slot ────────────────────────────────────────
    @GetMapping("/pdf/{slotId}")
    public ResponseEntity<Map<String, String>> getPdfSlot(@PathVariable String slotId) {
        return pdfRepo.findById(slotId)
            .map(p -> {
                Map<String, String> info = new HashMap<>();
                info.put("viewUrl",  "/api/cns/pdf/view/" + p.getSlotId());
                info.put("fileUrl",  p.getFileUrl());
                info.put("fileName", p.getFileName());
                return ResponseEntity.ok(info);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST: upload PDF cho 1 slot ───────────────────────────────────────────
    @PostMapping("/pdf/{slotId}")
    public ResponseEntity<Map<String, String>> uploadPdf(
            @PathVariable String slotId,
            @RequestParam("file") MultipartFile file) throws IOException {

        String url = cloudinaryService.uploadRaw(file, "cns/pdf", slotId);

        CnsPdf pdf = pdfRepo.findById(slotId).orElse(new CnsPdf());
        pdf.setSlotId(slotId);
        pdf.setFileUrl(url);
        pdf.setFileName(file.getOriginalFilename());
        pdfRepo.save(pdf);

        // Return both original cloud URL and a server-side view URL that streams with inline disposition
        String viewUrl = "/api/cns/pdf/view/" + slotId;
        Map<String, String> resp = new HashMap<>();
        resp.put("fileUrl", url);
        resp.put("viewUrl", viewUrl);
        resp.put("fileName", file.getOriginalFilename());
        return ResponseEntity.ok(resp);
    }

    // ── DELETE: xóa PDF của 1 slot ───────────────────────────────────────────
    @DeleteMapping("/pdf/{slotId}")
    public ResponseEntity<Void> deletePdf(@PathVariable String slotId) {
        pdfRepo.deleteById(slotId);
        return ResponseEntity.noContent().build();
    }

    // ── GET: proxy/view a PDF inline (streams with Content-Disposition: inline)
    @GetMapping("/pdf/view/{slotId}")
    public ResponseEntity<byte[]> viewPdfInline(@PathVariable String slotId) {
        var maybe = pdfRepo.findById(slotId);
        if (maybe.isEmpty()) return ResponseEntity.notFound().build();
        CnsPdf pdf = maybe.get();
        if (pdf.getFileUrl() == null) return ResponseEntity.notFound().build();

        RestTemplate rt = new RestTemplate();
        ResponseEntity<byte[]> resp;
        try {
            resp = rt.getForEntity(pdf.getFileUrl(), byte[].class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return ResponseEntity.status(resp.getStatusCode()).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        ContentDisposition cd = ContentDisposition.inline().filename(pdf.getFileName() == null ? "file.pdf" : pdf.getFileName()).build();
        headers.setContentDisposition(cd);
        return new ResponseEntity<>(resp.getBody(), headers, HttpStatus.OK);
    }
}

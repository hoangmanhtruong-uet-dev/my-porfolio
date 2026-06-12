package com.love.portfolio.controller;

import com.love.portfolio.model.BucketCategory;
import com.love.portfolio.model.BucketItem;
import com.love.portfolio.repository.BucketCategoryRepository;
import com.love.portfolio.repository.BucketItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bucket")

public class BucketController {

    @Autowired private BucketCategoryRepository catRepo;
    @Autowired private BucketItemRepository itemRepo;

    // ── GET tất cả categories (kèm items) ──────────────────────
    @GetMapping
    public List<BucketCategory> getAll() {
        return catRepo.findAllByOrderBySortOrderAscIdAsc();
    }

    // ── POST tạo category mới ──────────────────────────────────
    @PostMapping("/category")
    public BucketCategory createCategory(@RequestBody BucketCategory cat) {
        cat.setId(null);
        return catRepo.save(cat);
    }

    // ── DELETE xóa category (cascade xóa luôn items) ──────────
    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        catRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── POST thêm item vào category ────────────────────────────
    @PostMapping("/category/{catId}/item")
    public BucketItem addItem(@PathVariable Long catId,
                              @RequestBody BucketItem item) {
        BucketCategory cat = catRepo.findById(catId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + catId));
        item.setId(null);
        item.setCategory(cat);
        return itemRepo.save(item);
    }

    // ── PATCH toggle done / cập nhật item ──────────────────────
    @PatchMapping("/item/{id}")
    public BucketItem updateItem(@PathVariable Long id,
                                 @RequestBody Map<String, Object> patch) {
        BucketItem item = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));

        if (patch.containsKey("done"))
            item.setDone(Boolean.parseBoolean(patch.get("done").toString()));
        if (patch.containsKey("doneDate"))
            item.setDoneDate((String) patch.get("doneDate"));
        if (patch.containsKey("photo"))
            item.setPhoto((String) patch.get("photo"));

        return itemRepo.save(item);
    }

    // ── DELETE xóa item ────────────────────────────────────────
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── POST seed dữ liệu mặc định (gọi 1 lần khi DB trống) ───
    @PostMapping("/seed")
    public List<BucketCategory> seed() {
        if (catRepo.count() > 0) return catRepo.findAllByOrderBySortOrderAscIdAsc();

        String[][] defaults = {
            {"Du Lịch",  "fa-plane-departure", "Đi ngắm tuyết tại Sapa ❄️", "Check-in Đà Lạt 🌲"},
            {"Ẩm Thực",  "fa-utensils",        "Thử lẩu Haidilao 🍲",       "Tự nấu bữa tối 🍝"},
            {"Mục Tiêu", "fa-bullseye",        "Cùng tốt nghiệp UET 🎓",    "Nuôi một chú mèo 🐱"}
        };

        int order = 0;
        for (String[] d : defaults) {
            BucketCategory cat = new BucketCategory();
            cat.setTitle(d[0]);
            cat.setIcon(d[1]);
            cat.setSortOrder(order++);
            cat = catRepo.save(cat);

            for (int i = 2; i < d.length; i++) {
                BucketItem item = new BucketItem();
                item.setText(d[i]);
                item.setDone(false);
                item.setCategory(cat);
                itemRepo.save(item);
            }
        }
        return catRepo.findAllByOrderBySortOrderAscIdAsc();
    }
}

package com.love.portfolio.controller;

import com.love.portfolio.model.LocationUpdate;
import com.love.portfolio.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ─────────────────────────────────────────────────────────
    // WebSocket: client gửi lên /app/location.update
    // Server broadcast ra /topic/location cho tất cả subscriber
    // ─────────────────────────────────────────────────────────
    @MessageMapping("/location.update")
    @SendTo("/topic/location")
    public LocationUpdate handleLocationUpdate(LocationUpdate update) {
        locationService.update(update);
        return update;
    }

    // ─────────────────────────────────────────────────────────
    // REST fallback: GET vị trí hiện tại (dùng khi mới mở trang)
    // ─────────────────────────────────────────────────────────
    @GetMapping("/all")
    public Map<String, LocationUpdate> getAllLocations() {
        return locationService.getAll();
    }

    // REST fallback: POST vị trí (dùng khi WS chưa kết nối)
    @PostMapping("/update")
    public LocationUpdate postLocation(@RequestBody LocationUpdate update) {
        locationService.update(update);
        // Broadcast qua WebSocket luôn để các client khác nhận được
        messagingTemplate.convertAndSend("/topic/location", update);
        return update;
    }
}

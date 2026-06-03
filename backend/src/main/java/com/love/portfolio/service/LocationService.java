package com.love.portfolio.service;

import com.love.portfolio.model.LocationUpdate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Lưu vị trí mới nhất của mỗi người trong memory (in-process).
 * Không cần DB vì vị trí chỉ cần real-time, không cần persist.
 */
@Service
public class LocationService {

    private final Map<String, LocationUpdate> latest = new HashMap<>();

    public void update(LocationUpdate loc) {
        loc.setTimestamp(System.currentTimeMillis());
        latest.put(loc.getRole(), loc);
    }

    public LocationUpdate get(String role) {
        return latest.get(role);
    }

    public Map<String, LocationUpdate> getAll() {
        return Map.copyOf(latest);
    }
}

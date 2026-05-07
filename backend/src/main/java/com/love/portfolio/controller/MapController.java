package com.love.portfolio.controller;

import com.love.portfolio.model.LoveLocation;
import com.love.portfolio.repository.LoveLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class MapController {

    @Autowired
    private LoveLocationRepository loveLocationRepository;

    @GetMapping
    public List<LoveLocation> getAllLocations() {
        return loveLocationRepository.findAll();
    }

    @PostMapping
    public LoveLocation saveLocation(@RequestBody LoveLocation location) {
        return loveLocationRepository.save(location);
    }

    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable Long id) {
        loveLocationRepository.deleteById(id);
    }
}

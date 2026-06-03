package com.love.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdate {
    private String role;      // "male" hoặc "female"
    private double lat;
    private double lng;
    private long timestamp;   // ms
}

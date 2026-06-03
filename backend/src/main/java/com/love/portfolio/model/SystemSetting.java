package com.love.portfolio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SystemSetting {
    @Id
    private String settingKey; // e.g., "hero_image_url"
    private String settingValue;
}

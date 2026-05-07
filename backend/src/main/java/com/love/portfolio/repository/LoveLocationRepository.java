package com.love.portfolio.repository;

import com.love.portfolio.model.LoveLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoveLocationRepository extends JpaRepository<LoveLocation, Long> {
}

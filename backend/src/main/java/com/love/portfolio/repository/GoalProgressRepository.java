package com.love.portfolio.repository;

import com.love.portfolio.model.GoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalProgressRepository extends JpaRepository<GoalProgress, Integer> {
}

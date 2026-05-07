package com.love.portfolio.repository;

import com.love.portfolio.model.LocketMoment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocketMomentRepository extends JpaRepository<LocketMoment, Long> {
}

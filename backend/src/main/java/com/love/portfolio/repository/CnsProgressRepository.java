package com.love.portfolio.repository;

import com.love.portfolio.model.CnsProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CnsProgressRepository extends JpaRepository<CnsProgress, Long> {

    List<CnsProgress> findByLessonId(String lessonId);

    Optional<CnsProgress> findByLessonIdAndStepNum(String lessonId, int stepNum);
}

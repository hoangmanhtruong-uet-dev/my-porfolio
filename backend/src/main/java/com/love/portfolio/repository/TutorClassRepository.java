package com.love.portfolio.repository;

import com.love.portfolio.model.TutorClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TutorClassRepository extends JpaRepository<TutorClass, Long> {
    List<TutorClass> findAllByOrderByCreatedAtAsc();
}

package com.love.portfolio.repository;

import com.love.portfolio.model.CnsPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CnsPdfRepository extends JpaRepository<CnsPdf, String> {
}

package com.love.portfolio.repository;

import com.love.portfolio.model.BucketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BucketCategoryRepository extends JpaRepository<BucketCategory, Long> {
    List<BucketCategory> findAllByOrderBySortOrderAscIdAsc();
}

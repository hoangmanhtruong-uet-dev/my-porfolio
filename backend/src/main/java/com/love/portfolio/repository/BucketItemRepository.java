package com.love.portfolio.repository;

import com.love.portfolio.model.BucketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {
}

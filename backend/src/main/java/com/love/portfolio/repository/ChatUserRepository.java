package com.love.portfolio.repository;

import com.love.portfolio.model.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    Optional<ChatUser> findByRole(String role);
}
